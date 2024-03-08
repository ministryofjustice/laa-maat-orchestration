package uk.gov.justice.laa.crime.orchestration.data.builder;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderCCOutcomeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.ReservationsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserValidationDTO;
import uk.gov.justice.laa.crime.orchestration.enums.Action;
import uk.gov.justice.laa.crime.orchestration.enums.AppealType;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtSummary;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiRepOrderCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.orchestration.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipDetail;
import uk.gov.justice.laa.crime.orchestration.model.court_data_api.hardship.ApiHardshipProgress;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.*;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder.*;
import static uk.gov.justice.laa.crime.util.DateUtil.toDate;

@Component
public class TestModelDataBuilder {
    private static final Integer EVIDENCE_ID = 9552473;
    private static final String INCOME_EVIDENCE_DESCRIPTION = "Tax Return";
    private static final String INCOME_EVIDENCE = "TAX RETURN";
    private static final String EVIDENCE_FEE_LEVEL_1 = "LEVEL1";
    private static final Integer PASSPORTED_ID = 777;
    private static final Integer APPLICANT_HISTORY_ID = 666;
    private static final String RESULT_FAIL = "FAIL";
    private static final String RESULT_PASS = "PASS";
    private static final SysGenString REP_ORDER_DECISION_GRANTED = new SysGenString("Granted");
    private static final SysGenString CC_REP_TYPE_THROUGH_ORDER = new SysGenString("Through Order");
    private static final LocalDateTime CC_REP_ORDER_DATETIME = LocalDateTime.of(2022, 10, 13, 0, 0, 0);
    private static final LocalDateTime CC_WITHDRAWAL_DATETIME = LocalDateTime.of(2022, 10, 14, 0, 0, 0);
    private static final LocalDateTime SENTENCE_ORDER_DATETIME = CC_REP_ORDER_DATETIME.plusDays(1);
    private static final LocalDateTime DATETIME_RECEIVED = LocalDateTime.of(2022, 10, 13, 0, 0, 0);
    private static final Date ASSESSMENT_DATE =
            Date.from(DATETIME_RECEIVED.plusDays(2).toInstant(ZoneOffset.UTC));
    private static final LocalDateTime COMMITAL_DATETIME = DATETIME_RECEIVED.plusDays(1);
    private static final LocalDateTime DECISION_DATETIME = COMMITAL_DATETIME.plusDays(1);
    private static final SysGenString CONTRIBUTION_BASED_ON = new SysGenString("Means");
    private static final LocalDateTime CONTRIBUTION_CALCULATION_DATETIME = LocalDateTime.of(2022, 10, 5, 0, 0, 0);
    private static final Date CONTRIBUTION_CALCULATION_DATE =
            Date.from(Instant.ofEpochSecond(CONTRIBUTION_CALCULATION_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final LocalDateTime CONTRIBUTION_EFFECTIVE_DATETIME = LocalDateTime.of(2022, 10, 5, 0, 0, 0);
    private static final Date CONTRIBUTION_EFFECTIVE_DATE =
            Date.from(Instant.ofEpochSecond(CONTRIBUTION_EFFECTIVE_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final BigDecimal UPFRONT_CONTRIBUTION_AMOUNT = BigDecimal.valueOf(2000.00);
    private static final BigDecimal MONTHLY_CONTRIBUTION_AMOUNT = BigDecimal.valueOf(150.00);
    private static final Integer HARDSHIP_REVIEW_PROGRESS_ID = 53;
    private static final String HARDSHIP_REASON_NOTE = "Mock reason note";
    private static final String HARDSHIP_OTHER_DESCRIPTION = "Mock other description";
    private static final String CASEWORKER_DECISION_NOTES = "Mock caseworker decision notes";
    private static final String CASEWORKER_NOTES = "Mock caseworker notes";
    private static final String NEW_WORK_REASON_STRING = NewWorkReason.NEW.getCode();
    private static final String USER_SESSION = "8ab0bab5-c27e-471a-babf-c3992c7a4471";
    public static final Integer REP_ID = 200;
    private static final BigDecimal SOLICITOR_ESTIMATED_COST = BigDecimal.valueOf(2500);
    private static final BigDecimal SOLICITOR_VAT = BigDecimal.valueOf(250);
    private static final BigDecimal SOLICITOR_DISBURSEMENTS = BigDecimal.valueOf(375);
    private static final BigDecimal SOLICITOR_RATE = BigDecimal.valueOf(200);
    // Solicitors Costs
    private static final BigDecimal SOLICITOR_HOURS = BigDecimal.valueOf(50)
            .setScale(1, RoundingMode.DOWN);
    private static final LocalDateTime DATE_REQUIRED_DATETIME = LocalDateTime.of(2022, 12, 15, 0, 0, 0);
    private static final Date DATE_REQUIRED =
            Date.from(Instant.ofEpochSecond(DATE_REQUIRED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final LocalDateTime DATE_REQUESTED_DATETIME = LocalDateTime.of(2022, 11, 11, 0, 0, 0);
    private static final Date DATE_REQUESTED =
            Date.from(Instant.ofEpochSecond(DATE_REQUESTED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final LocalDateTime DATE_REVIEWED_DATETIME = LocalDateTime.of(2022, 11, 12, 0, 0, 0);
    private static final Date DATE_REVIEWED =
            Date.from(Instant.ofEpochSecond(DATE_REVIEWED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final LocalDateTime DATE_MODIFIED_DATETIME = LocalDateTime.of(2022, 10, 13, 0, 0, 0);
    private static final LocalDateTime DATE_COMPLETED_DATETIME = LocalDateTime.of(2022, 11, 14, 0, 0, 0);
    private static final Date DATE_COMPLETED =
            Date.from(Instant.ofEpochSecond(DATE_COMPLETED_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final Integer HARDSHIP_DETAIL_ID = 12345;
    private static final Integer CMU_ID = 50;
    private static final String OTHER_HOUSING_NOTES = "Other Housing Notes";
    private static final String ASSESSMENT_NOTES = "ASSESSMENT NOTES";
    private static final LocalDateTime APPLICATION_TIMESTAMP = LocalDateTime.parse("2024-01-27T10:15:30");
    private static final LocalDateTime REP_ORDER_MODIFIED_TIMESTAMP = LocalDateTime.parse("2023-06-27T10:15:30");
    private static final LocalDate REP_ORDER_CREATED_TIMESTAMP = LocalDate.of(2024, Month.JANUARY, 8);
    private static final List<String> TEST_ROLE_ACTIONS = List.of("CREATE_ASSESSMENT");
    public static final Action TEST_ACTION = Action.CREATE_ASSESSMENT;
    private static final NewWorkReason TEST_NEW_WORK_REASON = NewWorkReason.NEW;
    private static final List<String> TEST_NEW_WORK_REASONS = List.of(NEW_WORK_REASON_STRING);
    private static final String TEST_USER_SESSION = "sessionId_e5712593c198";
    private static final Integer TEST_RECORD_ID = 100;
    private static final LocalDateTime RESERVATION_DATE = LocalDateTime.of(2022, 12, 14, 0, 0, 0);
    public static final String RT_CODE = "DEF";

    public static ApiFindHardshipResponse getApiFindHardshipResponse() {
        return new ApiFindHardshipResponse()
                .withId(Constants.HARDSHIP_REVIEW_ID)
                .withCmuId(CMU_ID)
                .withNotes(CASEWORKER_NOTES)
                .withDecisionNotes(CASEWORKER_DECISION_NOTES)
                .withReviewDate(DATE_REVIEWED_DATETIME)
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
                .withHardshipReviewId(Constants.HARDSHIP_REVIEW_ID)
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
                                                                HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(DeniedIncomeDetailCode.MEDICAL_GROUNDS)
                                                        .withReasonNote(HARDSHIP_REASON_NOTE)
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
                                                                HARDSHIP_OTHER_DESCRIPTION)
                                                        .withItemCode(ExtraExpenditureDetailCode.CAR_LOAN)
                                        )
                                )
                                .withReviewDate(DATE_REVIEWED_DATETIME)
                                .withSolicitorCosts(
                                        new SolicitorCosts()
                                                .withVat(SOLICITOR_VAT)
                                                .withRate(SOLICITOR_RATE)
                                                .withHours(SOLICITOR_HOURS)
                                                .withDisbursements(SOLICITOR_DISBURSEMENTS)
                                                .withEstimatedTotal(SOLICITOR_ESTIMATED_COST)
                                )
                                .withTotalAnnualDisposableIncome(Constants.DISPOSABLE_INCOME)
                )
                .withHardshipMetadata(
                        new HardshipMetadata()
                                .withRepId(REP_ID)
                                .withCmuId(CMU_ID)
                                .withHardshipReviewId(Constants.HARDSHIP_REVIEW_ID)
                                .withFinancialAssessmentId(Constants.FINANCIAL_ASSESSMENT_ID)
                                .withReviewReason(NewWorkReason.getFrom(NEW_WORK_REASON_STRING))
                                .withReviewStatus(HardshipReviewStatus.COMPLETE)
                                .withNotes(CASEWORKER_NOTES)
                                .withDecisionNotes(CASEWORKER_DECISION_NOTES)
                                .withUserSession(getApiUserSession())
                                .withProgressItems(
                                        List.of(
                                                new HardshipProgress()
                                                        .withAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
                                                        .withResponse(
                                                                HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                                                        .withDateTaken(DATE_REQUESTED_DATETIME)
                                                        .withDateRequired(DATE_REQUIRED_DATETIME)
                                                        .withDateCompleted(DATE_COMPLETED_DATETIME)
                                        )
                                )
                );
    }

    public static ApiUpdateApplicationRequest getUpdateApplicationRequest() {
        return new ApiUpdateApplicationRequest()
                .withApplicantHistoryId(APPLICANT_HISTORY_ID)
                .withCrownRepId(REP_ID)
                .withIsImprisoned(Boolean.TRUE)
                .withUserSession(getApiUserSession())
                .withRepId(REP_ID)
                .withCaseType(CaseType.EITHER_WAY)
                .withMagCourtOutcome(MagCourtOutcome.SENT_FOR_TRIAL)
                .withDecisionReason(DecisionReason.GRANTED)
                .withDecisionDate(DECISION_DATETIME)
                .withCommittalDate(COMMITAL_DATETIME)
                .withDateReceived(DATETIME_RECEIVED)
                .withCrownCourtSummary(getApiCrownCourtSummary())
                .withIojAppeal(getApiIOJAppeal())
                .withFinancialAssessment(getApiFinancialAssessment())
                .withPassportAssessment(getApiPassportAssessment());
    }

    public static ApiCrownCourtSummary getApiCrownCourtSummary() {
        return new ApiCrownCourtSummary()
                .withRepId(REP_ID)
                .withRepType(CC_REP_TYPE_THROUGH_ORDER.getValue())
                .withRepOrderDate(CC_REP_ORDER_DATETIME)
                .withSentenceOrderDate(SENTENCE_ORDER_DATETIME)
                .withWithdrawalDate(CC_WITHDRAWAL_DATETIME)
                .withRepOrderDecision(REP_ORDER_DECISION_GRANTED.getValue())
                .withIsImprisoned(Boolean.TRUE)
                .withIsWarrantIssued(Boolean.TRUE)
                .withEvidenceFeeLevel(EvidenceFeeLevel.LEVEL1)
                .withCrownCourtOutcome(List.of(getApiCrownCourtOutcome(CrownCourtOutcome.CONVICTED)));
    }

    public static ApiRepOrderCrownCourtOutcome getApiRepOrderCrownCourtOutcome() {
        return new ApiRepOrderCrownCourtOutcome()
                .withOutcome(CrownCourtOutcome.CONVICTED)
                .withOutcomeDate(SENTENCE_ORDER_DATETIME);
    }

    public static ApiCrownCourtOutcome getApiCrownCourtOutcome(CrownCourtOutcome crownCourtOutcome) {
        return new ApiCrownCourtOutcome()
                .withDateSet(SENTENCE_ORDER_DATETIME)
                .withDescription(crownCourtOutcome.getDescription())
                .withOutComeType(crownCourtOutcome.getType())
                .withOutcome(crownCourtOutcome);
    }

    public static ApiIOJAppeal getApiIOJAppeal() {
        return new ApiIOJAppeal()
                .withIojResult(RESULT_PASS)
                .withDecisionResult(RESULT_PASS);
    }

    public static ApiFinancialAssessment getApiFinancialAssessment() {
        return new ApiFinancialAssessment()
                .withInitResult(RESULT_FAIL)
                .withInitStatus(CurrentStatus.COMPLETE)
                .withFullResult(RESULT_PASS)
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
                .withResult(RESULT_FAIL)
                .withStatus(CurrentStatus.COMPLETE);
    }


    public static ApiUserSession getApiUserSession() {
        return new ApiUserSession()
                .withUserName(Constants.USERNAME)
                .withSessionId(USER_SESSION);
    }

    public static ApiUpdateApplicationResponse getApiUpdateApplicationResponse() {
        return new ApiUpdateApplicationResponse()
                .withCrownRepOrderDate(CC_REP_ORDER_DATETIME)
                .withModifiedDateTime(DATE_MODIFIED_DATETIME)
                .withCrownRepOrderDecision(REP_ORDER_DECISION_GRANTED.toString())
                .withCrownRepOrderType(CC_REP_TYPE_THROUGH_ORDER.toString());
    }

    public static ApiUpdateCrownCourtResponse getApiUpdateCrownCourtResponse() {
        return new ApiUpdateCrownCourtResponse()
                .withModifiedDateTime(DATE_MODIFIED_DATETIME)
                .withCrownCourtSummary(getApiCrownCourtSummary());
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
                .withId(HARDSHIP_REVIEW_PROGRESS_ID)
                .withProgressResponse(HardshipReviewProgressResponse.ADDITIONAL_PROVIDED)
                .withDateCompleted(DATE_COMPLETED_DATETIME)
                .withDateRequested(DATE_REQUESTED_DATETIME)
                .withDateRequired(DATE_REQUIRED_DATETIME)
                .withProgressAction(HardshipReviewProgressAction.SOLICITOR_INFORMED)
        );
    }

    public static SolicitorCosts getSolicitorsCosts() {
        return new SolicitorCosts()
                .withVat(SOLICITOR_VAT)
                .withDisbursements(SOLICITOR_DISBURSEMENTS)
                .withRate(SOLICITOR_RATE)
                .withHours(SOLICITOR_HOURS)
                .withEstimatedTotal(SOLICITOR_ESTIMATED_COST);
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
                                .withId(HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.INCOME)
                                .withAmount(amount)
                                .withFrequency(Frequency.MONTHLY)
                                .withAccepted("Y")
                                .withOtherDescription(HARDSHIP_OTHER_DESCRIPTION)
                                .withReasonNote(HARDSHIP_REASON_NOTE)
                                .withDetailCode(HardshipReviewDetailCode.MEDICAL_GROUNDS)
                );
                case EXPENDITURE -> details.add(
                        new ApiHardshipDetail()
                                .withId(HARDSHIP_DETAIL_ID)
                                .withDetailType(HardshipReviewDetailType.EXPENDITURE)
                                .withAmount(amount)
                                .withFrequency(Frequency.ANNUALLY)
                                .withAccepted("F")
                                .withDetailReason(HardshipReviewDetailReason.EVIDENCE_SUPPLIED)
                                .withOtherDescription(HARDSHIP_OTHER_DESCRIPTION)
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
                .courtType(courtType)
                .applicationDTO(getApplicationDTOWithHardship(courtType))
                .build();
    }

    public static WorkflowRequest buildWorkFlowRequest(CourtType courtType) {
        return WorkflowRequest.builder()
                .userDTO(getUserDTO())
                .courtType(courtType)
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

    public static WorkflowRequest buildWorkFlowRequestForApplicationTimestampValidation() {
        return WorkflowRequest
                .builder()
                .applicationDTO(
                        ApplicationDTO
                                .builder()
                                .repId(123L)
                                .timestamp(APPLICATION_TIMESTAMP)
                                .build()).build();
    }

    public static WorkflowRequest buildWorkFlowRequest(boolean isUpdateAllowed) {
        return WorkflowRequest
                .builder()
                .applicationDTO(
                        ApplicationDTO
                                .builder()
                                .repId(123L)
                                .timestamp(APPLICATION_TIMESTAMP)
                                .statusDTO(RepStatusDTO
                                        .builder()
                                        .updateAllowed(isUpdateAllowed)
                                        .build())
                                .build())
                .build();
    }


    public static ApplicationDTO getApplicationDTO(CourtType courtType) {
        return ApplicationDTO.builder()
                .repId(REP_ID.longValue())
                .dateReceived(Date.from(DATETIME_RECEIVED.atZone(ZoneId.systemDefault()).toInstant()))
                .committalDate(Date.from(COMMITAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .decisionDate(Date.from(DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .applicantDTO(getApplicantDTO())
                .assessmentDTO(getAssessmentDTO(courtType))
                .crownCourtOverviewDTO(getCrownCourtOverviewDTO())
                .caseDetailsDTO(getCaseDetailDTO())
                .offenceDTO(getOffenceDTO())
                .statusDTO(getRepStatusDTO())
                .magsOutcomeDTO(getOutcomeDTO(CourtType.MAGISTRATE))
                .passportedDTO(getPassportedDTO())
                .repOrderDecision(getRepOrderDecisionDTO())
                .iojResult(RESULT_PASS)
                .assessmentSummary(Collections.emptyList())
                .build();
    }

    public static ApplicationDTO getApplicationDTO() {
        return ApplicationDTO.builder()
                .repId(REP_ID.longValue())
                .dateReceived(Date.from(DATETIME_RECEIVED.atZone(ZoneId.systemDefault()).toInstant()))
                .committalDate(Date.from(COMMITAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .decisionDate(Date.from(DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .applicantDTO(getApplicantDTO())
                .assessmentDTO(getAssessmentDTO())
                .caseManagementUnitDTO(getCaseManagementUnitDTO())
                .crownCourtOverviewDTO(getCrownCourtOverviewDTOForContribution())
                .caseDetailsDTO(getCaseDetailDTO())
                .offenceDTO(getOffenceDTO())
                .statusDTO(getRepStatusDTO())
                .magsOutcomeDTO(getOutcomeDTO(CourtType.MAGISTRATE))
                .passportedDTO(getPassportedDTO())
                .repOrderDecision(getRepOrderDecisionDTO())
                .partnerContraryInterestDTO(getContraryInterestDTO())
                .iojResult(RESULT_PASS)
                .assessmentSummary(Collections.emptyList())
                .build();
    }

    public static ChildWeightingDTO getChildWeightingDTO() {
        return ChildWeightingDTO.builder()
                .weightingId(37L)
                .noOfChildren(1)
                .build();
    }

    public static ContraryInterestDTO getContraryInterestDTO() {
        return ContraryInterestDTO.builder()
                .code("ContraryInterest")
                .description("Contrary Interest")
                .build();
    }

    public static ApplicantDTO getApplicantDTO() {
        return ApplicantDTO.builder()
                .id(Long.valueOf(1000))
                .applicantHistoryId(APPLICANT_HISTORY_ID.longValue())
                .employmentStatusDTO(getEmploymentStatusDTO())
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
                .ccRepId(REP_ID.longValue())
                .ccRepType(CC_REP_TYPE_THROUGH_ORDER)
                .ccRepOrderDate(Date.from(CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .sentenceOrderDate(Date.from(SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .ccWithDrawalDate(Date.from(CC_WITHDRAWAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .repOrderDecision(REP_ORDER_DECISION_GRANTED)
                .inPrisoned(Boolean.TRUE)
                .benchWarrantyIssued(Boolean.TRUE)
                .evidenceProvisionFee(getEvidenceFeeDTO())
                .outcomeDTOs(List.of(getOutcomeDTO(CourtType.CROWN_COURT)))
                .build();
    }

    public static CrownCourtSummaryDTO getCrownCourtSummaryDTOForContribution() {
        return CrownCourtSummaryDTO.builder()
                .ccRepId(REP_ID.longValue())
                .ccRepType(CC_REP_TYPE_THROUGH_ORDER)
                .ccRepOrderDate(Date.from(CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .ccWithDrawalDate(Date.from(CC_WITHDRAWAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .sentenceOrderDate(Date.from(SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .repOrderDecision(REP_ORDER_DECISION_GRANTED)
                .inPrisoned(Boolean.TRUE)
                .evidenceProvisionFee(getEvidenceFeeDTO())
                .outcomeDTOs(List.of(getOutcomeDTO(CourtType.CROWN_COURT), getOutcomeDTO()))
                .build();
    }

    public static EvidenceFeeDTO getEvidenceFeeDTO() {
        return EvidenceFeeDTO.builder()
                .feeLevel(EVIDENCE_FEE_LEVEL_1)
                .build();
    }

    public static IOJAppealDTO getIOJAppealDTO() {
        return IOJAppealDTO.builder()
                .appealDecisionResult(RESULT_PASS)
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
                    .dateSet(Date.from(SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
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
                .dateSet(Date.from(SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .description(CrownCourtOutcome.SUCCESSFUL.getDescription())
                .outComeType(CrownCourtOutcome.SUCCESSFUL.getType())
                .outcome(CrownCourtOutcome.SUCCESSFUL.toString())
                .build();
    }

    public static PassportedDTO getPassportedDTO() {
        return PassportedDTO.builder()
                .passportedId(PASSPORTED_ID.longValue())
                .newWorkReason(getNewWorkReasonDTO())
                .assessementStatusDTO(getAssessmentStatusDTO())
                .result(RESULT_FAIL)
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
                .basedOn(CONTRIBUTION_BASED_ON)
                .calcDate(new SysGenDate(CONTRIBUTION_CALCULATION_DATE))
                .effectiveDate(new SysGenDate(CONTRIBUTION_EFFECTIVE_DATE))
                .monthlyContribs(MONTHLY_CONTRIBUTION_AMOUNT)
                .upfrontContribs(UPFRONT_CONTRIBUTION_AMOUNT)
                .build();
    }

    public static ApplicationDTO getApplicationDTOWithHardship(CourtType courtType) {
        return ApplicationDTO.builder()
                .repId(REP_ID.longValue())
                .caseManagementUnitDTO(getCaseManagementUnitDTO())
                .crownCourtOverviewDTO(CrownCourtOverviewDTO.builder().build())
                .statusDTO(getRepStatusDTO())
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
                .assessmentNotes(ASSESSMENT_NOTES)
                .assessmentDate(ASSESSMENT_DATE)
                .result(RESULT_PASS)
                .assessmnentStatusDTO(getAssessmentStatusDTO())
                .otherHousingNote(OTHER_HOUSING_NOTES)
                .build();
    }

    public static InitialAssessmentDTO getInitialAssessmentDTO() {
        return InitialAssessmentDTO.builder()
                .result(RESULT_FAIL)
                .newWorkReason(getNewWorkReasonDTO())
                .reviewType(getReviewTypeDTO())
                .assessmnentStatusDTO(getAssessmentStatusDTO())
                .totalAggregatedIncome(TOTAL_AGGREGATED_INCOME.doubleValue())
                .sectionSummaries(List.of(getSectionSummaryDTO()))
                .childWeightings(List.of(getChildWeightingDTO()))
                .build();
    }


    private static CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return CaseManagementUnitDTO.builder()
                .cmuId(CMU_ID.longValue())
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
                .cmuId(CMU_ID.longValue())
                .disposableIncome(Constants.DISPOSABLE_INCOME)
                .reviewResult(Constants.HARDSHIP_REVIEW_RESULT)
                .disposableIncomeAfterHardship(Constants.POST_HARDSHIP_DISPOSABLE_INCOME)
                .reviewDate(DATE_REVIEWED)
                .section(TestModelDataBuilder.getHrSectionDtosWithMixedTypes())
                .asessmentStatus(getAssessmentStatusDTO())
                .newWorkReason(getNewWorkReasonDTO())
                .notes(CASEWORKER_NOTES)
                .decisionNotes(CASEWORKER_DECISION_NOTES)
                .solictorsCosts(getHRSolicitorsCostsDTO())
                .progress(getHrProgressDTOs())
                .build();
    }

    private static List<HRProgressDTO> getHrProgressDTOs() {
        return List.of(
                HRProgressDTO.builder()
                        .id(HARDSHIP_REVIEW_PROGRESS_ID.longValue())
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
                        .dateRequired(DATE_REQUIRED)
                        .dateRequested(DATE_REQUESTED)
                        .dateCompleted(DATE_COMPLETED)
                        .build()
        );
    }

    private static NewWorkReasonDTO getNewWorkReasonDTO() {
        return NewWorkReasonDTO.builder()
                .code(NEW_WORK_REASON_STRING)
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
                .solicitorRate(SOLICITOR_RATE)
                .solicitorHours(SOLICITOR_HOURS.doubleValue())
                .solicitorDisb(SOLICITOR_DISBURSEMENTS)
                .solicitorVat(SOLICITOR_VAT)
                .solicitorEstimatedTotalCost(SOLICITOR_ESTIMATED_COST)
                .build();
    }

    private static UserDTO getUserDTO() {
        return UserDTO.builder()
                .userName(Constants.USERNAME)
                .userSession(USER_SESSION)
                .build();
    }

    public static IncomeEvidenceSummaryDTO getIncomeEvidenceSummaryDTO() {
        return IncomeEvidenceSummaryDTO.builder()
                .upliftAppliedDate(toDate(LocalDateTime.of(2023, 11, 18, 0, 0, 0)))
                .upliftRemovedDate(toDate(LocalDateTime.of(2023, 11, 18, 0, 0, 0)))
                .incomeEvidenceNotes("Income Evidence Notes")
                .applicantIncomeEvidenceList(List.of(getEvidenceDTO()))
                .partnerIncomeEvidenceList(List.of(getEvidenceDTO()))
                .evidenceReceivedDate(toDate(LocalDateTime.of(2023, 2, 18, 0, 0, 0)))
                .evidenceDueDate(toDate(LocalDateTime.of(2023, 3, 18, 0, 0, 0)))
                .upliftsAvailable(true)
                .build();
    }

    private static EvidenceDTO getEvidenceDTO() {
        return EvidenceDTO.builder()
                .id(EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(toDate(LocalDateTime.of(2023, 11, 18, 0, 0, 0)))
                .otherDescription("OTHER DESCRIPTION")
                .selected(true)
                .build();
    }

    private static EvidenceTypeDTO getEvidenceTypeDTO() {
        return EvidenceTypeDTO.builder()
                .evidence(INCOME_EVIDENCE)
                .description(INCOME_EVIDENCE_DESCRIPTION)
                .build();
    }

    private static AppealDTO getAppealDTO() {
        return AppealDTO.builder()
                .available(true)
                .appealReceivedDate(toDate(LocalDateTime.of(2023, 3, 18, 0, 0, 0)))
                .appealSentenceOrderDate(toDate(LocalDateTime.of(2023, 8, 3, 0, 0, 0)))
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
                .contributionCap(100.0)
                .build();
    }

    private static RepStatusDTO getRepStatusDTO() {
        return RepStatusDTO.builder()
                .status("RepStatus")
                .removeContribs(true)
                .updateAllowed(true)
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
                                                .id(HARDSHIP_DETAIL_ID.longValue())
                                                .detailDescription(
                                                        HRDetailDescriptionDTO.builder()
                                                                .code(HardshipReviewDetailCode.CAR_LOAN.getCode())
                                                                .description(HardshipReviewDetailCode.CAR_LOAN.getDescription())
                                                                .build())
                                                .accepted(false)
                                                .frequency(FrequenciesDTO.builder()
                                                        .code(Frequency.ANNUALLY.getCode())
                                                        .annualWeighting(
                                                                (long) Frequency.ANNUALLY.getWeighting())
                                                        .description(Frequency.ANNUALLY.getDescription())
                                                        .build())
                                                .amountNumber(BigDecimal.valueOf(2000.00))
                                                .otherDescription(HARDSHIP_OTHER_DESCRIPTION)
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
                                                .id(HARDSHIP_DETAIL_ID.longValue())
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
                                                                (long) Frequency.MONTHLY.getWeighting())
                                                        .description(Frequency.MONTHLY.getDescription())
                                                        .build())
                                                .amountNumber(BigDecimal.valueOf(1500.00))
                                                .hrReasonNote(HARDSHIP_REASON_NOTE)
                                                .otherDescription(HARDSHIP_OTHER_DESCRIPTION)
                                                .reason(HRReasonDTO.builder().build())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    public static AssessmentStatusDTO getAssessmentStatusDTO(CurrentStatus status) {
        return AssessmentStatusDTO.builder()
                .status(status.getStatus())
                .description(status.getDescription())
                .build();
    }

    public static EmploymentStatusDTO getEmploymentStatusDTO() {
        return EmploymentStatusDTO.builder()
                .code("EMPLOY")
                .description("Employed")
                .build();
    }

    public static ApplicationDTO getApplicationDTOForMeansAssessmentMapper(Boolean isFullAssessmentAvailable) {
        ApplicationDTO applicationDTO = new ApplicationDTO();
        AssessmentDTO assessmentDTO = new AssessmentDTO();
        FinancialAssessmentDTO financialAssessmentDTO = getFinancialAssessmentDTOForMeansAssessmentMapper(isFullAssessmentAvailable);
        assessmentDTO.setFinancialAssessmentDTO(financialAssessmentDTO);
        applicationDTO.setAssessmentDTO(assessmentDTO);
        return applicationDTO;
    }

    private static FinancialAssessmentDTO getFinancialAssessmentDTOForMeansAssessmentMapper(Boolean isFullAssessmentAvailable) {
        FinancialAssessmentDTO financialAssessmentDTO = new FinancialAssessmentDTO();
        InitialAssessmentDTO initialAssessmentDTO = getInitialAssessmentDTOForMeansAssessmentMapper();
        FullAssessmentDTO fullAssessmentDTO = getFullAssessmentDTOForMeansAssessmentMapper();
        financialAssessmentDTO.setInitial(initialAssessmentDTO);
        financialAssessmentDTO.setFull(fullAssessmentDTO);
        financialAssessmentDTO.setFullAvailable(isFullAssessmentAvailable);
        return financialAssessmentDTO;
    }

    private static FullAssessmentDTO getFullAssessmentDTOForMeansAssessmentMapper() {
        FullAssessmentDTO fullAssessmentDTO = getFullAssessmentDTO();
        fullAssessmentDTO.setSectionSummaries(List.of(getAssessmentSectionSummaryDTOForMeansAssessmentMapper()));
        return fullAssessmentDTO;
    }

    private static InitialAssessmentDTO getInitialAssessmentDTOForMeansAssessmentMapper() {
        InitialAssessmentDTO initialAssessmentDTO = new InitialAssessmentDTO();
        initialAssessmentDTO.setReviewType(new ReviewTypeDTO());
        initialAssessmentDTO.setSectionSummaries(List.of(getAssessmentSectionSummaryDTOForMeansAssessmentMapper()));
        ChildWeightingDTO childWeightingDTO = new ChildWeightingDTO();
        childWeightingDTO.setWeightingId(37L);
        initialAssessmentDTO.setChildWeightings(List.of(childWeightingDTO));
        return initialAssessmentDTO;
    }

    private static AssessmentSectionSummaryDTO getAssessmentSectionSummaryDTOForMeansAssessmentMapper() {
        AssessmentSectionSummaryDTO assessmentSectionSummaryDTO = new AssessmentSectionSummaryDTO();
        assessmentSectionSummaryDTO.setSection(SECTION);
        AssessmentDetailDTO assessmentDetailDTO = new AssessmentDetailDTO();
        assessmentDetailDTO.setCriteriaDetailsId(CRITERIA_DETAIL_ID.longValue());
        assessmentSectionSummaryDTO.setAssessmentDetail(List.of(assessmentDetailDTO));
        return assessmentSectionSummaryDTO;
    }

    public static RepOrderDTO buildRepOrderDTOWithModifiedDate() {
        return RepOrderDTO.builder().dateModified(REP_ORDER_MODIFIED_TIMESTAMP).build();
    }

    public static RepOrderDTO buildRepOrderDTOWithCreatedDateAndNoModifiedDate() {
        return RepOrderDTO.builder().dateCreated(REP_ORDER_CREATED_TIMESTAMP).dateModified(null).build();
    }

    public static RepOrderDTO buildRepOrderDTO(String rorsStatus) {
        return RepOrderDTO.builder().id(1000).dateModified(APPLICATION_TIMESTAMP).rorsStatus(rorsStatus).build();
    }

    public static UserSummaryDTO getUserSummaryDTO() {
        return UserSummaryDTO.builder()
                .username(Constants.USERNAME)
                .roleActions(TEST_ROLE_ACTIONS)
                .newWorkReasons(TEST_NEW_WORK_REASONS)
                .reservationsEntity(getReservationsDTO())
                .build();
    }

    public static ReservationsDTO getReservationsDTO() {
        return ReservationsDTO.builder()
                .recordId(TEST_RECORD_ID)
                .recordName("")
                .userName(Constants.USERNAME)
                .userSession(TEST_USER_SESSION)
                .reservationDate(RESERVATION_DATE)
                .expiryDate(RESERVATION_DATE)
                .build();
    }

    public static UserValidationDTO getUserValidationDTO() {
        return UserValidationDTO.builder()
                .username(Constants.USERNAME)
                .action(TEST_ACTION)
                .newWorkReason(TEST_NEW_WORK_REASON)
                .sessionId(USER_SESSION).build();
    }

    public static UserValidationDTO getUserValidationDTOWithReservation() {
        return UserValidationDTO.builder()
                .username(Constants.USERNAME)
                .action(TEST_ACTION)
                .newWorkReason(TEST_NEW_WORK_REASON)
                .sessionId(TEST_USER_SESSION).build();
    }

    public static UserValidationDTO getUserValidationDTOInvalidValidRequest() {
        return UserValidationDTO.builder()
                .username(Constants.USERNAME)
                .action(null)
                .newWorkReason(null)
                .sessionId(null).build();
    }

    @NotNull
    public static RepOrderDTO getTestRepOrderDTO(ApplicationDTO applicationDTO) {
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.setArrestSummonsNo(applicationDTO.getArrestSummonsNo());
        repOrderDTO.setSuppAccountCode(applicationDTO.getArrestSummonsNo());
        repOrderDTO.setEvidenceFeeLevel(applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getEvidenceProvisionFee().getFeeLevel());
        repOrderDTO.setMacoCourt(null);
        repOrderDTO.setMagsOutcome(applicationDTO.getMagsOutcomeDTO().getOutcome());
        repOrderDTO.setDateReceived(null);
        repOrderDTO.setCrownRepOrderDate(null);
        repOrderDTO.setOftyOffenceType(applicationDTO.getOffenceDTO().getOffenceType());
        repOrderDTO.setCrownWithdrawalDate(null);
        repOrderDTO.setCaseId(applicationDTO.getCaseId());
        repOrderDTO.setCommittalDate(null);
        repOrderDTO.setApplicantHistoryId(null);
        repOrderDTO.setRorsStatus(applicationDTO.getStatusDTO().getStatus());
        repOrderDTO.setRepOrderCCOutcome(null);
        repOrderDTO.setAppealTypeCode(applicationDTO.getCrownCourtOverviewDTO().getAppealDTO().getAppealTypeDTO().getCode());
        return repOrderDTO;
    }

    @NotNull
    public static ApplicationDTO getTestApplicationDTO(WorkflowRequest workflowRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.setDateReceived(null);
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcRepOrderDate(null);
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcWithDrawalDate(null);
        applicationDTO.setCommittalDate(null);
        applicationDTO.getApplicantDTO().setApplicantHistoryId(null);
        return applicationDTO;
    }

    @NotNull
    public static RepOrderCCOutcomeDTO getRepOrderCCOutcomeDTO() {
        return RepOrderCCOutcomeDTO.builder()
                .id(1)
                .repId(1)
                .outcomeDate(LocalDateTime.now())
                .outcome("CONVICTED")
                .build();
    }

    public static UserSummaryDTO getUserSummaryDTO(List<String> roleActions, NewWorkReason newWorkReason) {
        return UserSummaryDTO.builder()
                .username(Constants.USERNAME)
                .roleActions(roleActions)
                .newWorkReasons(List.of(newWorkReason.getCode()))
                .reservationsEntity(getReservationsDTO())
                .build();
    }

    public static uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO getMaatApiFinancialAssessmentDTO() {
        return uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO.builder()
                .id(Constants.FINANCIAL_ASSESSMENT_ID)
                .fassInitStatus(CurrentStatus.COMPLETE.getStatus())
                .fassFullStatus(CurrentStatus.COMPLETE.getStatus())
                .assessmentType(AssessmentType.FULL.getType())
                .initResult(RESULT_PASS)
                .fullResult(RESULT_PASS)
                .initialAssessmentDate(INITIAL_ASSESSMENT_DATE)
                .fullAssessmentDate(FULL_ASSESSMENT_DATE)
                .rtCode(RT_CODE)
                .build();
    }

    public static AssessmentSummaryDTO getAssessmentSummaryDTOFromFullFinancialAssessment() {
        return AssessmentSummaryDTO.builder()
                .id(Constants.FINANCIAL_ASSESSMENT_ID.longValue())
                .status(CurrentStatus.COMPLETE.getStatus())
                .type("Full Means Test")
                .result(RESULT_PASS)
                .assessmentDate(DateUtil.toDate(FULL_ASSESSMENT_DATE))
                .reviewType(RT_CODE)
                .build();
    }

    public static AssessmentSummaryDTO getAssessmentSummaryDTOFromInitFinancialAssessment() {
        return AssessmentSummaryDTO.builder()
                .id(Constants.FINANCIAL_ASSESSMENT_ID.longValue())
                .status(CurrentStatus.COMPLETE.getStatus())
                .type("Initial Assessment")
                .result(RESULT_PASS)
                .assessmentDate(DateUtil.toDate(INITIAL_ASSESSMENT_DATE))
                .reviewType(RT_CODE)
                .build();
    }
}
