package uk.gov.justice.laa.crime.orchestration.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.common.model.meansassessment.*;
import uk.gov.justice.laa.crime.enums.*;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;

import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FeatureToggleDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggle;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;

import static uk.gov.justice.laa.crime.orchestration.data.Constants.FINANCIAL_ASSESSMENT_ID;
import static uk.gov.justice.laa.crime.util.DateUtil.toDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toZonedDateTime;

@Component
public class MeansAssessmentDataBuilder {
    public static final BigDecimal ANNUAL_DISPOSABLE_INCOME = BigDecimal.valueOf(1000.00);
    private static final Integer PARTNER_EVIDENCE_ID = 9552473;
    private static final Integer APPLICANT_EVIDENCE_ID = 552473;
    private static final Integer PARTNER_ID = 88;
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
    private static final LocalDateTime SENTENCE_ORDER_DATETIME = CC_REP_ORDER_DATETIME.plusDays(1);
    private static final LocalDateTime DATETIME_RECEIVED = LocalDateTime.of(2022, 10, 13, 0, 0, 0);
    private static final LocalDateTime COMMITTAL_DATETIME = DATETIME_RECEIVED.plusDays(1);
    private static final LocalDateTime DECISION_DATETIME = COMMITTAL_DATETIME.plusDays(1);
    public static final LocalDateTime INITIAL_ASSESSMENT_DATE = DATETIME_RECEIVED.plusDays(2);
    public static final LocalDateTime FULL_ASSESSMENT_DATE = INITIAL_ASSESSMENT_DATE;
    private static final Date ASSESSMENT_DATE = toDate(INITIAL_ASSESSMENT_DATE);
    private static final String FULL_ASSESSMENT_NOTES = "Mock full assessment notes";
    private static final Frequency FREQUENCY = Frequency.MONTHLY;
    private static final Frequency PARTNER_FREQUENCY = Frequency.TWO_WEEKLY;
    private static final BigDecimal APPLICANT_VALUE = BigDecimal.valueOf(1000);
    private static final Integer ASSESSMENT_DETAIL_ID = 134;
    public static final Integer CRITERIA_DETAIL_ID = 135;
    private static final BigDecimal APPLICANT_ANNUAL_TOTAL = BigDecimal.valueOf(12000);
    private static final BigDecimal PARTNER_ANNUAL_TOTAL = BigDecimal.valueOf(12000);
    private static final BigDecimal ANNUAL_TOTAL = APPLICANT_ANNUAL_TOTAL.add(PARTNER_ANNUAL_TOTAL);
    private static final String EMPLOYMENT_STATUS = "EMPLOY";
    private static final String INCOME_EVIDENCE_NOTES = "Mock Income evidence notes";
    private static final LocalDateTime INCOME_UPLIFT_APPLY_DATE =
            LocalDateTime.of(2021, 12, 12, 0, 0, 0);
    private static final LocalDateTime INCOME_UPLIFT_REMOVE_DATE =
            INCOME_UPLIFT_APPLY_DATE.plusDays(10);
    private static final LocalDateTime INCOME_EVIDENCE_DUE_DATE =
            LocalDateTime.of(2020, 10, 5, 0, 0, 0);
    private static final LocalDateTime INCOME_EVIDENCE_RECEIVED_DATE =
            LocalDateTime.of(2020, 10, 1, 0, 0, 0);
    private static final LocalDateTime FIRST_REMINDER_DATE =
            LocalDateTime.of(2020, 10, 2, 0, 0, 0);
    private static final LocalDateTime SECOND_REMINDER_DATE =
            LocalDateTime.of(2020, 10, 2, 0, 0, 0);
    private static final String TRANSACTION_ID = "7c49ebfe-fe3a-4f2f-8dad-f7b8f03b8327";
    private static final BigDecimal THRESHOLD = BigDecimal.valueOf(5000.00);
    private static final BigDecimal AGGREGATED_EXPENSE = BigDecimal.valueOf(22000.00);
    private static final Integer USN = 123456789;
    private static final String OTHER_HOUSING_NOTE = "Mock other housing note";
    private static final String ASSESSMENT_NOTES = "Mock assessment notes";
    private static final Integer CRITERIA_ID = 34;
    private static final SysGenString CONTRIBUTION_BASED_ON = new SysGenString("Means");
    private static final LocalDateTime CONTRIBUTION_CALCULATION_DATETIME = LocalDateTime.of(2022, 10, 5, 0, 0, 0);
    private static final Date CONTRIBUTION_CALCULATION_DATE =
            Date.from(Instant.ofEpochSecond(CONTRIBUTION_CALCULATION_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final LocalDateTime CONTRIBUTION_EFFECTIVE_DATETIME = LocalDateTime.of(2022, 10, 5, 0, 0, 0);
    private static final Date CONTRIBUTION_EFFECTIVE_DATE =
            Date.from(Instant.ofEpochSecond(CONTRIBUTION_EFFECTIVE_DATETIME.toEpochSecond(ZoneOffset.UTC)));
    private static final BigDecimal UPFRONT_CONTRIBUTION_AMOUNT = BigDecimal.valueOf(2000.00);
    private static final BigDecimal MONTHLY_CONTRIBUTION_AMOUNT = BigDecimal.valueOf(150.00);
    private static final String NEW_WORK_REASON_STRING = NewWorkReason.NEW.getCode();
    private static final String USER_SESSION = "8ab0bab5-c27e-471a-babf-c3992c7a4471";
    private static final String USERNAME = "mock-u";
    private static final Integer REP_ID = 200;
    private static final Integer CMU_ID = 50;
    private static final String OTHER_BENEFIT_NOTE = "Mock other benefit note";
    private static final String OTHER_INCOME_NOTE = "Mock other income note";
    public static final BigDecimal LOWER_THRESHOLD = BigDecimal.valueOf(12500.0);
    public static final BigDecimal UPPER_THRESHOLD = BigDecimal.valueOf(22500.0);
    public static final BigDecimal TOTAL_AGGREGATED_INCOME = BigDecimal.valueOf(15600.00);
    public static final BigDecimal ADJUSTED_INCOME_VALUE = BigDecimal.valueOf(15600.00);
    public static final BigDecimal ADJUSTED_LIVING_ALLOWANCE = BigDecimal.valueOf(15600.00);
    public static final String RESULT_REASON = "FullAssessmentResult.PASS.getReason()";
    public static final String CRITERIA_DETAIL_CODE = "Mock assessment detail code";
    public static final String ASSESSMENT_DESCRIPTION = "Mock assessment description";
    public static final BigDecimal PARTNER_AMOUNT = BigDecimal.valueOf(2000);
    private static final LocalDateTime DATE_MODIFIED = LocalDateTime.of(2023, 10, 13, 10, 15, 30);
    private static final ZonedDateTime TIME_STAMP =  toZonedDateTime(DATE_MODIFIED);
    public static final String SECTION = "INITA";
    public static final Integer INIT_MEANS_ID = 90;
    private static final LocalDateTime PARTNER_EVIDENCE_RECEIVED_DATE = LocalDateTime.of(2020, 9, 13, 0, 0, 0);
    private static final LocalDateTime APPLICANT_EVIDENCE_RECEIVED_DATE = LocalDateTime.of(2020, 10, 1, 0, 0, 0);
    public static final Date APPEAL_RECEIVED_DATE = new GregorianCalendar(2023, Calendar.MARCH, 18).getTime();

    private static final LocalDateTime APPLICATION_TIMESTAMP = LocalDateTime.of(2022, 10, 1, 0, 0, 0);
    public static final Date APPEAL_SENTENCE_ORDER_DATE = new GregorianCalendar(2023, Calendar.AUGUST, 3).getTime();
    public static final String OTHER_DESCRIPTION = "OTHER DESCRIPTION";
    private static final Integer EXTRA_EVIDENCE_ID = 52473;

    public static ApiUserSession getApiUserSession() {
        return new ApiUserSession()
                .withUserName(USERNAME)
                .withSessionId(USER_SESSION);
    }

    public static WorkflowRequest buildWorkFlowRequest() {
        return WorkflowRequest.builder()
                .userDTO(getUserDTO())
                .applicationDTO(getApplicationDTO())
                .build();
    }

    public static ApplicationDTO getApplicationDTO() {
        return ApplicationDTO.builder()
                .repId(REP_ID.longValue())
                .dateReceived(Date.from(DATETIME_RECEIVED.atZone(ZoneId.systemDefault()).toInstant()))
                .committalDate(Date.from(COMMITTAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .decisionDate(Date.from(DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .applicantDTO(getApplicantDTO())
                .assessmentDTO(getAssessmentDTO())
                .crownCourtOverviewDTO(getCrownCourtOverviewDTOForContribution())
                .caseDetailsDTO(getCaseDetailDTO())
                .caseManagementUnitDTO(getCaseManagementUnitDTO())
                .offenceDTO(getOffenceDTO())
                .statusDTO(getRepStatusDTO())
                .magsOutcomeDTO(getOutcomeDTO(CourtType.MAGISTRATE))
                .passportedDTO(getPassportedDTO())
                .repOrderDecision(getRepOrderDecisionDTO())
                .iojResult(RESULT_PASS)
                .build();
    }

    public static ApplicantDTO getApplicantDTO() {
        return ApplicantDTO.builder()
                .id(Constants.APPLICANT_ID.longValue())
                .employmentStatusDTO(getEmploymentStatusDTO())
                .applicantHistoryId(APPLICANT_HISTORY_ID.longValue())
                .build();
    }

    public static EmploymentStatusDTO getEmploymentStatusDTO() {
        return EmploymentStatusDTO.builder()
                .code("EMPLOY")
                .description("Employed")
                .build();
    }

    public static AssessmentDTO getAssessmentDTO() {
        return AssessmentDTO.builder()
                .iojAppeal(getIOJAppealDTO())
                .financialAssessmentDTO(getFinancialAssessmentDto())
                .build();
    }

    public static CrownCourtOverviewDTO getCrownCourtOverviewDTOForContribution() {
        return CrownCourtOverviewDTO.builder()
                .appealDTO(getAppealDTO())
                .contribution(getContributionsDTO())
                .crownCourtSummaryDTO(getCrownCourtSummaryDTOForContribution())
                .build();
    }

    public static CrownCourtSummaryDTO getCrownCourtSummaryDTOForContribution() {
        return CrownCourtSummaryDTO.builder()
                .ccRepId(REP_ID.longValue())
                .ccRepType(CC_REP_TYPE_THROUGH_ORDER)
                .ccRepOrderDate(Date.from(CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .sentenceOrderDate(Date.from(SENTENCE_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()))
                .repOrderDecision(REP_ORDER_DECISION_GRANTED)
                .inPrisoned(Boolean.TRUE)
                .evidenceProvisionFee(getEvidenceFeeDTO())
                .outcomeDTOs(List.of(getOutcomeDTO()))
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

    private static CaseManagementUnitDTO getCaseManagementUnitDTO() {
        return CaseManagementUnitDTO.builder()
                .cmuId(CMU_ID.longValue())
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
                .assessementStatusDTO(getAssessmentStatusDTO(CurrentStatus.COMPLETE))
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


    public static FullAssessmentDTO getFullAssessmentDTO() {
        return FullAssessmentDTO.builder()
                .adjustedLivingAllowance(ADJUSTED_LIVING_ALLOWANCE.doubleValue())
                .assessmentDate(ASSESSMENT_DATE)
                .assessmentNotes(FULL_ASSESSMENT_NOTES)
                .assessmnentStatusDTO(getAssessmentStatusDTO(CurrentStatus.COMPLETE))
                .criteriaId(CRITERIA_ID.longValue())
                .otherHousingNote(OTHER_HOUSING_NOTE)
                .result(RESULT_PASS)
                .resultReason(RESULT_REASON)
                .sectionSummaries(List.of(getSectionSummaryDTO()))
                .threshold(THRESHOLD.doubleValue())
                .totalAggregatedExpense(AGGREGATED_EXPENSE.doubleValue())
                .totalAnnualDisposableIncome(ANNUAL_DISPOSABLE_INCOME.doubleValue())
                .build();
    }

    public static AssessmentSectionSummaryDTO getSectionSummaryDTO() {
        return AssessmentSectionSummaryDTO.builder()
                .assessmentDetail(List.of(getSectionDetail()))
                .annualTotal(ANNUAL_TOTAL.doubleValue())
                .partnerAnnualTotal(PARTNER_ANNUAL_TOTAL.doubleValue())
                .applicantAnnualTotal(APPLICANT_ANNUAL_TOTAL.doubleValue())
                .section(SECTION)
                .build();
    }

    private static AssessmentDetailDTO getSectionDetail() {
        return AssessmentDetailDTO.builder()
                .id(ASSESSMENT_DETAIL_ID.longValue())
                .applicantAmount(APPLICANT_VALUE.doubleValue())
                .description(ASSESSMENT_DESCRIPTION)
                .applicantFrequency(getFrequencyDTO(FREQUENCY))
                .criteriaDetailsId(CRITERIA_DETAIL_ID.longValue())
                .detailCode(CRITERIA_DETAIL_CODE)
                .partnerAmount(PARTNER_AMOUNT.doubleValue())
                .partnerFrequency(getFrequencyDTO(PARTNER_FREQUENCY))
                .timestamp(TIME_STAMP)
                .build();
    }

    private static FrequenciesDTO getFrequencyDTO(Frequency frequency) {
        return FrequenciesDTO.builder()
                .annualWeighting((long) frequency.getWeighting())
                .description(frequency.getDescription())
                .code(frequency.getCode())
                .build();
    }

    public static InitialAssessmentDTO getInitialAssessmentDTO() {
        return InitialAssessmentDTO.builder()
                .id(INIT_MEANS_ID.longValue())
                .adjustedIncomeValue(ADJUSTED_INCOME_VALUE.doubleValue())
                .assessmentDate(ASSESSMENT_DATE)
                .assessmnentStatusDTO(getAssessmentStatusDTO(CurrentStatus.COMPLETE))
                .childWeightings(getChildWeightings())
                .lowerThreshold(LOWER_THRESHOLD.doubleValue())
                .newWorkReason(getNewWorkReasonDTO())
                .notes(ASSESSMENT_NOTES)
                .otherBenefitNote(OTHER_BENEFIT_NOTE)
                .otherIncomeNote(OTHER_INCOME_NOTE)
                .reviewType(getReviewTypeDTO())
                .result(RESULT_PASS)
                .resultReason(InitAssessmentResult.PASS.getReason())
                .sectionSummaries(List.of(getSectionSummaryDTO()))
                .totalAggregatedIncome(TOTAL_AGGREGATED_INCOME.doubleValue())
                .upperThreshold(UPPER_THRESHOLD.doubleValue())
                .build();
    }

    public static ReviewTypeDTO getReviewTypeDTO() {
        return ReviewTypeDTO.builder()
                .description(ReviewType.ER.getDescription())
                .code(ReviewType.ER.getCode())
                .build();
    }

    public static UserSummaryDTO getUserSummaryDTO() {
        return UserSummaryDTO.builder()
            .featureToggle(List.of(
                FeatureToggleDTO.builder()
                    .featureName(FeatureToggle.CALCULATE_CONTRIBUTION.getName())
                    .action(FeatureToggleAction.CREATE.getName())
                    .build()
            ))
            .build();
    }

    private static List<ChildWeightingDTO> getChildWeightings() {
        return List.of(
                ChildWeightingDTO.builder()
                        .id(1234L)
                        .noOfChildren(1)
                        .weightingId(37L)
                        .weightingFactor(0.0)
                        .build(),
                ChildWeightingDTO.builder()
                        .id(2345L)
                        .weightingId(38L)
                        .noOfChildren(2)
                        .weightingFactor(0.0)
                        .build()
        );
    }

    private static NewWorkReasonDTO getNewWorkReasonDTO() {
        return NewWorkReasonDTO.builder()
                .code(NEW_WORK_REASON_STRING)
                .description("New")
                .type("HARDIOJ")
                .build();
    }

    private static UserDTO getUserDTO() {
        return UserDTO.builder()
                .userName(USERNAME)
                .userSession(USER_SESSION)
                .build();
    }

    public static IncomeEvidenceSummaryDTO getIncomeEvidenceSummaryDTO() {
        return IncomeEvidenceSummaryDTO.builder()
                .upliftAppliedDate(toDate(INCOME_UPLIFT_APPLY_DATE))
                .upliftRemovedDate(toDate(INCOME_UPLIFT_REMOVE_DATE))
                .incomeEvidenceNotes(INCOME_EVIDENCE_NOTES)
                .applicantIncomeEvidenceList(List.of(getApplicantEvidenceDTO()))
                .partnerIncomeEvidenceList(List.of(getPartnerEvidenceDTO()))
                .extraEvidenceList(List.of(getExtraIncomeEvidenceDTO()))
                .evidenceReceivedDate(toDate(INCOME_EVIDENCE_RECEIVED_DATE))
                .evidenceDueDate(toDate(INCOME_EVIDENCE_DUE_DATE))
                .firstReminderDate(toDate(FIRST_REMINDER_DATE))
                .secondReminderDate(toDate(SECOND_REMINDER_DATE))
                .enabled(Boolean.FALSE)
                .build();
    }

    private static ExtraEvidenceDTO getExtraIncomeEvidenceDTO() {
        return ExtraEvidenceDTO.builder()
                .adhoc("Y")
                .id(EXTRA_EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(toDate(DATETIME_RECEIVED))
                .otherText(OTHER_DESCRIPTION)
                .mandatory(true)
                .timestamp(TIME_STAMP)
                .build();
    }

    private static EvidenceDTO getApplicantEvidenceDTO() {
        return EvidenceDTO.builder()
                .id(APPLICANT_EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(toDate(APPLICANT_EVIDENCE_RECEIVED_DATE))
                .timestamp(TIME_STAMP)
                .build();
    }

    private static EvidenceDTO getPartnerEvidenceDTO() {
        return EvidenceDTO.builder()
                .id(PARTNER_EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(toDate(PARTNER_EVIDENCE_RECEIVED_DATE))
                .timestamp(TIME_STAMP)
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
                .appealReceivedDate(APPEAL_RECEIVED_DATE)
                .appealSentenceOrderDate(APPEAL_SENTENCE_ORDER_DATE)
                .appealTypeDTO(getAppealTypeDTO())
                .build();
    }

    private static AppealTypeDTO getAppealTypeDTO() {
        return AppealTypeDTO.builder()
                .code(AppealType.ACS.getCode())
                .description(AppealType.ACS.getDescription())
                .build();
    }

    private static OffenceDTO getOffenceDTO() {
        return OffenceDTO.builder()
                .offenceType("Offence Type")
                .contributionCap(100d)
                .build();
    }

    private static RepStatusDTO getRepStatusDTO() {
        return RepStatusDTO.builder()
                .status("RepStatus")
                .removeContribs(true)
                .build();
    }

    public static ApiGetMeansAssessmentResponse getApiGetMeansAssessmentResponse() {
        return new ApiGetMeansAssessmentResponse()
                .withId(FINANCIAL_ASSESSMENT_ID)
                .withCriteriaId(CRITERIA_ID)
                .withFullAvailable(true)
                .withUsn(USN)
                .withFullAssessment(getApiFullAssessment(CurrentStatus.COMPLETE))
                .withInitialAssessment(getApiInitialMeansAssessment(CurrentStatus.COMPLETE, NewWorkReason.NEW, ReviewType.ER))
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary());
    }

    public static ApiInitialMeansAssessment getApiInitialMeansAssessment(CurrentStatus currentStatus, NewWorkReason newWorkReason,
                                                                         ReviewType reviewType) {
        ApiInitialMeansAssessment apiInitialMeansAssessment = new ApiInitialMeansAssessment();
        apiInitialMeansAssessment.setId(INIT_MEANS_ID);
        apiInitialMeansAssessment.setAssessmentDate(INITIAL_ASSESSMENT_DATE);
        apiInitialMeansAssessment.setOtherBenefitNote(OTHER_BENEFIT_NOTE);
        apiInitialMeansAssessment.setOtherIncomeNote(OTHER_INCOME_NOTE);
        apiInitialMeansAssessment.setTotalAggregatedIncome(TOTAL_AGGREGATED_INCOME);
        apiInitialMeansAssessment.setAdjustedIncomeValue(ADJUSTED_INCOME_VALUE);
        apiInitialMeansAssessment.setNotes(ASSESSMENT_NOTES);
        apiInitialMeansAssessment.setLowerThreshold(LOWER_THRESHOLD);
        apiInitialMeansAssessment.setUpperThreshold(UPPER_THRESHOLD);
        apiInitialMeansAssessment.setResult(InitAssessmentResult.PASS.getResult());
        apiInitialMeansAssessment.setResultReason(InitAssessmentResult.PASS.getReason());
        if (null != currentStatus) {
            ApiAssessmentStatus apiAssessmentStatus = new ApiAssessmentStatus();
            apiAssessmentStatus.setStatus(currentStatus.getStatus());
            apiAssessmentStatus.setDescription(currentStatus.getDescription());
            apiInitialMeansAssessment.setAssessmentStatus(apiAssessmentStatus);
        }

        if (null != newWorkReason) {
            ApiNewWorkReason apiNewWorkReason = new ApiNewWorkReason();
            apiNewWorkReason.setCode(newWorkReason.getCode());
            apiNewWorkReason.setDescription(newWorkReason.getDescription());
            apiNewWorkReason.setType(newWorkReason.getType());
            apiInitialMeansAssessment.setNewWorkReason(apiNewWorkReason);
        }

        if (null != reviewType) {
            ApiReviewType rType = new ApiReviewType();
            rType.setCode(reviewType.getCode());
            rType.setDescription(reviewType.getDescription());
            apiInitialMeansAssessment.setReviewType(rType);
        }
        apiInitialMeansAssessment.setAssessmentSectionSummary(List.of(getApiAssessmentSectionSummary()));
        apiInitialMeansAssessment.setChildWeighting(getAssessmentChildWeightings());

        return apiInitialMeansAssessment;
    }

    public static ApiFullMeansAssessment getApiFullAssessment(CurrentStatus currentStatus) {
        ApiAssessmentStatus assessmentStatus = null;
        if (currentStatus != null) {
            assessmentStatus = new ApiAssessmentStatus()
                    .withStatus(currentStatus.getStatus())
                    .withDescription(currentStatus.getDescription());
        }
        return new ApiFullMeansAssessment()
                .withCriteriaId(CRITERIA_ID)
                .withAssessmentDate(FULL_ASSESSMENT_DATE)
                .withAssessmentNotes(FULL_ASSESSMENT_NOTES)
                .withAdjustedLivingAllowance(ADJUSTED_LIVING_ALLOWANCE)
                .withOtherHousingNote(OTHER_HOUSING_NOTE)
                .withTotalAggregatedExpense(AGGREGATED_EXPENSE)
                .withTotalAnnualDisposableIncome(ANNUAL_DISPOSABLE_INCOME)
                .withThreshold(THRESHOLD)
                .withResult(AssessmentResult.PASS.toString())
                .withResultReason(RESULT_REASON)
                .withAssessmentSectionSummary(List.of(getApiAssessmentSectionSummary()))
                .withAssessmentStatus(assessmentStatus);
    }

    public static ApiCreateMeansAssessmentRequest getApiCreateMeansAssessmentRequest() {
        return new ApiCreateMeansAssessmentRequest()
                .withLaaTransactionId(TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withReviewType(ReviewType.NAFI)
                .withRepId(REP_ID)
                .withCmuId(CMU_ID)
                .withInitialAssessmentDate(INITIAL_ASSESSMENT_DATE)
                .withNewWorkReason(NewWorkReason.NEW)
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withCaseType(CaseType.EITHER_WAY)
                .withAssessmentStatus(CurrentStatus.COMPLETE)
                .withChildWeightings(getAssessmentChildWeightings())
                .withUserSession(getApiUserSession())
                .withEmploymentStatus(EMPLOYMENT_STATUS)
                .withUsn(USN)
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
                .withIncomeEvidenceNotes(INCOME_EVIDENCE_NOTES)
                .withEvidenceDueDate(INCOME_EVIDENCE_DUE_DATE)
                .withUpliftAppliedDate(INCOME_UPLIFT_APPLY_DATE)
                .withEvidenceReceivedDate(INCOME_EVIDENCE_RECEIVED_DATE)
                .withFirstReminderDate(FIRST_REMINDER_DATE)
                .withSecondReminderDate(SECOND_REMINDER_DATE)
                .withUpliftRemovedDate(INCOME_UPLIFT_REMOVE_DATE)
                .withIncomeEvidence(getIncomeEvidence());
    }

    private static List<ApiIncomeEvidence> getIncomeEvidence() {
        return List.of(
                new ApiIncomeEvidence()
                        .withId(APPLICANT_EVIDENCE_ID)
                        .withApplicantId(Constants.APPLICANT_ID)
                        .withDateReceived(APPLICANT_EVIDENCE_RECEIVED_DATE)
                        .withDateModified(DATE_MODIFIED)
                        .withApiEvidenceType(getApiEvidenceType()),
                new ApiIncomeEvidence()
                        .withId(PARTNER_EVIDENCE_ID)
                        .withApplicantId(PARTNER_ID)
                        .withDateReceived(PARTNER_EVIDENCE_RECEIVED_DATE)
                        .withDateModified(DATE_MODIFIED)
                        .withApiEvidenceType(getApiEvidenceType()),
                new ApiIncomeEvidence()
                        .withAdhoc("Y")
                        .withId(EXTRA_EVIDENCE_ID)
                        .withApiEvidenceType(getApiEvidenceType())
                        .withDateReceived(DATETIME_RECEIVED)
                        .withOtherText(OTHER_DESCRIPTION)
                        .withMandatory("true")
                        .withDateModified(DATE_MODIFIED)
        );
    }

    private static ApiEvidenceType getApiEvidenceType() {
        return new ApiEvidenceType()
                .withCode(INCOME_EVIDENCE)
                .withDescription(INCOME_EVIDENCE_DESCRIPTION);
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
                .withApplicantAnnualTotal(APPLICANT_ANNUAL_TOTAL)
                .withAnnualTotal(ANNUAL_TOTAL)
                .withPartnerAnnualTotal(PARTNER_ANNUAL_TOTAL)
                .withSection(SECTION)
                .withAssessmentDetails(
                        new ArrayList<>(
                                List.of(
                                        new ApiAssessmentDetail()
                                                .withId(ASSESSMENT_DETAIL_ID)
                                                .withCriteriaDetailId(CRITERIA_DETAIL_ID)
                                                .withApplicantAmount(APPLICANT_VALUE)
                                                .withApplicantFrequency(FREQUENCY)
                                                .withAssessmentDetailCode(CRITERIA_DETAIL_CODE)
                                                .withAssessmentDescription(ASSESSMENT_DESCRIPTION)
                                                .withPartnerAmount(PARTNER_AMOUNT)
                                                .withPartnerFrequency(PARTNER_FREQUENCY)
                                                .withDateModified(DATE_MODIFIED)
                                )
                        )
                );
    }

    public static ApiUpdateMeansAssessmentRequest getApiUpdateMeansAssessmentRequest() {
        return new ApiUpdateMeansAssessmentRequest()
                .withLaaTransactionId(TRANSACTION_ID)
                .withAssessmentType(AssessmentType.INIT)
                .withRepId(REP_ID)
                .withCmuId(CMU_ID)
                .withInitialAssessmentDate(INITIAL_ASSESSMENT_DATE)
                .withFullAssessmentDate(FULL_ASSESSMENT_DATE)
                .withIncomeEvidenceSummary(getApiIncomeEvidenceSummary())
                .withHasPartner(true)
                .withPartnerContraryInterest(false)
                .withOtherHousingNote(OTHER_HOUSING_NOTE)
                .withInitTotalAggregatedIncome(TOTAL_AGGREGATED_INCOME)
                .withFullAssessmentNotes(FULL_ASSESSMENT_NOTES)
                .withCaseType(CaseType.EITHER_WAY)
                .withEmploymentStatus(EMPLOYMENT_STATUS)
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
                .status(status.getStatus())
                .description(status.getDescription())
                .build();
    }

    public static FinancialAssessmentDTO getFinancialAssessmentDto() {
        return FinancialAssessmentDTO.builder()
                .id(FINANCIAL_ASSESSMENT_ID.longValue())
                .criteriaId(CRITERIA_ID.longValue())
                .usn(USN.longValue())
                .fullAvailable(true)
                .full(getFullAssessmentDTO())
                .initial(getInitialAssessmentDTO())
                .incomeEvidence(getIncomeEvidenceSummaryDTO())
                .build();
    }

    public static ApiMeansAssessmentResponse getApiMeansAssessmentResponse() {
        return new ApiMeansAssessmentResponse()
                .withAssessmentId(FINANCIAL_ASSESSMENT_ID)
                .withAssessmentSectionSummary(getAssessmentSectionSummary())
                .withAssessmentSummary(getAssessmentSummary())
                .withAssessmentType(AssessmentType.HARDSHIP)
                .withFullAssessmentAvailable(Boolean.TRUE)
                .withFullAssessmentDate(FULL_ASSESSMENT_DATE)
                .withInitialAssessmentDate(INITIAL_ASSESSMENT_DATE)
                .withFullAssessmentDate(FULL_ASSESSMENT_DATE)
                .withAdjustedIncomeValue(ADJUSTED_INCOME_VALUE)
                .withAdjustedLivingAllowance(ADJUSTED_LIVING_ALLOWANCE)
                .withApplicationTimestamp(APPLICATION_TIMESTAMP)
                .withChildWeightings(getAssessmentChildWeightings())
                .withCriteriaId(CRITERIA_DETAIL_ID)
                .withFassFullStatus(CurrentStatus.COMPLETE)
                .withFassInitStatus(CurrentStatus.IN_PROGRESS)
                .withFullResult(RESULT_PASS)
                .withFullResultReason(RESULT_REASON)
                .withFullThreshold(BigDecimal.valueOf(100))
                .withInitResult(RESULT_PASS)
                .withInitResultReason(RESULT_REASON)
                .withLowerThreshold(LOWER_THRESHOLD)
                .withRepId(REP_ID)
                .withReviewType(ReviewType.ER)
                .withTotalAggregatedExpense(AGGREGATED_EXPENSE)
                .withTotalAggregatedIncome(TOTAL_AGGREGATED_INCOME)
                .withTotalAnnualDisposableIncome(ANNUAL_DISPOSABLE_INCOME)
                .withTransactionDateTime(APPLICATION_TIMESTAMP)
                .withUpdated(APPLICATION_TIMESTAMP)
                .withUpperThreshold(UPPER_THRESHOLD);

    }

    private static List<ApiAssessmentSectionSummary> getAssessmentSectionSummary() {
        return List.of(
                new ApiAssessmentSectionSummary()
                        .withAssessmentType(AssessmentType.HARDSHIP)
                        .withAnnualTotal(ANNUAL_TOTAL)
                        .withAssessmentDetails(List.of(new ApiAssessmentDetail()
                                .withId(ASSESSMENT_DETAIL_ID)
                                .withAssessmentDetailCode(CRITERIA_DETAIL_CODE)
                                .withApplicantAmount(APPLICANT_VALUE)
                                .withAssessmentDescription(ASSESSMENT_DESCRIPTION)
                                .withCriteriaDetailId(CRITERIA_DETAIL_ID)
                                .withApplicantFrequency(FREQUENCY)
                                .withPartnerAmount(PARTNER_AMOUNT)
                                .withPartnerFrequency(PARTNER_FREQUENCY)
                                .withDateModified(DATE_MODIFIED)))
                        .withApplicantAnnualTotal(APPLICANT_ANNUAL_TOTAL)
                        .withPartnerAnnualTotal(PARTNER_ANNUAL_TOTAL)
                        .withSection(SECTION));
    }

    private static List<ApiAssessmentSummary> getAssessmentSummary() {
        return List.of(
                new ApiAssessmentSummary()
                        .withId(ASSESSMENT_DETAIL_ID)
                        .withAssessmentDate(FULL_ASSESSMENT_DATE)
                        .withType(WorkType.INITIAL_ASSESSMENT)
                        .withResult(RESULT_PASS)
                        .withReviewType(ReviewType.ER.getCode())
                        .withStatus(CurrentStatus.IN_PROGRESS.getStatus())
        );
    }

    public static ApiUpdateApplicationResponse getApiUpdateApplicationResponse() {
        return new ApiUpdateApplicationResponse()
                .withModifiedDateTime(DATE_MODIFIED)
                .withCrownRepOrderDecision(REP_ORDER_DECISION_GRANTED.getValue())
                .withCrownRepOrderType(CC_REP_TYPE_THROUGH_ORDER.getValue())
                .withCrownRepOrderDate(CC_REP_ORDER_DATETIME);
    }
    public static ApiRollbackMeansAssessmentResponse getApiRollbackMeansAssessmentResponse(String assessmentType) {
        return new ApiRollbackMeansAssessmentResponse()
                .withAssessmentType(assessmentType)
                .withFassFullStatus(CurrentStatus.COMPLETE)
                .withFullResult(AssessmentResult.PASS.toString())
                .withFassInitStatus(CurrentStatus.COMPLETE)
                .withInitResult(AssessmentResult.FULL.toString());
    }
}
