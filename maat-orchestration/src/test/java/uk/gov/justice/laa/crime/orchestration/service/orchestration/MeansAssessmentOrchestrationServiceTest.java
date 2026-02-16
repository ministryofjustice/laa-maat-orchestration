package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_INCOMPLETE_ASSESSMENT;
import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_REASSESSMENT;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.MeansAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.CCLFUpdateService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.FeatureDecisionService;
import uk.gov.justice.laa.crime.orchestration.service.IncomeEvidenceService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class MeansAssessmentOrchestrationServiceTest {

    private static final String MOCK_ALERT = "Mock alert";

    @Mock
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private FeatureDecisionService featureDecisionService;

    @Mock
    private ProceedingsService proceedingsService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private AssessmentSummaryService assessmentSummaryService;

    @Mock
    private ApplicationTrackingMapper applicationTrackingMapper;

    @Mock
    private MeansAssessmentMapper meansAssessmentMapper;

    @Mock
    private RepOrderService repOrderService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Mock
    private CCLFUpdateService cclfUpdateService;

    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;

    @Mock
    private IncomeEvidenceService incomeEvidenceService;

    @Mock
    private ApplicationTrackingDataService applicationTrackingDataService;

    @InjectMocks
    private MeansAssessmentOrchestrationService orchestrationService;

    private WorkflowRequest workflowRequest;
    private ApplicationDTO applicationDTO;
    private ContributionsDTO contributionsDTO;
    private RepOrderDTO repOrderDTO;

    @BeforeEach
    void setUp() {
        workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        applicationDTO = MeansAssessmentDataBuilder.getApplicationDTO();
        contributionsDTO = MeansAssessmentDataBuilder.getContributionsDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(applicationDTO);
    }

    @Test
    void givenFinancialAssessmentId_whenFindIsInvoked_thenMeansAssessmentServiceIsCalled() {
        orchestrationService.find(Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        verify(meansAssessmentService).find(Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
    }

    @Test
    void givenValidRequest_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithContribution() {
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);
        when(maatCourtDataApiService.getRepOrderByRepId(anyInt())).thenReturn(repOrderDTO);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        assertThat(actual.getCrownCourtOverviewDTO().getContribution()).isEqualTo(contributionsDTO);

        verify(meansAssessmentService).create(workflowRequest);
        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        applicationDTO, workflowRequest.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION);

        verify(proceedingsService).updateApplication(workflowRequest, repOrderDTO);
        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        applicationDTO,
                        workflowRequest.getUserDTO(),
                        StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE);
        verify(assessmentSummaryService, times(1)).getSummary(any(FinancialAssessmentDTO.class));
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenValidRequest_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithContribution() {
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);
        when(maatCourtDataApiService.getRepOrderByRepId(anyInt())).thenReturn(repOrderDTO);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        assertThat(actual.getCrownCourtOverviewDTO().getContribution()).isEqualTo(contributionsDTO);

        verify(meansAssessmentService).update(workflowRequest);
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
        verify(assessmentSummaryService, times(1)).getSummary(any(FinancialAssessmentDTO.class));
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenPostProcessingNotEnabled_whenCreatedIsInvoked_thenPostProcessingWorkflowIsNotInvoked() {
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);
        when(featureDecisionService.isMaatPostAssessmentProcessingEnabled(workflowRequest))
                .thenReturn(false);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        orchestrationService.create(workflowRequest);

        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        any(ApplicationDTO.class),
                        any(UserDTO.class),
                        eq(StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3));

        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        applicationDTO, workflowRequest.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION);

        verify(workflowPreProcessorService, never()).preProcessRequest(any(), any(), any());
        verify(incomeEvidenceService, never()).createEvidence(any(), any());
        verify(proceedingsService, never()).determineMagsRepDecision(any());
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenPostProcessingEnabled_whenCreatedIsInvoked_thenPostProcessingWorkflowIsInvoked() {
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(featureDecisionService.isMaatPostAssessmentProcessingEnabled(workflowRequest))
                .thenReturn(true);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        orchestrationService.create(workflowRequest);

        verify(maatCourtDataService, never())
                .invokeStoredProcedure(
                        any(ApplicationDTO.class),
                        any(UserDTO.class),
                        eq(StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3));

        verify(maatCourtDataService, never())
                .invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.PRE_UPDATE_CC_APPLICATION);
        verify(workflowPreProcessorService).preProcessRequest(any(), any(), any());

        verify(incomeEvidenceService, times(1)).createEvidence(any(), any());
        verify(proceedingsService, times(1)).determineMagsRepDecision(any());
        verify(contributionService, times(1)).calculate(any());
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenPostProcessingNotEnabled_whenUpdateIsInvoked_thenPostProcessingWorkflowIsNotInvoked() {
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);
        when(featureDecisionService.isMaatPostAssessmentProcessingEnabled(workflowRequest))
                .thenReturn(false);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        orchestrationService.update(workflowRequest);

        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        applicationDTO, workflowRequest.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION);

        verify(workflowPreProcessorService, never()).preProcessRequest(any(), any(), any());
        verify(incomeEvidenceService, never()).createEvidence(any(), any());
        verify(proceedingsService, never()).determineMagsRepDecision(any());
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenPostProcessingEnabled_whenUpdateIsInvoked_thenPostProcessingWorkflowIsInvoked() {
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(featureDecisionService.isMaatPostAssessmentProcessingEnabled(workflowRequest))
                .thenReturn(true);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        orchestrationService.update(workflowRequest);
        verify(maatCourtDataService, never())
                .invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.PRE_UPDATE_CC_APPLICATION);
        verify(workflowPreProcessorService).preProcessRequest(any(), any(), any());

        verify(incomeEvidenceService, never()).createEvidence(any(), any());
        verify(proceedingsService, times(1)).determineMagsRepDecision(any());
        verify(contributionService, times(1)).calculate(any());
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrimeValidationExceptionThrownInCMAService_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        doThrow(new CrimeValidationException(List.of()))
                .when(meansAssessmentService)
                .create(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(CrimeValidationException.class);
        verify(meansAssessmentService, times(0)).rollback(any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenValidationExceptionThrownInCMAService_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        doThrow(new ValidationException()).when(meansAssessmentService).create(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.create(workflowRequest)).isInstanceOf(ValidationException.class);
        verify(meansAssessmentService, times(0)).rollback(any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrimeValidationExceptionThrownInCMAService_whenUpdateIsInvoked_thenRollbackIsNotInvoked() {
        doThrow(new CrimeValidationException(List.of()))
                .when(meansAssessmentService)
                .update(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(CrimeValidationException.class);
        verify(meansAssessmentService, times(0)).rollback(any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenValidationExceptionThrownInCMAService_whenUpdateIsInvoked_thenRollbackIsNotInvoked() {
        doThrow(new ValidationException()).when(meansAssessmentService).update(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.update(workflowRequest)).isInstanceOf(ValidationException.class);
        verify(meansAssessmentService, times(0)).rollback(any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenValidationFailureAtPostAssessmentProcessing_whenUpdateIsInvoked_thenRollbackIsInvoked() {
        ApplicationDTO applicationSpy = spy(workflowRequest.getApplicationDTO());
        workflowRequest.setApplicationDTO(applicationSpy);

        when(contributionService.calculate(workflowRequest)).thenReturn(workflowRequest.getApplicationDTO());
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenAnswer(invocation -> {
                    StoredProcedure procedure = invocation.getArgument(2);
                    if (procedure == StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3) {
                        workflowRequest.getApplicationDTO().setAlertMessage(WRN_MSG_REASSESSMENT);
                    }
                    return workflowRequest.getApplicationDTO();
                });

        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessage(WRN_MSG_REASSESSMENT);

        verify(applicationSpy, times(1)).setAlertMessage("");
        verify(proceedingsService, times(0))
                .updateApplication(
                        workflowRequest, TestModelDataBuilder.getTestRepOrderDTO(workflowRequest.getApplicationDTO()));
        verify(meansAssessmentService, times(1)).rollback(any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenValidationFailureAtPostAssessmentProcessing_whenCreateIsInvoked_thenRollbackIsInvoked() {
        ApplicationDTO applicationSpy = spy(workflowRequest.getApplicationDTO());
        workflowRequest.setApplicationDTO(applicationSpy);

        when(contributionService.calculate(workflowRequest)).thenReturn(workflowRequest.getApplicationDTO());
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenAnswer(invocation -> {
                    StoredProcedure procedure = invocation.getArgument(2);
                    if (procedure == StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3) {
                        workflowRequest.getApplicationDTO().setAlertMessage(WRN_MSG_INCOMPLETE_ASSESSMENT);
                    }
                    return workflowRequest.getApplicationDTO();
                });

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(ValidationException.class)
                .hasMessage(WRN_MSG_INCOMPLETE_ASSESSMENT);
        verify(applicationSpy, times(1)).setAlertMessage("");
        verify(proceedingsService, times(0))
                .updateApplication(
                        workflowRequest, TestModelDataBuilder.getTestRepOrderDTO(workflowRequest.getApplicationDTO()));
        verify(meansAssessmentService, times(1)).rollback(any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenSuccessfulPostAssessmentProcessing_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        workflowRequest.getApplicationDTO().setAlertMessage(MOCK_ALERT);

        when(contributionService.calculate(workflowRequest)).thenReturn(workflowRequest.getApplicationDTO());
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(maatCourtDataApiService.getRepOrderByRepId(anyInt())).thenReturn(repOrderDTO);
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(123));

        orchestrationService.create(workflowRequest);

        verify(proceedingsService, times(1)).updateApplication(workflowRequest, repOrderDTO);
        verify(meansAssessmentService, times(0)).rollback(any());
        verify(applicationTrackingDataService, times(1)).sendTrackingOutputResult(any());
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }
}
