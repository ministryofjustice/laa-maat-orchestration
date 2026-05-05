package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.exception.RollbackException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import java.util.function.Consumer;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class IojAppealsOrchestrationServiceTest {
    private static final int EXISTING_APPEAL_ID = 1;
    private static final String APPEAL_ID = "654";

    @Mock
    private ApplicationService applicationService;

    @Mock
    private AssessmentSummaryService assessmentSummaryService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private IojAppealService iojAppealService;

    @Mock
    private IojAppealMapper iojAppealMapper;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private ProceedingsService proceedingsService;

    @Mock
    private RepOrderService repOrderService;

    @Mock
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Mock
    private ApplicationTrackingMapper applicationTrackingMapper;

    @InjectMocks
    private IojAppealsOrchestrationService iojAppealsOrchestrationService;

    @Test
    void givenAppealId_whenFindIsInvoked_thenIojAppealServiceIsCalled() {
        iojAppealsOrchestrationService.find(EXISTING_APPEAL_ID);

        verify(iojAppealService).find(EXISTING_APPEAL_ID);
    }

    @Test
    void givenWorkflowRequest_whenCreateIsInvoked_thenApplicationDTOIsUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        IOJAppealDTO iojAppealDTO =
                workflowRequest.getApplicationDTO().getAssessmentDTO().getIojAppeal();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(workflowRequest.getApplicationDTO());
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        ApplicationTrackingOutputResult applicationTrackingOutputResult = new ApplicationTrackingOutputResult();

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(iojAppealMapper.getUserActionDTO(workflowRequest)).thenReturn(userActionDTO);
        when(proceedingsService.determineMagsRepDecision(workflowRequest))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(contributionService.calculate(workflowRequest)).thenReturn(workflowRequest.getApplicationDTO());
        when(maatCourtDataService.invokeStoredProcedure(any(), any(), any()))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(applicationTrackingMapper.build(any(), any(), any(), any())).thenReturn(applicationTrackingOutputResult);

        AssessmentSummaryDTO assessmentSummaryDTO = TestModelDataBuilder.getAssessmentSummaryDTOFromIojAppealDTO();
        when(assessmentSummaryService.getSummary(iojAppealDTO)).thenReturn(assessmentSummaryDTO);

        iojAppealsOrchestrationService.create(workflowRequest);

        verify(iojAppealService).create(workflowRequest);

        verify(workflowPreProcessorService).preProcessRequest(workflowRequest, repOrderDTO, userActionDTO);
        verify(proceedingsService).determineMagsRepDecision(workflowRequest);
        verify(contributionService).calculate(workflowRequest);
        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.PRE_UPDATE_CC_APPLICATION);
        verify(proceedingsService).updateApplication(workflowRequest, repOrderDTO);
        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE);
        verify(applicationService).updateDateModified(workflowRequest, workflowRequest.getApplicationDTO());

        verify(assessmentSummaryService, times(1)).getSummary(iojAppealDTO);
        verify(assessmentSummaryService).updateApplication(workflowRequest.getApplicationDTO(), assessmentSummaryDTO);
    }

    @Test
    void givenPostProcessingFailure_whenCreateIsInvokedAndRollbackSuccessful_thenMaatOrchestrationExceptionThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(workflowRequest.getApplicationDTO());
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(iojAppealMapper.getUserActionDTO(workflowRequest)).thenReturn(userActionDTO);
        when(proceedingsService.determineMagsRepDecision(workflowRequest))
                .thenThrow(new RuntimeException("Runtime Exception"));

        assertThatThrownBy(() -> iojAppealsOrchestrationService.create(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);
    }

    @ParameterizedTest
    @MethodSource("exceptions")
    void givenPostProcessingFailure_whenCreateIsInvokedAndRollbackUnsuccessful_thenRollbackExceptionIsThrown(
            Exception exception) {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(workflowRequest.getApplicationDTO());
        UserActionDTO userActionDTO = TestModelDataBuilder.getUserActionDTO();
        RuntimeException runtimeError = new RuntimeException("Runtime Exception on Post-Processing");

        when(iojAppealService.create(workflowRequest)).thenReturn(APPEAL_ID);
        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(iojAppealMapper.getUserActionDTO(workflowRequest)).thenReturn(userActionDTO);
        when(proceedingsService.determineMagsRepDecision(workflowRequest)).thenThrow(runtimeError);
        doThrow(exception).when(iojAppealService).rollback(APPEAL_ID, workflowRequest);

        assertThatThrownBy(() -> iojAppealsOrchestrationService.create(workflowRequest))
                .isInstanceOf(RollbackException.class)
                .hasSuppressedException(runtimeError)
                // RollbackException is rethrown so does not contain a cause
                .hasCause(exception instanceof RollbackException ? null : exception);
    }

    static Stream<Exception> exceptions() {
        RuntimeException runtimeException = new RuntimeException("Runtime Exception on rollback");
        RollbackException rollbackException = new RollbackException(new ApplicationDTO());
        return Stream.of(runtimeException, rollbackException);
    }

    static Stream<Arguments> requiredFieldNullers() {
        return Stream.of(
                Arguments.of("applicationDTO", (Consumer<WorkflowRequest>) req -> req.setApplicationDTO(null)),
                Arguments.of("applicationDTO.repId", (Consumer<WorkflowRequest>)
                        req -> req.getApplicationDTO().setRepId(null)),
                Arguments.of("applicationDTO.dateReceived", (Consumer<WorkflowRequest>)
                        req -> req.getApplicationDTO().setDateReceived(null)),
                Arguments.of("applicationDTO.assessmentDTO", (Consumer<WorkflowRequest>)
                        req -> req.getApplicationDTO().setAssessmentDTO(null)),
                Arguments.of("assessmentDTO.iojAppeal", (Consumer<WorkflowRequest>)
                        req -> req.getApplicationDTO().getAssessmentDTO().setIojAppeal(null)),
                Arguments.of("iojAppeal.cmuId", (Consumer<WorkflowRequest>) req -> req.getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .setCmuId(null)),
                Arguments.of("iojAppeal.receivedDate", (Consumer<WorkflowRequest>) req -> req.getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .setReceivedDate(null)),
                Arguments.of("iojAppeal.newWorkReasonDTO", (Consumer<WorkflowRequest>) req -> req.getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .setNewWorkReasonDTO(null)),
                Arguments.of(
                        "iojAppeal.newWorkReasonDTO.code", (Consumer<WorkflowRequest>) req -> req.getApplicationDTO()
                                .getAssessmentDTO()
                                .getIojAppeal()
                                .getNewWorkReasonDTO()
                                .setCode(null)),
                Arguments.of("iojAppeal.appealReason", (Consumer<WorkflowRequest>) req -> req.getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .setAppealReason(null)),
                Arguments.of("iojAppeal.appealReason.code", (Consumer<WorkflowRequest>) req -> req.getApplicationDTO()
                        .getAssessmentDTO()
                        .getIojAppeal()
                        .getAppealReason()
                        .setCode(null)));
    }

    @ParameterizedTest(name = "missing {0} -> ValidationException")
    @MethodSource("requiredFieldNullers")
    void givenRequiredFieldMissing_whenCreateIsInvoked_thenValidationExceptionIsThrown(
            String expectedFieldName, Consumer<WorkflowRequest> nullField) {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        nullField.accept(workflowRequest);

        // make sure each null field causes a validationError
        assertThatThrownBy(() -> iojAppealsOrchestrationService.create(workflowRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining(expectedFieldName);

        // make sure no downstream calls made it through
        verify(repOrderService, never()).getRepOrder(any());
        verify(iojAppealService, never()).create(any());
    }

    @Test
    void givenMultipleRequiredFieldsMissing_whenCreateIsInvoked_thenAllFieldsAreReportedInOneMessage() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setRepId(null);
        workflowRequest.getApplicationDTO().getAssessmentDTO().getIojAppeal().setCmuId(null);
        workflowRequest.getApplicationDTO().getAssessmentDTO().getIojAppeal().setNewWorkReasonDTO(null);

        assertThatThrownBy(() -> iojAppealsOrchestrationService.create(workflowRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("applicationDTO.repId")
                .hasMessageContaining("iojAppeal.cmuId")
                .hasMessageContaining("iojAppeal.newWorkReasonDTO");
    }
}
