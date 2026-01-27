package uk.gov.justice.laa.crime.orchestration.mapper;

import static uk.gov.justice.laa.crime.util.DateUtil.toDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.contribution.ApiAssessment;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.MagCourtOutcome;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.contribution.AssessmentType;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FullAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IncomeEvidenceSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenDate;
import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenString;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ContributionMapper extends CrownCourtMapper {

    public ApiMaatCalculateContributionRequest workflowRequestToMaatCalculateContributionRequest(
            WorkflowRequest workflowRequest) {

        log.info("ContributionMapper.workflowRequestToMaatCalculateContributionRequest()");
        ApiMaatCalculateContributionRequest request;

        UserDTO user = workflowRequest.getUserDTO();
        ApplicationDTO application = workflowRequest.getApplicationDTO();

        CrownCourtOverviewDTO crownCourtOverviewDTO = application.getCrownCourtOverviewDTO();
        ContributionsDTO contributionsDTO = crownCourtOverviewDTO.getContribution();
        FinancialAssessmentDTO financialAssessmentDTO =
                application.getAssessmentDTO().getFinancialAssessmentDTO();
        IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = financialAssessmentDTO.getIncomeEvidence();
        HardshipOverviewDTO hardshipOverviewDTO = financialAssessmentDTO.getHardship();

        log.info("workflowRequestToMaatCalculateContributionRequest()");
        String appealType =
                crownCourtOverviewDTO.getAppealDTO().getAppealTypeDTO().getCode();
        LocalDateTime effectiveDate = toLocalDateTime(
                contributionsDTO.getEffectiveDate() != null
                        ? contributionsDTO.getEffectiveDate().getValue()
                        : null);

        request = new ApiMaatCalculateContributionRequest()
                .withUserCreated(user.getUserName())
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withApplicantId(
                        NumberUtils.toInteger(application.getApplicantDTO().getId()))
                .withMagCourtOutcome(
                        MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withCommittalDate(toLocalDateTime(application.getCommittalDate()))
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withAssessments(applicationDtoToAssessments(application))
                .withContributionCap(
                        BigDecimal.valueOf(application.getOffenceDTO().getContributionCap()))
                .withContributionId(NumberUtils.toInteger(contributionsDTO.getId()))
                .withMonthlyContributions(contributionsDTO.getMonthlyContribs())
                .withEffectiveDate(effectiveDate)
                .withUpfrontContributions(contributionsDTO.getUpfrontContribs())
                .withRemoveContributions(
                        application.getStatusDTO().getRemoveContribs().toString())
                .withMagCourtOutcome(
                        application.getMagsOutcomeDTO() != null
                                ? MagCourtOutcome.getFrom(
                                        application.getMagsOutcomeDTO().getOutcome())
                                : null)
                .withAppealType(appealType != null ? AppealType.getFrom(appealType) : null)
                .withCrownCourtOutcome(
                        crownCourtSummaryDtoToCrownCourtOutcomes(crownCourtOverviewDTO.getCrownCourtSummaryDTO()))
                .withDateUpliftApplied(toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftAppliedDate()))
                .withDateUpliftRemoved(toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftRemovedDate()))
                .withTotalAnnualDisposableIncome(
                        Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())
                                ? BigDecimal.valueOf(
                                        financialAssessmentDTO.getFull().getTotalAnnualDisposableIncome())
                                : null);

        if (hardshipOverviewDTO != null && hardshipOverviewDTO.getCrownCourtHardship() != null) {
            request.withDisposableIncomeAfterCrownHardship(
                    hardshipOverviewDTO.getCrownCourtHardship().getDisposableIncomeAfterHardship());
        }

        if (hardshipOverviewDTO != null && hardshipOverviewDTO.getMagCourtHardship() != null) {
            request.withDisposableIncomeAfterMagHardship(
                    hardshipOverviewDTO.getMagCourtHardship().getDisposableIncomeAfterHardship());
        }
        return request;
    }

    private List<ApiAssessment> applicationDtoToAssessments(final ApplicationDTO application) {
        List<ApiAssessment> assessmentList = new ArrayList<>();
        FinancialAssessmentDTO financialAssessmentDTO =
                application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();

        if (initialAssessmentDTO != null
                && null != initialAssessmentDTO.getAssessmnentStatusDTO()
                && StringUtils.isNotBlank(
                        initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                && StringUtils.isNotBlank(initialAssessmentDTO.getResult())) {
            log.info("applicationDtoToAssessments.initialAssessmentDTO.status-->"
                    + initialAssessmentDTO.getAssessmnentStatusDTO().getStatus());
            assessmentList.add(new ApiAssessment()
                    .withAssessmentType(AssessmentType.INIT)
                    .withResult(AssessmentResult.getFrom(initialAssessmentDTO.getResult()))
                    .withAssessmentDate(toLocalDateTime(initialAssessmentDTO.getAssessmentDate()))
                    .withNewWorkReason(NewWorkReason.getFrom(
                            initialAssessmentDTO.getNewWorkReason().getCode()))
                    .withStatus(CurrentStatus.getFrom(
                            initialAssessmentDTO.getAssessmnentStatusDTO().getStatus())));
        }

        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        log.info("applicationDtoToAssessments.fullAssessmentDTO-->" + fullAssessmentDTO);
        if (null != fullAssessmentDTO
                && null != fullAssessmentDTO.getAssessmnentStatusDTO()
                && StringUtils.isNotBlank(
                        fullAssessmentDTO.getAssessmnentStatusDTO().getStatus())
                && StringUtils.isNotBlank(fullAssessmentDTO.getResult())) {
            log.info("applicationDtoToAssessments.fullAssessmentDTO.status-->"
                    + fullAssessmentDTO.getAssessmnentStatusDTO().getStatus());
            assessmentList.add(new ApiAssessment()
                    .withAssessmentType(AssessmentType.FULL)
                    .withResult(AssessmentResult.getFrom(fullAssessmentDTO.getResult()))
                    .withAssessmentDate(toLocalDateTime(fullAssessmentDTO.getAssessmentDate()))
                    .withNewWorkReason(NewWorkReason.getFrom(
                            initialAssessmentDTO.getNewWorkReason().getCode()))
                    .withStatus(CurrentStatus.getFrom(
                            fullAssessmentDTO.getAssessmnentStatusDTO().getStatus())));
        }

        PassportedDTO passported = application.getPassportedDTO();
        if (passported != null && passported.getPassportedId() != null) {
            log.info("applicationDtoToAssessments.passported.status-->"
                    + passported.getAssessementStatusDTO().getStatus());
            assessmentList.add(new ApiAssessment()
                    .withAssessmentType(AssessmentType.PASSPORT)
                    .withResult(AssessmentResult.getFrom(passported.getResult()))
                    .withAssessmentDate(toLocalDateTime(passported.getDate()))
                    .withNewWorkReason(
                            NewWorkReason.getFrom(passported.getNewWorkReason().getCode()))
                    .withStatus(CurrentStatus.getFrom(
                            passported.getAssessementStatusDTO().getStatus())));
        }

        if (financialAssessmentDTO.getHardship() != null) {
            HardshipReviewDTO crownCourtHardshipReviewDTO =
                    financialAssessmentDTO.getHardship().getCrownCourtHardship();

            if (crownCourtHardshipReviewDTO != null && crownCourtHardshipReviewDTO.getId() != null) {
                log.info("applicationDtoToAssessments.crownCourtHardshipReviewDTO.status-->"
                        + crownCourtHardshipReviewDTO.getAsessmentStatus().getStatus());
                assessmentList.add(new ApiAssessment()
                        .withAssessmentType(AssessmentType.HARDSHIP)
                        .withResult(AssessmentResult.getFrom(crownCourtHardshipReviewDTO.getReviewResult()))
                        .withAssessmentDate(toLocalDateTime(crownCourtHardshipReviewDTO.getReviewDate()))
                        .withNewWorkReason(NewWorkReason.getFrom(
                                crownCourtHardshipReviewDTO.getNewWorkReason().getCode()))
                        .withStatus(CurrentStatus.getFrom(
                                crownCourtHardshipReviewDTO.getAsessmentStatus().getStatus())));
            }
        }

        log.info("applicationDtoToAssessments.assessmentList-->" + assessmentList);
        return assessmentList;
    }

    public ApiMaatCheckContributionRuleRequest applicationDtoToCheckContributionRuleRequest(
            ApplicationDTO application) {

        CrownCourtSummaryDTO crownCourtSummary =
                application.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO();
        return new ApiMaatCheckContributionRuleRequest()
                .withCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()))
                .withMagCourtOutcome(
                        MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
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

    public Collection<ContributionSummaryDTO> contributionSummaryToDto(
            List<ApiContributionSummary> contributionSummaries) {
        Collection<ContributionSummaryDTO> contributionSummaryCollection = new ArrayList<>();
        if (null != contributionSummaries) {
            for (ApiContributionSummary apiContributionSummary : contributionSummaries) {
                ContributionSummaryDTO contributionSummaryDTO = new ContributionSummaryDTO();
                contributionSummaryDTO.setId(Long.valueOf(apiContributionSummary.getId()));
                contributionSummaryDTO.setMonthlyContribs(
                        apiContributionSummary.getMonthlyContributions().doubleValue());
                contributionSummaryDTO.setUpfrontContribs(
                        apiContributionSummary.getUpfrontContributions().doubleValue());
                contributionSummaryDTO.setBasedOn(apiContributionSummary.getBasedOn());
                contributionSummaryDTO.setUpliftApplied(
                        "Y".equalsIgnoreCase((apiContributionSummary.getUpliftApplied())));
                contributionSummaryDTO.setEffectiveDate(toDate(apiContributionSummary.getEffectiveDate()));
                contributionSummaryDTO.setCalcDate(toDate(apiContributionSummary.getCalcDate()));
                contributionSummaryCollection.add(contributionSummaryDTO);
            }
        }
        return contributionSummaryCollection;
    }
}
