package uk.gov.justice.laa.crime.orchestration.data.builder;

import io.netty.util.Constant;
import org.apache.tomcat.util.bcel.Const;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.*;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.*;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TestModelDataBuilder {

    public static ApiFindHardshipResponse getApiFindHardshipResponse() {
        return new ApiFindHardshipResponse()
                .withId(Constants.TEST_HARDSHIP_REVIEW_ID)
                .withCmuId(Constants.TEST_CMU_ID)
                .withNotes(Constants.TEST_CASEWORKER_NOTES)
                .withDecisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                .withReviewDate(Constants.TEST_DATE_REVIEWED_DATETIME)
                .withReviewResult(HardshipReviewResult.PASS)
                .withDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .withDisposableIncomeAfterHardship(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME)
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
                .withDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .withHardshipReviewId(Constants.TEST_HARDSHIP_REVIEW_ID)
                .withPostHardshipDisposableIncome(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME);
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
                                                                Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                                        .withReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
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
                                                                Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(ExtraExpenditureDetailCode.CAR_LOAN)
                                        )
                                )
                                .withReviewDate(Constants.TEST_DATE_REVIEWED_DATETIME)
                                .withSolicitorCosts(
                                        new SolicitorCosts()
                                                .withVat(Constants.TEST_SOLICITOR_VAT)
                                                .withRate(Constants.TEST_SOLICITOR_RATE)
                                                .withHours(Constants.TEST_SOLICITOR_HOURS)
                                                .withDisbursements(Constants.TEST_SOLICITOR_DISBURSEMENTS)
                                                .withEstimatedTotal(Constants.TEST_SOLICITOR_ESTIMATED_COST)
                                )
                                .withTotalAnnualDisposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                )
                .withHardshipMetadata(
                        new HardshipMetadata()
                                .withRepId(Constants.TEST_REP_ID)
                                .withCmuId(Constants.TEST_CMU_ID)
                                .withHardshipReviewId(Constants.TEST_HARDSHIP_REVIEW_ID)
                                .withFinancialAssessmentId(Constants.TEST_FINANCIAL_ASSESSMENT_ID)
                                .withReviewReason(NewWorkReason.getFrom(Constants.TEST_NEW_WORK_REASON_STRING))
                                .withReviewStatus(HardshipReviewStatus.COMPLETE)
                                .withNotes(Constants.TEST_CASEWORKER_NOTES)
                                .withDecisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                                .withUserSession(getApiUserSession())
                                .withProgressItems(
                                        List.of(
                                                new HardshipProgress()
                                                        .withAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
                                                        .withResponse(
                                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                        .withDateTaken(Constants.TEST_DATE_REQUESTED_DATETIME)
                                                        .withDateRequired(Constants.TEST_DATE_REQUIRED_DATETIME)
                                                        .withDateCompleted(Constants.TEST_DATE_COMPLETED_DATETIME)
                                        )
                                )
                );
    }

    public static ApiUpdateApplicationRequest getUpdateApplicationRequest() {
        return new ApiUpdateApplicationRequest()
                .withApplicantHistoryId(Constants.TEST_APPLICANT_HISTORY_ID)
                .withCrownRepId(Constants.TEST_REP_ID)
                .withIsImprisoned(Boolean.TRUE)
                .withUserSession(getApiUserSession())
                .withRepId(Constants.TEST_REP_ID)
                .withCaseType(CaseType.EITHER_WAY)
                .withMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .withDecisionReason(DecisionReason.GRANTED)
                .withDecisionDate(Constants.TEST_DECISION_DATETIME)
                .withCommittalDate(Constants.TEST_COMMITAL_DATETIME)
                .withDateReceived(Constants.TEST_DATETIME_RECEIVED)
                .withCrownCourtSummary(getApiCrownCourtSummary())
                .withIojAppeal(getApiIOJAppeal())
                .withFinancialAssessment(getApiFinancialAssessment())
                .withPassportAssessment(getApiPassportAssessment());
    }

    public static ApiCrownCourtSummary getApiCrownCourtSummary() {
        return new ApiCrownCourtSummary()
                .withEvidenceFeeLevel(Constants.TEST_EVIDENCE_FEE_LEVEL_1)
                .withCrownCourtOutcome(List.of(getApiCrownCourtOutcome()))
                .withRepOrderDate(Constants.TEST_CC_REP_ORDER_DATETIME)
                .withRepType(Constants.TEST_CC_REP_TYPE_THROUGH_ORDER.getValue())
                .withRepOrderDecision(Constants.TEST_REP_ORDER_DECISION_GRANTED.getValue())
                .withRepId(Constants.TEST_REP_ID)
                .withSentenceOrderDate(Constants.TEST_SENTENCE_ORDER_DATETIME);
    }

    public static ApiCrownCourtOutcome getApiCrownCourtOutcome() {
        return new ApiCrownCourtOutcome()
                .withDateSet(Constants.TEST_SENTENCE_ORDER_DATETIME)
                .withDescription(Constants.TEST_CC_OUTCOME_DESCRIPTION_CONVICTED)
                .withOutComeType(Constants.TEST_CC_OUTCOME_TYPE_TRIAL)
                .withOutcome(CrownCourtOutcome.CONVICTED);
    }

    public static ApiIOJAppeal getApiIOJAppeal() {
        return new ApiIOJAppeal()
                .withIojResult(Constants.TEST_RESULT_PASS)
                .withDecisionResult(Constants.TEST_RESULT_PASS);
    }

    public static ApiFinancialAssessment getApiFinancialAssessment() {
        return new ApiFinancialAssessment()
                .withInitResult(Constants.TEST_RESULT_FAIL)
                .withInitStatus(CurrentStatus.COMPLETE)
                .withFullResult(Constants.TEST_RESULT_PASS)
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
                .withResult(Constants.TEST_RESULT_FAIL)
                .withStatus(CurrentStatus.COMPLETE);
    }


    public static ApiUserSession getApiUserSession() {
        return new ApiUserSession()
                .withUserName(Constants.TEST_USERNAME)
                .withSessionId(Constants.TEST_USER_SESSION);
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
                               .withId(Constants.TEST_HARDSHIP_REVIEW_PROGRESS_ID)
                               .withProgressResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                               .withDateCompleted(Constants.TEST_DATE_COMPLETED_DATETIME)
                               .withDateRequested(Constants.TEST_DATE_REQUESTED_DATETIME)
                               .withDateRequired(Constants.TEST_DATE_REQUIRED_DATETIME)
                               .withProgressAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
        );
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(Constants.TEST_SOLICITOR_VAT)
                .withDisbursements(Constants.TEST_SOLICITOR_DISBURSEMENTS)
                .withRate(Constants.TEST_SOLICITOR_RATE)
                .withHours(Constants.TEST_SOLICITOR_HOURS)
                .withEstimatedTotal(Constants.TEST_SOLICITOR_ESTIMATED_COST);
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
                                .withId(Constants.TEST_HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.INCOME)
                                .withAmount(amount)
                                .withFrequency(Frequency.MONTHLY)
                                .withAccepted("Y")
                                .withOtherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                .withReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
                                .withDetailCode(HardshipReviewDetailCode.MEDICAL_GROUNDS)
                );
                case EXPENDITURE -> details.add(
                        new ApiHardshipDetail()
                                .withId(Constants.TEST_HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("F")
                                .withDetailReason(HardshipReviewDetailReason.EVIDENCE_SUPPLIED)
                                .withOtherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
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
                .id(Constants.TEST_ASSESSMENT_SUMMARY_ID.longValue())
                .status(AssessmentStatusDTO.COMPLETE)
                .result(ReviewResult.PASS.getResult())
                .assessmentDate(Constants.TEST_ASSESSMENT_SUMMARY_DATE)
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

    public static ApplicationDTO getApplicationDTO(CourtType courtType) {
        return ApplicationDTO.builder()
                .repId(Constants.TEST_REP_ID.longValue())
                .dateReceived(Date.from(Constants.TEST_DATETIME_RECEIVED.atZone(ZoneId.systemDefault()).toInstant()))
                .committalDate(Date.from(Constants.TEST_COMMITAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .decisionDate(Date.from(Constants.TEST_DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .applicantDTO(getApplicantDTO())
                .assessmentDTO(getAssessmentDTO(courtType))
                .crownCourtOverviewDTO(getCrownCourtOverviewDTO())
                .caseDetailsDTO(getCaseDetailDTO())
                .magsOutcomeDTO(getOutcomeDTO(CourtType.MAGISTRATE))
                .passportedDTO(getPassportedDTO())
                .repOrderDecision(getRepOrderDecisionDTO())
                .iojResult(Constants.TEST_RESULT_PASS)
                .build();
    }

    public static ApplicantDTO getApplicantDTO() {
        return ApplicantDTO.builder()
                .applicantHistoryId(Constants.TEST_APPLICANT_HISTORY_ID.longValue())
                .build();
    }

    public static AssessmentDTO getAssessmentDTO(CourtType courtType) {
        return AssessmentDTO.builder()
                .iojAppeal(getIOJAppealDTO())
                .financialAssessmentDTO(getFinancialAssessmentDTO(courtType))
                .build();
    }

    public static CrownCourtOverviewDTO getCrownCourtOverviewDTO() {
        return CrownCourtOverviewDTO.builder()
                .crownCourtSummaryDTO(getCrownCourtSummaryDTO())
                .build();
    }

    public static CrownCourtSummaryDTO getCrownCourtSummaryDTO() {
        return CrownCourtSummaryDTO.builder()
                .ccRepId(Constants.TEST_REP_ID.longValue())
                .ccRepType(Constants.TEST_CC_REP_TYPE_THROUGH_ORDER)
                .ccRepOrderDate(Date.from(Constants.TEST_CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .sentenceOrderDate(Date.from(Constants.TEST_SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .repOrderDecision(Constants.TEST_REP_ORDER_DECISION_GRANTED)
                .inPrisoned(Boolean.TRUE)
                .evidenceProvisionFee(getEvidenceFeeDTO())
                .outcomeDTOs(List.of(getOutcomeDTO(CourtType.CROWN_COURT)))
                .build();
    }

    public static EvidenceFeeDTO getEvidenceFeeDTO() {
        return EvidenceFeeDTO.builder()
                .feeLevel(Constants.TEST_EVIDENCE_FEE_LEVEL_1)
                .build();
    }

    public static IOJAppealDTO getIOJAppealDTO() {
        return IOJAppealDTO.builder()
                .appealDecisionResult(Constants.TEST_RESULT_PASS)
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
                    .dateSet(Date.from(Constants.TEST_SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                    .description(Constants.TEST_CC_OUTCOME_DESCRIPTION_CONVICTED)
                    .outComeType(Constants.TEST_CC_OUTCOME_TYPE_TRIAL)
                    .outcome(CrownCourtOutcome.CONVICTED.getCode())
                    .build();
        } else {
            return OutcomeDTO.builder()
                    .outcome(MagCourtOutcome.SENT_FOR_TRIAL.getOutcome())
                    .build();
        }

    }

    public static PassportedDTO getPassportedDTO() {
        return PassportedDTO.builder()
                .passportedId(Constants.TEST_PASSPORTED_ID.longValue())
                .assessementStatusDTO(getAssessmentStatusDTO())
                .result(Constants.TEST_RESULT_FAIL)
                .build();
    }

    public static RepOrderDecisionDTO getRepOrderDecisionDTO() {
        return RepOrderDecisionDTO.builder()
                .code(DecisionReason.GRANTED.getCode())
                .build();
    }

    public static ContributionsDTO getContributionsDTO() {
        return ContributionsDTO.builder()
                .id(Constants.TEST_CONTRIBUTIONS_ID.longValue())
                .upliftApplied(false)
                .basedOn(Constants.CONTRIBUTION_BASED_ON)
                .calcDate(Constants.TEST_CONTRIBUTION_CALCULATION_DATE)
                .effectiveDate(Constants.TEST_CONTRIBUTION_EFFECTIVE_DATE)
                .monthlyContribs(Constants.TEST_MONTHLY_CONTRIBUTION_AMOUNT)
                .upfrontContribs(Constants.TEST_UPFRONT_CONTRIBUTION_AMOUNT)
                .build();
    }

    public static ApplicationDTO getApplicationDTOWithHardship(CourtType courtType) {
        return ApplicationDTO.builder()
                .courtType(courtType)
                .repId(Constants.TEST_REP_ID.longValue())
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
                .id(Constants.TEST_FINANCIAL_ASSESSMENT_ID.longValue())
                .full(getFullAssessmentDTO())
                .initial(getInitialAssessmentDTO())
                .hardship(getHardshipOverviewDTO(courtType))
                .build();
    }

    public static FullAssessmentDTO getFullAssessmentDTO() {
        return FullAssessmentDTO.builder()
                .assessmentDate(Constants.TEST_ASSESSMENT_DATE)
                .result(Constants.TEST_RESULT_PASS)
                .assessmnentStatusDTO(getAssessmentStatusDTO())
                .build();
    }

    public static InitialAssessmentDTO getInitialAssessmentDTO() {
        return InitialAssessmentDTO.builder()
                .result(Constants.TEST_RESULT_FAIL)
                .assessmnentStatusDTO(getAssessmentStatusDTO())
                .build();
    }

    private static CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return CaseManagementUnitDTO.builder()
                .cmuId(Constants.TEST_CMU_ID.longValue())
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
        return dtoBuilder.build();
    }

    public static HardshipReviewDTO getHardshipReviewDTO() {
        return HardshipReviewDTO.builder()
                .id(Constants.TEST_HARDSHIP_REVIEW_ID.longValue())
                .cmuId(Constants.TEST_CMU_ID.longValue())
                .disposableIncome(Constants.TEST_DISPOSABLE_INCOME)
                .reviewResult(Constants.TEST_HARDSHIP_REVIEW_RESULT)
                .disposableIncomeAfterHardship(Constants.TEST_POST_HARDSHIP_DISPOSABLE_INCOME)
                .reviewDate(Constants.TEST_DATE_REVIEWED)
                .section(TestModelDataBuilder.getHrSectionDtosWithMixedTypes())
                .asessmentStatus(getAssessmentStatusDTO())
                .newWorkReason(getNewWorkReasonDTO())
                .notes(Constants.TEST_CASEWORKER_NOTES)
                .decisionNotes(Constants.TEST_CASEWORKER_DECISION_NOTES)
                .solictorsCosts(getHRSolicitorsCostsDTO())
                .progress(getHrProgressDTOs())
                .build();
    }

    @NotNull
    private static List<HRProgressDTO> getHrProgressDTOs() {
        return List.of(
                HRProgressDTO.builder()
                        .id(Constants.TEST_HARDSHIP_REVIEW_PROGRESS_ID.longValue())
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
                        .dateRequired(Constants.TEST_DATE_REQUIRED)
                        .dateRequested(Constants.TEST_DATE_REQUESTED)
                        .dateCompleted(Constants.TEST_DATE_COMPLETED)
                        .build()
        );
    }

    private static NewWorkReasonDTO getNewWorkReasonDTO() {
        return NewWorkReasonDTO.builder()
                .code(Constants.TEST_NEW_WORK_REASON_STRING)
                .description("New")
                .build();
    }

    private static AssessmentStatusDTO getAssessmentStatusDTO() {
        return AssessmentStatusDTO.builder()
                .status(AssessmentStatusDTO.COMPLETE)
                .description("Complete")
                .build();
    }

    private static HRSolicitorsCostsDTO getHRSolicitorsCostsDTO() {
        return HRSolicitorsCostsDTO.builder()
                .solicitorRate(Constants.TEST_SOLICITOR_RATE)
                .solicitorHours(Constants.TEST_SOLICITOR_HOURS.doubleValue())
                .solicitorDisb(Constants.TEST_SOLICITOR_DISBURSEMENTS)
                .solicitorVat(Constants.TEST_SOLICITOR_VAT)
                .solicitorEstimatedTotalCost(Constants.TEST_SOLICITOR_ESTIMATED_COST)
                .build();
    }

    private static UserDTO getUserDTO() {
        return UserDTO.builder()
                .userName(Constants.TEST_USERNAME)
                .userSession(Constants.TEST_USER_SESSION)
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
                                                .id(Constants.TEST_HARDSHIP_DETAIL_ID.longValue())
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
                                                .otherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
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
                                                .id(Constants.TEST_HARDSHIP_DETAIL_ID.longValue())
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
                                                .hrReasonNote(Constants.TEST_HARDSHIP_REASON_NOTE)
                                                .otherDescription(Constants.TEST_HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(HRReasonDTO.builder().build())
                                                .build()
                                )
                        )
                        .build()
        );
    }
}
