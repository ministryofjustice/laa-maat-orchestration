package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.LastOutcome;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ContributionMapper extends CrownCourtMapper {

    public ApiMaatCalculateContributionRequest workflowRequestToMaatCalculateContributionRequest(
            WorkflowRequest workflowRequest) {

        UserDTO user = workflowRequest.getUserDTO();
        ApplicationDTO application = workflowRequest.getApplicationDTO();

        CrownCourtOverviewDTO crownCourtOverviewDTO = application.getCrownCourtOverviewDTO();
        ContributionsDTO contributionsDTO = crownCourtOverviewDTO.getContribution();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        IncomeEvidenceSummaryDTO incomeEvidenceSummaryDTO = financialAssessmentDTO.getIncomeEvidence();
        HardshipOverviewDTO hardshipOverviewDTO = financialAssessmentDTO.getHardship();

        Collection<OutcomeDTO> outcomeDTOs = crownCourtOverviewDTO.getCrownCourtSummaryDTO().getOutcomeDTOs();
        String appealType = crownCourtOverviewDTO.getAppealDTO().getAppealTypeDTO().getCode();
        LocalDateTime effectiveDate = DateUtil.toLocalDateTime(contributionsDTO.getEffectiveDate());

        return new ApiMaatCalculateContributionRequest()
                .withUserCreated(user.getUserName())
                .withRepId(NumberUtils.toInteger(application.getRepId()))
                .withApplId(NumberUtils.toInteger(application.getApplicantDTO().getId()))
                .withMagCourtOutcome(MagCourtOutcome.getFrom(application.getMagsOutcomeDTO().getOutcome()))
                .withCommittalDate(DateUtil.toLocalDateTime(application.getCommittalDate()))
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
                .withDateUpliftApplied(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftAppliedDate()))
                .withDateUpliftRemoved(DateUtil.toLocalDateTime(incomeEvidenceSummaryDTO.getUpliftRemovedDate()))
                .withDisposableIncomeAfterCrownHardship(
                        hardshipOverviewDTO.getCrownCourtHardship().getDisposableIncomeAfterHardship())
                .withDisposableIncomeAfterMagHardship(
                        hardshipOverviewDTO.getMagCourtHardship().getDisposableIncomeAfterHardship())
                .withTotalAnnualDisposableIncome(
                        Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())
                                ? BigDecimal.valueOf(financialAssessmentDTO.getFull().getTotalAnnualDisposableIncome())
                                : null
                );
    }

    private LastOutcome getLastCrownCourtOutcome(final Collection<OutcomeDTO> crownCourtOutcomeList) {
        return crownCourtOutcomeList.stream().reduce((first, second) -> second)
                .map(outcome ->
                             new LastOutcome()
                                     .withDateSet(DateUtil.toLocalDateTime(outcome.getDateSet()))
                                     .withOutcome(CrownCourtAppealOutcome.getFrom(outcome.getOutcome()))
                ).orElse(null);
    }

    private List<ApiAssessment> applicationDtoToAssessments(final ApplicationDTO application) {
        List<ApiAssessment> assessmentList = new ArrayList<>();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();

        assessmentList.add(
                new ApiAssessment()
                        .withAssessmentType(AssessmentType.INIT)
                        .withResult(AssessmentResult.getFrom(initialAssessmentDTO.getResult()))
                        .withAssessmentDate(DateUtil.toLocalDateTime(initialAssessmentDTO.getAssessmentDate()))
                        .withNewWorkReason(NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()))
                        .withStatus(CurrentStatus.getFrom(initialAssessmentDTO.getAssessmnentStatusDTO().getStatus()))
        );

        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        if (Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())) {
            assessmentList.add(
                    new ApiAssessment()
                            .withAssessmentType(AssessmentType.FULL)
                            .withResult(AssessmentResult.getFrom(fullAssessmentDTO.getResult()))
                            .withAssessmentDate(DateUtil.toLocalDateTime(fullAssessmentDTO.getAssessmentDate()))
                            .withNewWorkReason(NewWorkReason.getFrom(initialAssessmentDTO.getNewWorkReason().getCode()))
                            .withStatus(CurrentStatus.getFrom(fullAssessmentDTO.getAssessmnentStatusDTO().getStatus()))
            );
        }

        PassportedDTO passported = application.getPassportedDTO();
        if (passported.getPassportedId() != null) {
            assessmentList.add(
                    new ApiAssessment()
                            .withAssessmentType(AssessmentType.PASSPORT)
                            .withResult(AssessmentResult.getFrom(passported.getResult()))
                            .withAssessmentDate(DateUtil.toLocalDateTime(passported.getDate()))
                            .withNewWorkReason(NewWorkReason.getFrom(passported.getNewWorkReason().getCode()))
                            .withStatus(CurrentStatus.getFrom(passported.getAssessementStatusDTO().getStatus()))
            );
        }
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
                .calcDate(DateUtil.toDate(response.getCalcDate()))
                .basedOn(new SysGenString(response.getBasedOn()))
                .monthlyContribs(response.getMonthlyContributions())
                .upfrontContribs(response.getUpfrontContributions())
                .effectiveDate(DateUtil.toDate(response.getEffectiveDate()))
                .upliftApplied(Boolean.parseBoolean(response.getUpliftApplied()))
                .build();
    }
}
