package uk.gov.justice.laa.crime.orchestration.util;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.AssessmentResult;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType.MEANS_FULL;
import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType.MEANS_INIT;

class AssessmentTypeUtilTest {



    @ParameterizedTest
    @MethodSource("workflowRequestForInitialAssessment")
    void givenAValidInput_whenIsInitialAssessmentCompletedIsInvoked_thenExpectedIsReturned(WorkflowRequest request,boolean expected) {
       assertEquals(AssessmentTypeUtil.isInitialAssessmentCompleted(request), expected);
    }

    @ParameterizedTest
    @MethodSource("workflowRequestForFullAssessment")
    void givenAValidInput_whenIsFullAssessmentCompletedIsInvoked_thenExpectedIsReturned(WorkflowRequest request,boolean expected) {
        assertEquals(AssessmentTypeUtil.isFullAssessmentCompleted(request), expected);
    }

    @ParameterizedTest
    @MethodSource("workflowRequestForAssessmentCompleted")
    void givenAValidInput_whenIsAssessmentCompletedIsInvoked_thenExpectedIsReturned(WorkflowRequest request,boolean expected) {
        assertEquals(AssessmentTypeUtil.isAssessmentCompleted(request), expected);
    }

    @ParameterizedTest
    @MethodSource("workflowRequestForGetAssessmentType")
    void givenAValidInput_whenAetAssessmentTypeIsInvoked_thenCorrectAssessmentIsReturned(WorkflowRequest request, ApplicationTrackingOutputResult.AssessmentType expected) {
        assertEquals(AssessmentTypeUtil.getAssessmentType(request), expected);
    }

    private static Stream<Arguments> workflowRequestForInitialAssessment() {

        WorkflowRequest initialAssessmentInProgress = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        initialAssessmentInProgress.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getInitial().getAssessmnentStatusDTO().setStatus(CurrentStatus.IN_PROGRESS.getDescription());

        WorkflowRequest initialAssessmentResult = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        initialAssessmentResult.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getInitial().setResult(AssessmentResult.FULL.getResult());

        return Stream.of(
                Arguments.of(initialAssessmentInProgress, false),
                Arguments.of(initialAssessmentResult, false),
                Arguments.of(TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT), true)

        );
    }

    private static Stream<Arguments> workflowRequestForFullAssessment() {

        WorkflowRequest fullAssessmentInProgress = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        fullAssessmentInProgress.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getFull().getAssessmnentStatusDTO().setStatus(CurrentStatus.IN_PROGRESS.getDescription());

        return Stream.of(
                Arguments.of(fullAssessmentInProgress, false),
                Arguments.of(TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT), true)
        );
    }

    private static Stream<Arguments> workflowRequestForAssessmentCompleted() {

        WorkflowRequest initialAssessmentResult = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        initialAssessmentResult.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getInitial().setResult(AssessmentResult.FULL.getResult());
        initialAssessmentResult.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getFull().getAssessmnentStatusDTO().setStatus(CurrentStatus.IN_PROGRESS.getDescription());

        WorkflowRequest fullAssessmentInProgress = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        fullAssessmentInProgress.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getInitial().getAssessmnentStatusDTO().setStatus(CurrentStatus.IN_PROGRESS.getDescription());
        fullAssessmentInProgress.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getFull().getAssessmnentStatusDTO().setStatus(CurrentStatus.IN_PROGRESS.getDescription());

        return Stream.of(
                Arguments.of(initialAssessmentResult, false),
                Arguments.of(fullAssessmentInProgress, false),
                Arguments.of(TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT), true));
    }



    private static Stream<Arguments> workflowRequestForGetAssessmentType() {

        WorkflowRequest fullAssessmentInProgress = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        fullAssessmentInProgress.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getFull().getAssessmnentStatusDTO().setStatus(CurrentStatus.IN_PROGRESS.getDescription());

        return Stream.of(
                Arguments.of(fullAssessmentInProgress, MEANS_INIT),
                Arguments.of(TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT), MEANS_FULL)
        );
    }

}