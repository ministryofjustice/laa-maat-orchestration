package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.contribution.model.common.ApiAssessment;
import uk.gov.justice.laa.crime.contribution.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.LastOutcome;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;
import uk.gov.justice.laa.crime.orchestration.util.NumberUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CalculateContributionMapper implements RequestMapper<ApiMaatCalculateContributionRequest, WorkflowRequestDTO>,
        ResponseMapper<ApiMaatCalculateContributionResponse, ApplicationDTO> {

    @Override
    public ApiMaatCalculateContributionRequest fromDto(WorkflowRequestDTO workflowRequest) {
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
                .withAssessments(getApiAssessments(application))
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
                .withLastOutcome(getLastCCOutcome(outcomeDTOs))
                .withCrownCourtOutcome(
                        outcomeDTOs.stream()
                                .map(this::mapApiCrownCourtOutcome)
                                .collect(Collectors.toList())
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

    private ApiCrownCourtOutcome mapApiCrownCourtOutcome(OutcomeDTO outcomeDTO) {
        return new ApiCrownCourtOutcome()
                .withOutcome(CrownCourtOutcome.getFrom(outcomeDTO.getOutcome()))
                .withOutComeType(outcomeDTO.getOutComeType())
                .withDateSet(DateUtil.toLocalDateTime(outcomeDTO.getDateSet()))
                .withDescription(outcomeDTO.getDescription());
    }

    private LastOutcome getLastCCOutcome(final Collection<OutcomeDTO> crownCourtOutcomeList) {
        return crownCourtOutcomeList.stream().reduce((first, second) -> second)
                .map(outcome ->
                             new LastOutcome()
                                     .withDateSet(DateUtil.toLocalDateTime(outcome.getDateSet()))
                                     .withOutcome(CrownCourtAppealOutcome.getFrom(outcome.getOutcome()))
                ).orElse(null);
    }

    private ApiAssessment mapAssessment(final AssessmentType assessmentType,
                                        final Date assessmentDate,
                                        final AssessmentStatusDTO assessmentStatusDTO,
                                        final String result,
                                        final NewWorkReasonDTO newWorkReason) {
        return new ApiAssessment()
                .withAssessmentType(assessmentType)
                .withAssessmentDate(DateUtil.toLocalDateTime(assessmentDate))
                .withNewWorkReason(
                        newWorkReason.getCode() != null ? NewWorkReason.getFrom(newWorkReason.getCode()) : null)
                .withResult(result != null ? AssessmentResult.valueOf(result) : null)
                .withStatus(CurrentStatus.getFrom(assessmentStatusDTO.getStatus()));
    }

    private List<ApiAssessment> getApiAssessments(final ApplicationDTO application) {
        List<ApiAssessment> assessmentList = new ArrayList<>();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = financialAssessmentDTO.getInitial();
        assessmentList.add(mapAssessment(AssessmentType.INIT,
                                         initialAssessmentDTO.getAssessmentDate(),
                                         initialAssessmentDTO.getAssessmnentStatusDTO(),
                                         initialAssessmentDTO.getResult(),
                                         initialAssessmentDTO.getNewWorkReason()
        ));

        FullAssessmentDTO fullAssessmentDTO = financialAssessmentDTO.getFull();
        if (Boolean.TRUE.equals(financialAssessmentDTO.getFullAvailable())) {
            assessmentList.add(mapAssessment(AssessmentType.FULL,
                                             fullAssessmentDTO.getAssessmentDate(),
                                             fullAssessmentDTO.getAssessmnentStatusDTO(),
                                             fullAssessmentDTO.getResult(),
                                             initialAssessmentDTO.getNewWorkReason()
            ));
        }

        PassportedDTO passported = application.getPassportedDTO();
        if (passported.getPassportedId() != null) {
            assessmentList.add(mapAssessment(AssessmentType.PASSPORT,
                                             passported.getDate(),
                                             passported.getAssessementStatusDTO(),
                                             passported.getResult(),
                                             passported.getNewWorkReason()
            ));
        }
        return assessmentList;
    }

    @Override
    public void toDto(ApiMaatCalculateContributionResponse response, ApplicationDTO application) {
        if (response != null && response.getContributionId() != null) {
            ContributionsDTO contributionsDTO = application.getCrownCourtOverviewDTO().getContribution();
            contributionsDTO.setBasedOn(new SysGenString(response.getBasedOn()));
            contributionsDTO.setId(response.getContributionId().longValue());
            contributionsDTO.setCalcDate(DateUtil.toDate(response.getCalcDate()));
            contributionsDTO.setMonthlyContribs(response.getMonthlyContributions());
            contributionsDTO.setUpfrontContribs(response.getUpfrontContributions());
            contributionsDTO.setEffectiveDate(DateUtil.toDate(response.getEffectiveDate()));
            contributionsDTO.setUpliftApplied(Boolean.parseBoolean(response.getUpliftApplied()));
            contributionsDTO.setCapped(response.getContributionCap());
        }
    }
}
