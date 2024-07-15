package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.enums.orchestration.AssessmentResult;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.common.model.orchestration.common.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.LastOutcome;
import uk.gov.justice.laa.crime.common.model.orchestration.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static uk.gov.justice.laa.crime.util.DateUtil.toDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;
@Slf4j
@Component
@RequiredArgsConstructor
public class ContributionMapper extends CrownCourtMapper {

    public ApiMaatCalculateContributionRequest workflowRequestToMaatCalculateContributionRequest(
            WorkflowRequest workflowRequest) {

        log.info("ContributionMapper.workflowRequestToMaatCalculateContributionRequest()");
        ApiMaatCalculateContributionRequest request = null;

        try {
            UserDTO user = workflowRequest.getUserDTO();
            ApplicationDTO application = workflowRequest.getApplicationDTO();

            CrownCourtOverviewDTO crownCourtOverviewDTO = application.getCrownCourtOverviewDTO();
            ContributionsDTO contributionsDTO = crownCourtOverviewDTO.getContribution();
            FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
            IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = financialAssessmentDTO.getIncomeEvidence();
            HardshipOverviewDTO hardshipOverviewDTO = financialAssessmentDTO.getHardship();

            log.info("workflowRequestToMaatCalculateContributionRequest()");
            Collection<OutcomeDTO> outcomeDTOs = crownCourtOverviewDTO.getCrownCourtSummaryDTO().getOutcomeDTOs();
            String appealType = crownCourtOverviewDTO.getAppealDTO().getAppealTypeDTO().getCode();
            LocalDateTime effectiveDate = toLocalDateTime(contributionsDTO.getEffectiveDate() != null ? contributionsDTO.getEffectiveDate().getValue() : null);

            request = new ApiMaatCalculateContributionRequest()
                    .withUserCreated(user.getUserName())
                    .withRepId(NumberUtils.toInteger(application.getRepId()))
                    .withApplId(NumberUtils.toInteger(application.getApplicantDTO().getId()))
                    .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                    .withCommittalDate(toLocalDateTime(application.getCommittalDate()))
                    .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                    .withAssessments(applicationDtoToAssessments(application))
                    .withContributionCap(BigDecimal.valueOf(application.getOffenceDTO().getContributionCap()))
                    .withContributionId(NumberUtils.toInteger(contributionsDTO.getId()))
                    .withMonthlyContributions(contributionsDTO.getMonthlyContribs())
                    .withEffectiveDate(effectiveDate)
                    .withUpfrontContributions(contributionsDTO.getUpfrontContribs())
                    .withRemoveContributions(application.getStatusDTO().getRemoveContribs().toString())
                    .withMagCourtOutcome(application.getMagsOutcomeDTO() != null ?
                            MagCourtOutcome.getFrom(
                                    application.getMagsOutcomeDTO().getOutcome()) : null)
                    .withAppealType(appealType != null ? AppealType.getFrom(appealType) : null)
                    .withLastOutcome(getLastCrownCourtOutcome(outcomeDTOs))
                    .withCrownCourtOutcome(
                            crownCourtSummaryDtoToCrownCourtOutcomes(crownCourtOverviewDTO.getCrownCourtSummaryDTO())
                    )
                    .withDateUpliftApplied(toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftAppliedDate()))
                    .withDateUpliftRemoved(toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftRemovedDate()))
                    .withTotalAnnualDisposableIncome(
                            Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())
                                    ? BigDecimal.valueOf(financialAssessmentDTO.getFull().getTotalAnnualDisposableIncome())
                                    : null
                    );

            if (hardshipOverviewDTO.getCrownCourtHardship() != null) {
                request.withDisposableIncomeAfterCrownHardship(
                        hardshipOverviewDTO.getCrownCourtHardship().getDisposableIncomeAfterHardship());
            }

            if (hardshipOverviewDTO.getMagCourtHardship() != null) {
                request.withDisposableIncomeAfterMagHardship(
                        hardshipOverviewDTO.getMagCourtHardship().getDisposableIncomeAfterHardship());
            }
        } catch(Exception exception) {
            log.info("exception --" + exception.getMessage());
            log.error("exception", exception);
            exception.printStackTrace();
            throw new RuntimeException(exception);
        }

        return request;
    }

    private LastOutcome getLastCrownCourtOutcome(final Collection<OutcomeDTO> crownCourtOutcomeList) {
        return crownCourtOutcomeList.stream()
                .reduce((first, second) -> second)
                .filter(outcome ->  outcome.getOutComeType() != null && outcome.getOutComeType().equals(CrownCourtOutcomeType.APPEAL.getType()))
                .map(appealOutcome -> new LastOutcome()
                        .withDateSet(toLocalDateTime(appealOutcome.getDateSet()))
                        .withOutcome(CrownCourtAppealOutcome.getFrom(appealOutcome.getOutcome())))
                .orElse(null);
    }

    private List<ApiAssessment> applicationDtoToAssessments(final ApplicationDTO application) {
        List<ApiAssessment> assessmentList = new ArrayList<>();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        log.info("applicationDtoToAssessments.initialAssessmentDTO.status-->" + initialAssessmentDTO.getAssessmnentStatusDTO().getStatus());
        assessmentList.add(
                new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withResult(AssessmentResult.getFrom(initialAssessmentDTO.getResult()))
                        .withAssessmentDate(toLocalDateTime(initialAssessmentDTO.getAssessmentDate()))
                        .withNewWorkReason(NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()))
                        .withStatus( StringUtils.isNotBlank(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus()) ? CurrentStatus.getFrom(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                           : CurrentStatus.getFrom(CurrentStatus.IN_PROGRESS.getStatus()))
        );

        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        if (Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable()) && StringUtils.isNotBlank(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus())
            && StringUtils.isNotBlank(fullAssessmentDTO.getResult()) ) {
            log.info("applicationDtoToAssessments.fullAssessmentDTO.status-->" + fullAssessmentDTO.getAssessmnentStatusDTO().getStatus());
            assessmentList.add(
                    new ApiAssessment()
                            .withAssessmentType(AssessmentType.FULL)
                            .withResult(AssessmentResult.getFrom(fullAssessmentDTO.getResult()))
                            .withAssessmentDate(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()))
                            .withNewWorkReason(NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()))
                            .withStatus(StringUtils.isNotBlank(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus()) ? CurrentStatus.getFrom(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                                    : CurrentStatus.getFrom(CurrentStatus.IN_PROGRESS.getStatus()))
            );
        }

        PassportedDTO passported = application.getPassportedDTO();
        if (passported.getPassportedId() != null) {
            log.info("applicationDtoToAssessments.passported.status-->" + passported.getAssessementStatusDTO().getStatus());
            assessmentList.add(
                    new ApiAssessment()
                            .withAssessmentType(AssessmentType.PASSPORT)
                            .withResult(AssessmentResult.getFrom(passported.getResult()))
                            .withAssessmentDate(toLocalDateTime(passported.getDate()))
                            .withNewWorkReason(NewWorkReason.getFrom(passported.getNewWorkReason().getCode()))
                            .withStatus(CurrentStatus.getFrom(passported.getAssessementStatusDTO().getStatus()))
            );
        }
        log.info("applicationDtoToAssessments.assessmentList-->" + assessmentList);
        return assessmentList;
    }

    public ApiMaatCheckContributionRuleRequest applicationDtoToCheckContributionRuleRequest(
            ApplicationDTO application) {

        CrownCourtSummaryDTO crownCourtSummary = application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        return new ApiMaatCheckContributionRuleRequest()
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withCrownCourtOutcome(crownCourtSummaryDtoToCrownCourtOutcomes(crownCourtSummary));
    }

    public ContributionsDTO maatCalculateContributionResponseToContributionsDto(
            ApiMaatCalculateContributionResponse response) {

        return ContributionsDTO.builder()
                .id(response.getContributionId().longValue())
                .capped(response.getContributionCap())
                .calcDate(new SysGenDate(toDate(response.getCalcDate())))
                .basedOn(new SysGenString(response.getBasedOn()))
                .monthlyContribs(response.getMonthlyContributions())
                .upfrontContribs(response.getUpfrontContributions())
                .effectiveDate(new SysGenDate(toDate(response.getEffectiveDate())))
                .upliftApplied("Y".equalsIgnoreCase(response.getUpliftApplied()))
                .build();
    }

    public Collection<ContributionSummaryDTO> contributionSummaryToDto(List<ApiContributionSummary> contributionSummaries) {
        Collection<ContributionSummaryDTO> contributionSummaryCollection = new ArrayList<>();
        if (null != contributionSummaries) {
            for (ApiContributionSummary apiContributionSummary : contributionSummaries) {
                ContributionSummaryDTO contributionSummaryDTO = new ContributionSummaryDTO();
                contributionSummaryDTO.setId(Long.valueOf(apiContributionSummary.getId()));
                contributionSummaryDTO.setMonthlyContribs(apiContributionSummary.getMonthlyContributions().doubleValue());
                contributionSummaryDTO.setUpfrontContribs(apiContributionSummary.getUpfrontContributions().doubleValue());
                contributionSummaryDTO.setBasedOn(apiContributionSummary.getBasedOn());
                contributionSummaryDTO.setUpliftApplied("Y".equalsIgnoreCase((apiContributionSummary.getUpliftApplied())));
                contributionSummaryDTO.setEffectiveDate(toDate(apiContributionSummary.getEffectiveDate()));
                contributionSummaryDTO.setCalcDate(toDate(apiContributionSummary.getCalcDate()));
                contributionSummaryCollection.add(contributionSummaryDTO);
            }
        }
        return contributionSummaryCollection;
    }
}
