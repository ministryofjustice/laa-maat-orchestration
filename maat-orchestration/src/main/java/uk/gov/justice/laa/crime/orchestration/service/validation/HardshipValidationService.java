package uk.gov.justice.laa.crime.orchestration.service.validation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.enums.RequestType;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.model.hardship.*;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipValidationService {

    private final MaatCourtDataService maatCourtDataService;

    public static final String CANNOT_MODIFY_COMPLETE_HARDSHIP_ERROR =
            "Cannot modify a complete hardship review";
    public static final String INCOMPLETE_ASSESSMENT_ERROR =
            "Hardship review can only be entered after a completed assessment";
    public static final String REVIEW_DATE_ERROR =
            "Hardship review date precedes the initial or full assessment date(s)";
    public static final String REVIEW_STATUS_ERROR =
            "Review Date must be entered for completed hardship";
    public static final String NEW_WORK_REASON_ERROR = "Review Reason must be entered for hardship";
    public static final String SOLICITOR_DETAILS_ERROR =
            "Solicitor Number of Hours must be entered when Solicitor Hourly Rate is specified";
    public static final String EXPENDITURE_OR_DENIED_INCOME_ERROR =
            "Amount, Frequency, and Reason must be entered for each detail in section ";
    public static final String PROGRESSION_ITEMS_ERROR =
            "Date Taken, Response Required, and Date Required must be entered for each Action Taken in section Review Progress";

    public void validate(final ApiPerformHardshipRequest apiPerformHardshipRequest, RequestType requestType) {
        validateHardshipReviewStatus(apiPerformHardshipRequest);
        validateHardshipReviewNewWorkReason(apiPerformHardshipRequest);
        validateSolicitorDetails(apiPerformHardshipRequest);
        validateDeniedIncome(apiPerformHardshipRequest);
        validateExpenditure(apiPerformHardshipRequest);
        validateProgressionItems(apiPerformHardshipRequest);
        validateReviewDate(apiPerformHardshipRequest);
        if (requestType == RequestType.UPDATE) {
            validateUpdate(apiPerformHardshipRequest);
        }
    }

    private void validateUpdate(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        ApiFindHardshipResponse hardship =
                maatCourtDataService.getHardship(apiPerformHardshipRequest.getHardshipMetadata().getHardshipReviewId());
        if (hardship.getStatus().equals(HardshipReviewStatus.COMPLETE)) {
            throw new ValidationException(CANNOT_MODIFY_COMPLETE_HARDSHIP_ERROR);
        }
    }

    private void validateReviewDate(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        FinancialAssessmentDTO financialAssessment =
                maatCourtDataService.getFinancialAssessment(
                        apiPerformHardshipRequest.getHardshipMetadata().getFinancialAssessmentId()
                );

        if (isNull(financialAssessment)
                || "Y".equals(financialAssessment.getReplaced())
                || isNull(financialAssessment.getDateCompleted())) {
            throw new ValidationException(INCOMPLETE_ASSESSMENT_ERROR);
        }

        var reviewDate = apiPerformHardshipRequest.getHardship().getReviewDate();
        var initialAssessmentDate = financialAssessment.getInitialAssessmentDate();
        var fullAssessmentDate = financialAssessment.getFullAssessmentDate();
        var assessmentDate = fullAssessmentDate != null ? fullAssessmentDate : initialAssessmentDate;

        if (reviewDate.isBefore(assessmentDate)) {
            throw new ValidationException(REVIEW_DATE_ERROR);
        }

    }

    private void validateHardshipReviewStatus(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        if (hardshipStatusIsCompleteWithoutReviewDate(apiPerformHardshipRequest)) {
            throw new ValidationException(REVIEW_STATUS_ERROR);
        }
    }

    private void validateHardshipReviewNewWorkReason(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        if (isNull(apiPerformHardshipRequest.getHardshipMetadata().getReviewReason())) {
            throw new ValidationException(NEW_WORK_REASON_ERROR);
        }
    }

    private void validateSolicitorDetails(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        var solicitorCosts = apiPerformHardshipRequest.getHardship().getSolicitorCosts();
        var solicitorRate = BigDecimal.ZERO;
        var solicitorHours = BigDecimal.ZERO;
        if (solicitorCosts != null) {
            solicitorRate = Optional.ofNullable(solicitorCosts.getRate()).orElse(BigDecimal.ZERO);
            solicitorHours = Optional.ofNullable(solicitorCosts.getHours()).orElse(BigDecimal.ZERO);
        }
        if (solicitorRateSpecifiedWithoutSolicitorHours(solicitorRate, solicitorHours)) {
            throw new ValidationException(SOLICITOR_DETAILS_ERROR);
        }
    }

    private void validateDeniedIncome(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<DeniedIncome> deniedIncomes = apiPerformHardshipRequest.getHardship().getDeniedIncome();
        Optional.ofNullable(deniedIncomes).orElse(List.of()).forEach(deniedIncome -> {
            if (deniedIncomeWithoutAmountOrFrequencyOrReasonNote(deniedIncome)) {
                throw new ValidationException(
                        EXPENDITURE_OR_DENIED_INCOME_ERROR + deniedIncome.getItemCode().getDescription());
            }
        });
    }

    private void validateExpenditure(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<ExtraExpenditure> expenditures = apiPerformHardshipRequest.getHardship().getExtraExpenditure();
        Optional.ofNullable(expenditures).orElse(List.of()).forEach(expenditure -> {
            if (expenditureWithoutAmountOrFrequencyOrReasonCode(expenditure)) {
                throw new ValidationException(
                        EXPENDITURE_OR_DENIED_INCOME_ERROR + expenditure.getItemCode().getDescription());
            }
        });
    }

    private void validateProgressionItems(ApiPerformHardshipRequest apiPerformHardshipRequest) {
        List<HardshipProgress> progressionItems = apiPerformHardshipRequest.getHardshipMetadata().getProgressItems();
        Optional.ofNullable(progressionItems).orElse(List.of()).forEach(progression -> {
            if (progressionItemWithoutRequiredDateOrResponseOrDateTaken(progression)) {
                throw new ValidationException(PROGRESSION_ITEMS_ERROR);
            }
        });
    }

    private static boolean progressionItemWithoutRequiredDateOrResponseOrDateTaken(HardshipProgress progression) {
        return (nonNull(progression.getAction()) &&
                (isNull(progression.getDateRequired()) || isNull(progression.getResponse()) ||
                        isNull(progression.getDateTaken())));
    }

    private static boolean expenditureWithoutAmountOrFrequencyOrReasonCode(ExtraExpenditure expenditure) {
        return (nonNull(expenditure.getItemCode()) &&
                (isNull(expenditure.getAmount()) || isNull(expenditure.getFrequency()) ||
                        isNull(expenditure.getReasonCode())));
    }

    private static boolean hardshipStatusIsCompleteWithoutReviewDate(
            ApiPerformHardshipRequest apiPerformHardshipRequest) {
        return ((apiPerformHardshipRequest.getHardshipMetadata().getReviewStatus() == HardshipReviewStatus.COMPLETE)
                && isNull(apiPerformHardshipRequest.getHardship().getReviewDate()));
    }

    private static boolean solicitorRateSpecifiedWithoutSolicitorHours(BigDecimal solicitorRate,
                                                                       BigDecimal solicitorHours) {
        return (solicitorRate.compareTo(BigDecimal.ZERO) > 0) && (solicitorHours.intValue() == 0);
    }

    private static boolean deniedIncomeWithoutAmountOrFrequencyOrReasonNote(DeniedIncome deniedIncome) {
        return (nonNull(deniedIncome.getItemCode()) &&
                (isNull(deniedIncome.getAmount()) || isNull(deniedIncome.getFrequency()) ||
                        StringUtils.isEmpty(deniedIncome.getReasonNote())));
    }
}
