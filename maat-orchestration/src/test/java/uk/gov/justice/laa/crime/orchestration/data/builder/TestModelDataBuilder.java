package uk.gov.justice.laa.crime.orchestration.data.builder;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOverview;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.*;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;
import uk.gov.justice.laa.crime.orchestration.model.means_assessment.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    public static ApiFindHardshipResponse getApiFindHardshipResponse() {
        return new ApiFindHardshipResponse()
                .withId(Constants.HARDSHIP_REVIEW_ID)
                .withCmuId(Constants.CMU_ID)
                .withNotes(Constants.CASEWORKER_NOTES)
                .withDecisionNotes(Constants.CASEWORKER_DECISION_NOTES)
                .withReviewDate(Constants.DATE_REVIEWED_DATETIME)
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(Constants.DISPOSABLE_INCOME)
                .withDisposableIncomeAfterHardship(Constants.POST_HARDSHIP_DISPOSABLE_INCOME)
                .withNewWorkReason(NewWorkReason.NEW)
                .withSolicitorCosts(getSolicitorsCosts())
                .withStatus(HardshipReviewStatus.COMPLETE)
                .withReviewDetails(
                        Stream.concat(
                                getApiHardshipReviewDetails(BigDecimal.valueOf(2000.00),
                                        HardshipReviewDetailType.EXPENDITURE
                                ).stream(),
                                getApiHardshipReviewDetails(BigDecimal.valueOf(1500.00),
                                        HardshipReviewDetailType.INCOME
                                ).stream()
                        ).toList())
                .withReviewProgressItems(getReviewProgressItems());
    }

    public static ApiPerformHardshipResponse getApiPerformHardshipResponse() {
        return new ApiPerformHardshipResponse()
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(Constants.DISPOSABLE_INCOME)
                .withHardshipReviewId(Constants.HARDSHIP_REVIEW_ID)
                .withPostHardshipDisposableIncome(Constants.POST_HARDSHIP_DISPOSABLE_INCOME);
    }

    public static ApiPerformHardshipRequest getApiPerformHardshipRequest() {
        return new ApiPerformHardshipRequest()
                .withHardship(
                        new HardshipReview()
                                .withCourtType(CourtType.MAGISTRATE)
                                .withDeniedIncome(
                                        List.of(
                                                new DeniedIncome()
                                                        .withAmount(BigDecimal.valueOf(1500))
                                                        .withFrequency(Frequency.MONTHLY)
                                                        .withAccepted(true)
                                                        .withDescription(
                                                                Constants.HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                                        .withReasonNote(Constants.HARDSHIP_REASON_NOTE)
                                        )
                                )
                                .withExtraExpenditure(
                                        List.of(
                                                new ExtraExpenditure()
                                                        .withAmount(BigDecimal.valueOf(2000.00))
                                                        .withFrequency(Frequency.ANNUALLY)
                                                        .withAccepted(false)
                                                        .withReasonCode(HardshipReviewDetailReason.EVIDENCE_SUPPLIED)
                                                        .withDescription(
                                                                Constants.HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(ExtraExpenditureDetailCode.CAR_LOAN)
                                        )
                                )
                                .withReviewDate(Constants.DATE_REVIEWED_DATETIME)
                                .withSolicitorCosts(
                                        new SolicitorCosts()
                                                .withVat(Constants.SOLICITOR_VAT)
                                                .withRate(Constants.SOLICITOR_RATE)
                                                .withHours(Constants.SOLICITOR_HOURS)
                                                .withDisbursements(Constants.SOLICITOR_DISBURSEMENTS)
                                                .withEstimatedTotal(Constants.SOLICITOR_ESTIMATED_COST)
                                )
                                .withTotalAnnualDisposableIncome(Constants.DISPOSABLE_INCOME)
                )
                .withHardshipMetadata(
                        new HardshipMetadata()
                                .withRepId(Constants.REP_ID)
                                .withCmuId(Constants.CMU_ID)
                                .withHardshipReviewId(Constants.HARDSHIP_REVIEW_ID)
                                .withFinancialAssessmentId(Constants.FINANCIAL_ASSESSMENT_ID)
                                .withReviewReason(NewWorkReason.getFrom(Constants.NEW_WORK_REASON_STRING))
                                .withReviewStatus(HardshipReviewStatus.COMPLETE)
                                .withNotes(Constants.CASEWORKER_NOTES)
                                .withDecisionNotes(Constants.CASEWORKER_DECISION_NOTES)
                                .withUserSession(getApiUserSession())
                                .withProgressItems(
                                        List.of(
                                                new HardshipProgress()
                                                        .withAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
                                                        .withResponse(
                                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                        .withDateTaken(Constants.DATE_REQUESTED_DATETIME)
                                                        .withDateRequired(Constants.DATE_REQUIRED_DATETIME)
                                                        .withDateCompleted(Constants.DATE_COMPLETED_DATETIME)
                                        )
                                )
                );
    }

    public static ApiUpdateApplicationRequest getUpdateApplicationRequest() {
        return new ApiUpdateApplicationRequest()
                .withApplicantHistoryId(Constants.APPLICANT_HISTORY_ID)
                .withCrownRepId(Constants.REP_ID)
                .withIsImprisoned(Boolean.TRUE)
                .withUserSession(getApiUserSession())
                .withRepId(Constants.REP_ID)
                .withCaseType(CaseType.EITHER_WAY)
                .withMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .withDecisionReason(DecisionReason.GRANTED)
                .withDecisionDate(Constants.DECISION_DATETIME)
                .withCommittalDate(Constants.COMMITAL_DATETIME)
                .withDateReceived(Constants.DATETIME_RECEIVED)
                .withCrownCourtSummary(getApiCrownCourtSummary())
                .withIojAppeal(getApiIOJAppeal())
                .withFinancialAssessment(getApiFinancialAssessment())
                .withPassportAssessment(getApiPassportAssessment());
    }

    public static ApiCrownCourtSummary getApiCrownCourtSummary() {
        return new ApiCrownCourtSummary()
                .withEvidenceFeeLevel(Constants.EVIDENCE_FEE_LEVEL_1)
                .withCrownCourtOutcome(List.of(getApiCrownCourtOutcome(CrownCourtOutcome.CONVICTED)))
                .withRepOrderDate(Constants.CC_REP_ORDER_DATETIME)
                .withRepType(Constants.CC_REP_TYPE_THROUGH_ORDER.getValue())
                .withRepOrderDecision(Constants.REP_ORDER_DECISION_GRANTED.getValue())
                .withRepId(Constants.REP_ID)
                .withSentenceOrderDate(Constants.SENTENCE_ORDER_DATETIME);
    }

    public static ApiCrownCourtOutcome getApiCrownCourtOutcome(CrownCourtOutcome crownCourtOutcome) {
        return new ApiCrownCourtOutcome()
                .withDateSet(Constants.SENTENCE_ORDER_DATETIME)
                .withDescription(crownCourtOutcome.getDescription())
                .withOutComeType(crownCourtOutcome.getType())
                .withOutcome(crownCourtOutcome);
    }

    public static ApiIOJAppeal getApiIOJAppeal() {
        return new ApiIOJAppeal()
                .withIojResult(Constants.RESULT_PASS)
                .withDecisionResult(Constants.RESULT_PASS);
    }

    public static ApiFinancialAssessment getApiFinancialAssessment() {
        return new ApiFinancialAssessment()
                .withInitResult(Constants.RESULT_FAIL)
                .withInitStatus(CurrentStatus.COMPLETE)
                .withFullResult(Constants.RESULT_PASS)
                .withFullStatus(CurrentStatus.COMPLETE)
                .withHardshipOverview(getApiHardshipOverview());
    }

    public static ApiHardshipOverview getApiHardshipOverview() {
        return new ApiHardshipOverview()
                .withReviewResult(ReviewResult.PASS)
                .withAssessmentStatus(CurrentStatus.COMPLETE);
    }

    public static ApiPassportAssessment getApiPassportAssessment() {
        return new ApiPassportAssessment()
                .withResult(Constants.RESULT_FAIL)
                .withStatus(CurrentStatus.COMPLETE);
    }


    public static ApiUserSession getApiUserSession() {
        return new ApiUserSession()
                .withUserName(Constants.USERNAME)
                .withSessionId(Constants.USER_SESSION);
    }

    public static ApiUpdateApplicationResponse getApiUpdateApplicationResponse() {
        return new ApiUpdateApplicationResponse();
    }

    public static ApplicationDTO getApplicationDTOWithBlankHardship(CourtType courtType) {
        ApplicationDTO applicationDTOWithHardship;
        if (courtType == CourtType.MAGISTRATE) {
            applicationDTOWithHardship = getApplicationDTOWithHardship(CourtType.MAGISTRATE);
            applicationDTOWithHardship.getAssessmentDTO()
                    .getFinancialAssessmentDTO()
                    .getHardship()
                    .setMagCourtHardship(HardshipReviewDTO.builder().build());
        } else {
            applicationDTOWithHardship = getApplicationDTOWithHardship(CourtType.CROWN_COURT);
            applicationDTOWithHardship.getAssessmentDTO()
                    .getFinancialAssessmentDTO()
                    .getHardship()
                    .setCrownCourtHardship(HardshipReviewDTO.builder().build());
        }
        return applicationDTOWithHardship;
    }

    private static List<ApiHardshipProgress> getReviewProgressItems() {
        return List.of(new ApiHardshipProgress()
                .withId(Constants.HARDSHIP_REVIEW_PROGRESS_ID)
                .withProgressResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                .withDateCompleted(Constants.DATE_COMPLETED_DATETIME)
                .withDateRequested(Constants.DATE_REQUESTED_DATETIME)
                .withDateRequired(Constants.DATE_REQUIRED_DATETIME)
                .withProgressAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
        );
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(Constants.SOLICITOR_VAT)
                .withDisbursements(Constants.SOLICITOR_DISBURSEMENTS)
                .withRate(Constants.SOLICITOR_RATE)
                .withHours(Constants.SOLICITOR_HOURS)
                .withEstimatedTotal(Constants.SOLICITOR_ESTIMATED_COST);
    }

    public static List<ApiHardshipDetail> getApiHardshipReviewDetails(BigDecimal amount,
                                                                      HardshipReviewDetailType... detailTypes) {
        List<ApiHardshipDetail> details = new ArrayList<>();

        Arrays.stream(detailTypes).forEach(type -> {
            switch (type) {
                case FUNDING -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.FUNDING)
                                .withAmount(amount)
                                .withDateDue(LocalDateTime.now())
                );
                case INCOME -> details.add(
                        new ApiHardshipDetail()
                                .withId(Constants.HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.INCOME)
                                .withAmount(amount)
                                .withFrequency(Frequency.MONTHLY)
                                .withAccepted("Y")
                                .withOtherDescription(Constants.HARDSHIP_OTHER_DESCRIPTION)
                                .withReasonNote(Constants.HARDSHIP_REASON_NOTE)
                                .withDetailCode(HardshipReviewDetailCode.MEDICAL_GROUNDS)
                );
                case EXPENDITURE -> details.add(
                        new ApiHardshipDetail()
                                .withId(Constants.HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("F")
                                .withDetailReason(HardshipReviewDetailReason.EVIDENCE_SUPPLIED)
                                .withOtherDescription(Constants.HARDSHIP_OTHER_DESCRIPTION)
                                .withDetailCode(HardshipReviewDetailCode.CAR_LOAN)
                );
                case SOL_COSTS -> details.add(
                        new ApiHardshipDetail()
                                .withDetailType(HardshipReviewDetailType.SOL_COSTS)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("Y")
                );
            }
        });
        return details;
    }

    public static AssessmentSummaryDTO getAssessmentSummaryDTO() {
        return AssessmentSummaryDTO.builder()
                .id(Constants.ASSESSMENT_SUMMARY_ID.longValue())
                .status(AssessmentStatusDTO.COMPLETE)
                .result(ReviewResult.PASS.getResult())
                .assessmentDate(Constants.ASSESSMENT_SUMMARY_DATE)
                .build();
    }

    public static AssessmentSummaryDTO getAssessmentSummaryDTOFromHardship(CourtType courtType) {
        AssessmentSummaryDTO assessmentSummaryDTO = getAssessmentSummaryDTO();
        if (courtType == CourtType.CROWN_COURT) {
            assessmentSummaryDTO.setType("Hardship Review - Crown Court");
        } else {
            assessmentSummaryDTO.setType("Hardship Review - Magistrate");
        }
        return assessmentSummaryDTO;
    }

    public static List<HRSectionDTO> getHrSectionDtosWithMixedTypes() {
        return Stream.concat(
                getHrSectionDtosWithExpenditureType().stream(),
                getHrSectionDtosWithDeniedIncomeType().stream()
        ).collect(Collectors.toList());
    }

    public static WorkflowRequest buildWorkflowRequestWithHardship(CourtType courtType) {
        return WorkflowRequest.builder()
                .userDTO(getUserDTO())
                .applicationDTO(getApplicationDTOWithHardship(courtType))
                .build();
    }

    // TODO: Check existing builders to see if they can use new methods for certain objects
    public static WorkflowRequest buildWorkFlowRequest(CourtType courtType) {
        return WorkflowRequest.builder()
                .userDTO(getUserDTO())
                .applicationDTO(getApplicationDTO(courtType))
                .build();
    }

    public static WorkflowRequest buildWorkFlowRequest() {
        return WorkflowRequest.builder()
                .userDTO(getUserDTO())
                .applicationDTO(getApplicationDTO())
                .isC3Enabled(true)
                .build();
    }


    public static ApplicationDTO getApplicationDTO(CourtType courtType) {
        return ApplicationDTO.builder()
                .repId(Constants.REP_ID.longValue())
                .dateReceived(Date.from(Constants.DATETIME_RECEIVED.atZone(ZoneId.systemDefault()).toInstant()))
                .committalDate(Date.from(Constants.COMMITAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .decisionDate(Date.from(Constants.DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .applicantDTO(getApplicantDTO())
                .assessmentDTO(getAssessmentDTO(courtType))
                .crownCourtOverviewDTO(getCrownCourtOverviewDTO())
                .caseDetailsDTO(getCaseDetailDTO())
                .offenceDTO(getOffenceDTO())
                .statusDTO(getRepStatusDTO())
                .magsOutcomeDTO(getOutcomeDTO(CourtType.MAGISTRATE))
                .passportedDTO(getPassportedDTO())
                .repOrderDecision(getRepOrderDecisionDTO())
                .iojResult(Constants.RESULT_PASS)
                .build();
    }

    public static ApplicationDTO getApplicationDTO() {
        return ApplicationDTO.builder()
                .repId(Constants.REP_ID.longValue())
                .dateReceived(Date.from(Constants.DATETIME_RECEIVED.atZone(ZoneId.systemDefault()).toInstant()))
                .committalDate(Date.from(Constants.COMMITAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .decisionDate(Date.from(Constants.DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .applicantDTO(getApplicantDTO())
                .assessmentDTO(getAssessmentDTO())
                .crownCourtOverviewDTO(getCrownCourtOverviewDTOForContribution())
                .caseDetailsDTO(getCaseDetailDTO())
                .offenceDTO(getOffenceDTO())
                .statusDTO(getRepStatusDTO())
                .magsOutcomeDTO(getOutcomeDTO(CourtType.MAGISTRATE))
                .passportedDTO(getPassportedDTO())
                .repOrderDecision(getRepOrderDecisionDTO())
                .iojResult(Constants.RESULT_PASS)
                .build();
    }

    public static ApplicantDTO getApplicantDTO() {
        return ApplicantDTO.builder()
                .applicantHistoryId(Constants.APPLICANT_HISTORY_ID.longValue())
                .build();
    }

    public static AssessmentDTO getAssessmentDTO(CourtType courtType) {
        return AssessmentDTO.builder()
                .iojAppeal(getIOJAppealDTO())
                .financialAssessmentDTO(getFinancialAssessmentDTO(courtType))
                .build();
    }
    public static AssessmentDTO getAssessmentDTO() {
        return AssessmentDTO.builder()
                .iojAppeal(getIOJAppealDTO())
                .financialAssessmentDTO(getFinancialAssessmentDTO())
                .build();
    }

    public static CrownCourtOverviewDTO getCrownCourtOverviewDTO() {
        return CrownCourtOverviewDTO.builder()
                .appealDTO(getAppealDTO())
                .contribution(getContributionsDTO())
                .crownCourtSummaryDTO(getCrownCourtSummaryDTO())
                .build();
    }
    public static CrownCourtOverviewDTO getCrownCourtOverviewDTOForContribution() {
        return CrownCourtOverviewDTO.builder()
                .appealDTO(getAppealDTO())
                .contribution(getContributionsDTO())
                .crownCourtSummaryDTO(getCrownCourtSummaryDTOForContribution())
                .build();
    }

    public static CrownCourtSummaryDTO getCrownCourtSummaryDTO() {
        return CrownCourtSummaryDTO.builder()
                .ccRepId(Constants.REP_ID.longValue())
                .ccRepType(Constants.CC_REP_TYPE_THROUGH_ORDER)
                .ccRepOrderDate(Date.from(Constants.CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .sentenceOrderDate(Date.from(Constants.SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .repOrderDecision(Constants.REP_ORDER_DECISION_GRANTED)
                .inPrisoned(Boolean.TRUE)
                .evidenceProvisionFee(getEvidenceFeeDTO())
                .outcomeDTOs(List.of(getOutcomeDTO(CourtType.CROWN_COURT)))
                .build();
    }
    public static CrownCourtSummaryDTO getCrownCourtSummaryDTOForContribution() {
        return CrownCourtSummaryDTO.builder()
                .ccRepId(Constants.REP_ID.longValue())
                .ccRepType(Constants.CC_REP_TYPE_THROUGH_ORDER)
                .ccRepOrderDate(Date.from(Constants.CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .sentenceOrderDate(Date.from(Constants.SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .repOrderDecision(Constants.REP_ORDER_DECISION_GRANTED)
                .inPrisoned(Boolean.TRUE)
                .evidenceProvisionFee(getEvidenceFeeDTO())
                .outcomeDTOs(List.of(getOutcomeDTO()))
                .build();
    }

    public static EvidenceFeeDTO getEvidenceFeeDTO() {
        return EvidenceFeeDTO.builder()
                .feeLevel(Constants.EVIDENCE_FEE_LEVEL_1)
                .build();
    }

    public static IOJAppealDTO getIOJAppealDTO() {
        return IOJAppealDTO.builder()
                .appealDecisionResult(Constants.RESULT_PASS)
                .build();
    }

    public static CaseDetailDTO getCaseDetailDTO() {
        return CaseDetailDTO.builder()
                .caseType(CaseType.EITHER_WAY.getCaseType())
                .build();
    }

    public static OutcomeDTO getOutcomeDTO(CourtType courtType) {
        if (courtType.equals(CourtType.CROWN_COURT)) {
            return OutcomeDTO.builder()
                    .dateSet(Date.from(Constants.SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                    .description(CrownCourtOutcome.CONVICTED.getDescription())
                    .outComeType(CrownCourtOutcome.CONVICTED.getType())
                    .outcome(CrownCourtOutcome.CONVICTED.toString())
                    .build();
        } else {
            return OutcomeDTO.builder()
                    .outcome(MagCourtOutcome.SENT_FOR_TRIAL.getOutcome())
                    .build();
        }

    }

    public static OutcomeDTO getOutcomeDTO() {
        return OutcomeDTO.builder()
                .dateSet(Date.from(Constants.SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .description(CrownCourtOutcome.SUCCESSFUL.getDescription())
                .outComeType(CrownCourtOutcome.SUCCESSFUL.getType())
                .outcome(CrownCourtOutcome.SUCCESSFUL.toString())
                .build();
    }

    public static PassportedDTO getPassportedDTO() {
        return PassportedDTO.builder()
                .passportedId(Constants.PASSPORTED_ID.longValue())
                .newWorkReason(getNewWorkReasonDTO())
                .assessementStatusDTO(getAssessmentStatusDTO())
                .result(Constants.RESULT_FAIL)
                .build();
    }

    public static RepOrderDecisionDTO getRepOrderDecisionDTO() {
        return RepOrderDecisionDTO.builder()
                .code(DecisionReason.GRANTED.getCode())
                .build();
    }

    public static ContributionsDTO getContributionsDTO() {
        return ContributionsDTO.builder()
                .id(Constants.CONTRIBUTIONS_ID.longValue())
                .upliftApplied(false)
                .basedOn(Constants.CONTRIBUTION_BASED_ON)
                .calcDate(Constants.CONTRIBUTION_CALCULATION_DATE)
                .effectiveDate(Constants.CONTRIBUTION_EFFECTIVE_DATE)
                .monthlyContribs(Constants.MONTHLY_CONTRIBUTION_AMOUNT)
                .upfrontContribs(Constants.UPFRONT_CONTRIBUTION_AMOUNT)
                .build();
    }

    public static ApplicationDTO getApplicationDTOWithHardship(CourtType courtType) {
        return ApplicationDTO.builder()
                .courtType(courtType)
                .repId(Constants.REP_ID.longValue())
                .caseManagementUnitDTO(getCaseManagementUnitDTO())
                .crownCourtOverviewDTO(CrownCourtOverviewDTO.builder().build())
                .assessmentDTO(
                        AssessmentDTO.builder()
                                .financialAssessmentDTO(getFinancialAssessmentDTO(courtType))
                                .build()
                ).build();
    }

    private static FinancialAssessmentDTO getFinancialAssessmentDTO(CourtType courtType) {
        return FinancialAssessmentDTO.builder()
                .id(Constants.FINANCIAL_ASSESSMENT_ID.longValue())
                .full(getFullAssessmentDTO())
                .initial(getInitialAssessmentDTO())
                .hardship(getHardshipOverviewDTO(courtType))
                .incomeEvidence(getIncomeEvidenceSummaryDTO())
                .build();
    }
    private static FinancialAssessmentDTO getFinancialAssessmentDTO() {
        return FinancialAssessmentDTO.builder()
                .id(Constants.FINANCIAL_ASSESSMENT_ID.longValue())
                .full(getFullAssessmentDTO())
                .initial(getInitialAssessmentDTO())
                .hardship(getHardshipOverviewDTO())
                .incomeEvidence(getIncomeEvidenceSummaryDTO())
                .build();
    }

    public static FullAssessmentDTO getFullAssessmentDTO() {
        return FullAssessmentDTO.builder()
                .assessmentDate(Constants.ASSESSMENT_DATE)
                .result(Constants.RESULT_PASS)
                .assessmnentStatusDTO(getAssessmentStatusDTO())
                .build();
    }

    public static InitialAssessmentDTO getInitialAssessmentDTO() {
        return InitialAssessmentDTO.builder()
                .result(Constants.RESULT_FAIL)
                .newWorkReason(getNewWorkReasonDTO())
                .assessmnentStatusDTO(getAssessmentStatusDTO())
                .build();
    }

    private static CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return CaseManagementUnitDTO.builder()
                .cmuId(Constants.CMU_ID.longValue())
                .build();
    }

    public static HardshipOverviewDTO getHardshipOverviewDTO(CourtType courtType) {
        HardshipOverviewDTO.HardshipOverviewDTOBuilder<?, ?> dtoBuilder = HardshipOverviewDTO.builder();

        HardshipReviewDTO reviewDTO = getHardshipReviewDTO();

        if (courtType == CourtType.CROWN_COURT) {
            dtoBuilder.crownCourtHardship(reviewDTO);
            // Mimic Maat passing an instance with null fields
            reviewDTO.setSolictorsCosts(HRSolicitorsCostsDTO.builder().build());
        } else {
            dtoBuilder.magCourtHardship(reviewDTO);
            reviewDTO.setSolictorsCosts(getHRSolicitorsCostsDTO());
        }
//
        return dtoBuilder.build();
    }

    public static HardshipOverviewDTO getHardshipOverviewDTO() {
        return HardshipOverviewDTO.builder()
                .crownCourtHardship(getHardshipReviewDTO())
                .magCourtHardship(getHardshipReviewDTO())
                .build();
    }

    public static HardshipReviewDTO getHardshipReviewDTO() {
        return HardshipReviewDTO.builder()
                .id(Constants.HARDSHIP_REVIEW_ID.longValue())
                .cmuId(Constants.CMU_ID.longValue())
                .disposableIncome(Constants.DISPOSABLE_INCOME)
                .reviewResult(Constants.HARDSHIP_REVIEW_RESULT)
                .disposableIncomeAfterHardship(Constants.POST_HARDSHIP_DISPOSABLE_INCOME)
                .reviewDate(Constants.DATE_REVIEWED)
                .section(TestModelDataBuilder.getHrSectionDtosWithMixedTypes())
                .asessmentStatus(getAssessmentStatusDTO())
                .newWorkReason(getNewWorkReasonDTO())
                .notes(Constants.CASEWORKER_NOTES)
                .decisionNotes(Constants.CASEWORKER_DECISION_NOTES)
                .solictorsCosts(getHRSolicitorsCostsDTO())
                .progress(getHrProgressDTOs())
                .build();
    }

    @NotNull
    private static List<HRProgressDTO> getHrProgressDTOs() {
        return List.of(
                HRProgressDTO.builder()
                        .id(Constants.HARDSHIP_REVIEW_PROGRESS_ID.longValue())
                        .progressAction(
                                HRProgressActionDTO.builder()
                                        .action(HardshipReviewProgressAction.SOLICITOR_INFORMED.getAction())
                                        .description(HardshipReviewProgressAction.SOLICITOR_INFORMED.getDescription())
                                        .build()
                        )
                        .progressResponse(
                                HRProgressResponseDTO.builder()
                                        .response(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED.getResponse())
                                        .description(
                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED.getDescription())
                                        .build()
                        )
                        .dateRequired(Constants.DATE_REQUIRED)
                        .dateRequested(Constants.DATE_REQUESTED)
                        .dateCompleted(Constants.DATE_COMPLETED)
                        .build()
        );
    }

    private static NewWorkReasonDTO getNewWorkReasonDTO() {
        return NewWorkReasonDTO.builder()
                .code(Constants.NEW_WORK_REASON_STRING)
                .description("New")
                .build();
    }

    private static AssessmentStatusDTO getAssessmentStatusDTO() {
        return AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description("Complete")
                .build();
    }
    public static HRSolicitorsCostsDTO getHRSolicitorsCostsDTO() {
        return HRSolicitorsCostsDTO.builder()
                .solicitorRate(Constants.SOLICITOR_RATE)
                .solicitorHours(Constants.SOLICITOR_HOURS.doubleValue())
                .solicitorDisb(Constants.SOLICITOR_DISBURSEMENTS)
                .solicitorVat(Constants.SOLICITOR_VAT)
                .solicitorEstimatedTotalCost(Constants.SOLICITOR_ESTIMATED_COST)
                .build();
    }

    private static UserDTO getUserDTO() {
        return UserDTO.builder()
                .userName(Constants.USERNAME)
                .userSession(Constants.USER_SESSION)
                .build();
    }

    public static IncomeEvidenceSummaryDTO getIncomeEvidenceSummaryDTO() {
        return IncomeEvidenceSummaryDTO.builder()
                .upliftAppliedDate(new Date(2023, 11, 18))
                .upliftRemovedDate(new Date(2023, 11, 18))
                .incomeEvidenceNotes("Income Evidence Notes")
                .applicantIncomeEvidenceList(List.of(getEvidenceDTO()))
                .partnerIncomeEvidenceList(List.of(getEvidenceDTO()))
                .evidenceReceivedDate(new Date(2023, 02, 18))
                .evidenceDueDate(new Date(2023, 03, 18))
                .upliftsAvailable(true)
                .build();
    }

    private static EvidenceDTO getEvidenceDTO() {
        return EvidenceDTO.builder()
                .id(Constants.EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(new Date(2023, 11, 18))
                .otherDescription("OTHER DESCRIPTION")
                .selected(true)
                .build();
    }

    private static EvidenceTypeDTO getEvidenceTypeDTO() {
        return EvidenceTypeDTO.builder()
                .evidence(Constants.INCOME_EVIDENCE)
                .description(Constants.INCOME_EVIDENCE_DESCRIPTION)
                .build();
    }

    private static AppealDTO getAppealDTO() {
        return AppealDTO.builder()
                .available(true)
                .appealReceivedDate(new Date(2023, 03, 18))
                .appealSentenceOrderDate(new Date(2023, 8, 3))
                .appealTypeDTO(getAppealTypeDTO())
                .build();
    }

    private static AppealTypeDTO getAppealTypeDTO() {
        return AppealTypeDTO.builder()
                .code(AppealType.ACS.getCode())
                .description(AppealType.ACS.getDescription())
                .build();
    }

    public static ApiContributionSummary getApiContributionSummary() {
        return new ApiContributionSummary()
                .withId(1)
                .withMonthlyContributions(BigDecimal.valueOf(100))
                .withUpfrontContributions(BigDecimal.valueOf(100))
                .withBasedOn("Means")
                .withUpliftApplied("Y")
                .withEffectiveDate(LocalDateTime.parse("2023-06-27T10:15:30"))
                .withCalcDate(LocalDateTime.parse("2023-03-01T09:00:00"))
                .withFileName("Test")
                .withDateSent(LocalDateTime.parse("2022-12-01T09:00:00"))
                .withDateReceived(LocalDateTime.parse("2023-01-01T09:00:00"));
    }

    public static ApiMaatCalculateContributionResponse getApiMaatCalculateContributionResponse() {
        return new ApiMaatCalculateContributionResponse()
                .withContributionCap(BigDecimal.valueOf(100))
                .withContributionId(1)
                .withCreateContributionOrder("Contribution_Order")
                .withCalcDate(LocalDateTime.parse("2022-12-01T09:00:00"))
                .withCcOutcomeCount(1)
                .withEffectiveDate(LocalDateTime.parse("2023-01-01T09:00:00"))
                .withMonthlyContributions(BigDecimal.valueOf(50))
                .withProcessActivity(true)
                .withRepId(1)
                .withReplacedDate(LocalDateTime.parse("2022-01-01T09:00:00"))
                .withTotalMonths(12)
                .withUpliftApplied("Y")
                .withUpfrontContributions(BigDecimal.valueOf(50));
    }

    private static OffenceDTO getOffenceDTO() {
        return OffenceDTO.builder()
                .offenceType("Offence Type")
                .contributionCap(Double.valueOf(100))
                .build();
    }

    private static RepStatusDTO getRepStatusDTO() {
        return RepStatusDTO.builder()
                .status("RepStatus")
                .removeContribs(true)
                .build();
    }

    public static List<HRSectionDTO> getHrSectionDtosWithExpenditureType() {
        return List.of(
                HRSectionDTO.builder()
                        .detailType(HRDetailTypeDTO.builder()
                                .type(HardshipReviewDetailType.EXPENDITURE.getType())
                                .description(HardshipReviewDetailType.EXPENDITURE.getDescription())
                                .build())
                        .detail(List.of(
                                        HRDetailDTO.builder()
                                                .id(Constants.HARDSHIP_DETAIL_ID.longValue())
                                                .detailDescription(
                                                        HRDetailDescriptionDTO.builder()
                                                                .code(HardshipReviewDetailCode.CAR_LOAN.getCode())
                                                                .description(HardshipReviewDetailCode.CAR_LOAN.getDescription())
                                                                .build())
                                                .accepted(false)
                                                .frequency(FrequenciesDTO.builder()
                                                        .code(Frequency.ANNUALLY.getCode())
                                                        .annualWeighting(
                                                                (long) Frequency.ANNUALLY.getAnnualWeighting())
                                                        .description(Frequency.ANNUALLY.getDescription())
                                                        .build())
                                                .amountNumber(BigDecimal.valueOf(2000.00))
                                                .otherDescription(Constants.HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(
                                                        HRReasonDTO.builder()
                                                                .reason(HardshipReviewDetailReason.EVIDENCE_SUPPLIED.getReason())
                                                                .build())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    public static List<HRSectionDTO> getHrSectionDtosWithDeniedIncomeType() {
        return List.of(
                HRSectionDTO.builder()
                        .detailType(HRDetailTypeDTO.builder()
                                .type(HardshipReviewDetailType.INCOME.getType())
                                .description(HardshipReviewDetailType.INCOME.getDescription())
                                .build())
                        .detail(List.of(
                                        HRDetailDTO.builder()
                                                .id(Constants.HARDSHIP_DETAIL_ID.longValue())
                                                .detailDescription(
                                                        HRDetailDescriptionDTO.builder()
                                                                .code(HardshipReviewDetailCode.MEDICAL_GROUNDS.getCode())
                                                                .description(
                                                                        HardshipReviewDetailCode.MEDICAL_GROUNDS.getDescription())
                                                                .build())
                                                .accepted(true)
                                                .frequency(FrequenciesDTO.builder()
                                                        .code(Frequency.MONTHLY.getCode())
                                                        .annualWeighting(
                                                                (long) Frequency.MONTHLY.getAnnualWeighting())
                                                        .description(Frequency.MONTHLY.getDescription())
                                                        .build())
                                                .amountNumber(BigDecimal.valueOf(1500.00))
                                                .hrReasonNote(Constants.HARDSHIP_REASON_NOTE)
                                                .otherDescription(Constants.HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(HRReasonDTO.builder().build())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    public static ApiGetMeansAssessmentResponse getApiGetMeansAssessmentResponse() {
        return new ApiGetMeansAssessmentResponse()
                .withId(Constants.FINANCIAL_ASSESSMENT_ID)
                .withCriteriaId(Constants.CRITERIA_ID)
                .withFullAvailable(true)
                .withUsn(Constants.USN)
                .withFullAssessment(getApiFullAssessment(CurrentStatus.COMPLETE));
    }

    public static ApiFullMeansAssessment getApiFullAssessment(CurrentStatus currentStatus) {
        return new ApiFullMeansAssessment()
                .withCriteriaId(Constants.CRITERIA_ID)
                .withAssessmentDate(Constants.ASSESSMENT_DATETIME)
                .withAssessmentNotes(Constants.ASSESSMENT_NOTES)
                .withAdjustedLivingAllowance(BigDecimal.valueOf(15600.00))
                .withOtherHousingNote(Constants.OTHER_HOUSING_NOTE)
                .withTotalAggregatedExpense(Constants.AGGREGATED_EXPENSE)
                .withTotalAnnualDisposableIncome(Constants.ANNUAL_DISPOSABLE_INCOME)
                .withThreshold(Constants.THRESHOLD)
                .withResult(AssessmentResult.PASS.toString())
                .withResultReason("FullAssessmentResult.PASS.getReason()")
                .withAssessmentStatus(new ApiAssessmentStatus()
                        .withStatus(currentStatus.getValue())
                        .withDescription(currentStatus.getDescription()));
    }

    public static ApiCreateMeansAssessmentRequest getApiCreateMeansAssessmentRequest() {
            return new ApiCreateMeansAssessmentRequest()
                    .withLaaTransactionId(Constants.TRANSACTION_ID)
                    .withAssessmentType(AssessmentType.INIT)
                    .withReviewType(ReviewType.NAFI)
                    .withRepId(Constants.REP_ID)
                    .withCmuId(Constants.CMU_ID)
                    .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                    .withNewWorkReason(NewWorkReason.NEW)
                    .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                    .withHasPartner(true)
                    .withPartnerContraryInterest(false)
                    .withCaseType(CaseType.EITHER_WAY)
                    .withAssessmentStatus(CurrentStatus.COMPLETE)
                    .withChildWeightings(getAssessmentChildWeightings())
                    .withUserSession(getApiUserSession())
                    .withEmploymentStatus(Constants.EMPLOYMENT_STATUS)
                    .withUsn(Constants.USN)
                    .withCrownCourtOverview(new ApiCrownCourtOverview()
                            .withAvailable(true)
                            .withCrownCourtSummary(
                                    new ApiCrownCourtSummary()
                                            .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                            )
                    )
                    .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
        }
    public static ApiIncomeEvidenceSummary getApiIncomeEvidenceSummary() {
        return new ApiIncomeEvidenceSummary()
                .withIncomeEvidenceNotes(Constants.INCOME_EVIDENCE_NOTES)
                .withEvidenceDueDate(Constants.INCOME_EVIDENCE_DUE_DATE)
                .withUpliftAppliedDate(Constants.INCOME_UPLIFT_APPLY_DATE)
                .withUpliftRemovedDate(Constants.INCOME_UPLIFT_REMOVE_DATE);
    }

    public static List<ApiAssessmentChildWeighting> getAssessmentChildWeightings() {
        return List.of(
                new ApiAssessmentChildWeighting()
                        .withId(1234)
                        .withChildWeightingId(37)
                        .withNoOfChildren(1)
                ,
                new ApiAssessmentChildWeighting()
                        .withId(2345)
                        .withChildWeightingId(38)
                        .withNoOfChildren(2)
        );
    }

    public static ApiAssessmentSectionSummary getApiAssessmentSectionSummary() {
        return new ApiAssessmentSectionSummary()
                .withApplicantAnnualTotal(Constants.APPLICANT_ANNUAL_TOTAL)
                .withAnnualTotal(Constants.APPLICANT_ANNUAL_TOTAL)
                .withPartnerAnnualTotal(BigDecimal.ZERO)
                .withSection("INITA")
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withCriteriaDetailId(Constants.CRITERIA_DETAIL_ID)
                                                .withApplicantAmount(Constants.APPLICANT_VALUE)
                                                .withApplicantFrequency(Constants.FREQUENCY)
                                )
                        )
                );
    }

    public static ApiUpdateMeansAssessmentRequest getApiUpdateMeansAssessmentRequest() {
        return new ApiUpdateMeansAssessmentRequest()
                .withLaaTransactionId(Constants.TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withRepId(Constants.REP_ID)
                .withCmuId(Constants.CMU_ID)
                .withInitialAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withFullAssessmentDate(LocalDateTime.of(2021, 12, 16, 10, 0))
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withOtherHousingNote(Constants.OTHER_HOUSING_NOTE)
                .withInitTotalAggregatedIncome(Constants.AGGREGATED_EXPENSE)
                .withFullAssessmentNotes(Constants.FULL_ASSESSMENT_NOTES)
                .withCaseType(CaseType.EITHER_WAY)
                .withEmploymentStatus(Constants.EMPLOYMENT_STATUS)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getApiUserSession())
                .withCrownCourtOverview(new ApiCrownCourtOverview()
                        .withAvailable(true)
                        .withCrownCourtSummary(
                                new ApiCrownCourtSummary()
                                        .withRepOrderDecision("MOCK_REP_ORDER_DECISION")
                        )
                )
                .withSectionSummaries(List.of(getApiAssessmentSectionSummary()));
    }

    public static AssessmentStatusDTO getAssessmentStatusDTO(CurrentStatus status) {
        return AssessmentStatusDTO.builder()
                .status(status.getValue())
                .description(status.getDescription())
                .build();
    }
}
