package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.FeatureDecisionService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.MeansAssessmentService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_INCOMPLETE_ASSESSMENT;
import static uk.gov.justice.laa.crime.orchestration.common.Constants.WRN_MSG_REASSESSMENT;

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
    @InjectMocks
    private MeansAssessmentOrchestrationService orchestrationService;

    @Test
    void givenFinancialAssessmentId_whenFindIsInvoked_thenMeansAssessmentServiceIsCalled() {
        orchestrationService.find(Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
        verify(meansAssessmentService).find(Constants.FINANCIAL_ASSESSMENT_ID, Constants.APPLICANT_ID);
    }

    @Test
    void givenARequestWithC3Enabled_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithContribution() {

        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        ContributionsDTO contributionsDTO = MeansAssessmentDataBuilder.getContributionsDTO();
        ApplicationDTO applicationDTO = MeansAssessmentDataBuilder.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculate(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        ))
                .thenReturn(applicationDTO);
        when(featureDecisionService.isC3Enabled(workflowRequest)).thenReturn(true);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        assertThat(actual.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(meansAssessmentService).create(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(applicationDTO, workflowRequest.getUserDTO(),
                                                           StoredProcedure.PRE_UPDATE_CC_APPLICATION
        );

        verify(proceedingsService).updateApplication(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(applicationDTO, workflowRequest.getUserDTO(),
                                                           StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2
        );
        verify(assessmentSummaryService, times(1)).getSummary(any(FinancialAssessmentDTO.class));
    }

    @Test
    void givenARequestWithC3NotEnabled_whenCreateIsInvoked_thenCalculationContributionIsNotCalled() {

        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        )).thenReturn(workflowRequest.getApplicationDTO());

        when(featureDecisionService.isC3Enabled(workflowRequest)).thenReturn(false);

        orchestrationService.create(workflowRequest);
        verify(meansAssessmentService).create(workflowRequest);
        verify(contributionService, times(0)).calculate(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(workflowRequest.getApplicationDTO(),
                                                           workflowRequest.getUserDTO(),
                                                           StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1
        );

        verify(proceedingsService, times(1)).updateApplication(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(workflowRequest.getApplicationDTO(),
                                                           workflowRequest.getUserDTO(),
                                                           StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2
        );
        verify(assessmentSummaryService, times(1)).getSummary(any());
    }

    @Test
    void givenARequestWithC3Enabled_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithContribution() {

        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        ContributionsDTO contributionsDTO = MeansAssessmentDataBuilder.getContributionsDTO();
        ApplicationDTO applicationDTO = MeansAssessmentDataBuilder.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculate(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        ))
                .thenReturn(applicationDTO);
        when(featureDecisionService.isC3Enabled(workflowRequest)).thenReturn(true);

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        assertThat(actual.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(meansAssessmentService).update(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(workflowRequest.getApplicationDTO(),
                                                           workflowRequest.getUserDTO(),
                                                           StoredProcedure.PRE_UPDATE_CC_APPLICATION
        );

        verify(proceedingsService).updateApplication(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(workflowRequest.getApplicationDTO(),
                                                           workflowRequest.getUserDTO(),
                                                           StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2
        );
        verify(assessmentSummaryService, times(1)).getSummary(any());
    }

    @Test
    void givenARequestWithC3NotEnabled_whenUpdateIsInvoked_thenCalculationContributionIsNotCalled() {

        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        ))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(featureDecisionService.isC3Enabled(workflowRequest)).thenReturn(false);

        orchestrationService.update(workflowRequest);
        verify(meansAssessmentService).update(workflowRequest);
        verify(contributionService, times(0)).calculate(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(workflowRequest.getApplicationDTO(),
                                                           workflowRequest.getUserDTO(),
                                                           StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1
        );

        verify(proceedingsService, times(1)).updateApplication(workflowRequest);
        verify(maatCourtDataService).invokeStoredProcedure(workflowRequest.getApplicationDTO(),
                                                           workflowRequest.getUserDTO(),
                                                           StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_2
        );
        verify(assessmentSummaryService, times(1)).getSummary(any());
    }

    @Test
    void givenCrimeValidationExceptionThrownInCMAService_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        doThrow(new CrimeValidationException(List.of())).when(meansAssessmentService).create(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(CrimeValidationException.class);
        Mockito.verify(meansAssessmentService, times(0)).rollback(any());
    }

    @Test
    void givenValidationExceptionThrownInCMAService_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        doThrow(new ValidationException()).when(meansAssessmentService).create(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(ValidationException.class);
        Mockito.verify(meansAssessmentService, times(0)).rollback(any());
    }

    @Test
    void givenCrimeValidationExceptionThrownInCMAService_whenUpdateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        doThrow(new CrimeValidationException(List.of())).when(meansAssessmentService).update(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(CrimeValidationException.class);
        Mockito.verify(meansAssessmentService, times(0)).rollback(any());
    }

    @Test
    void givenValidationExceptionThrownInCMAService_whenUpdateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        doThrow(new ValidationException()).when(meansAssessmentService).update(workflowRequest);
        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(ValidationException.class);
        Mockito.verify(meansAssessmentService, times(0)).rollback(any());
    }

    @Test
    void givenValidationFailureAtPostAssessmentProcessing_whenUpdateIsInvoked_thenRollbackIsInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationSpy = spy(workflowRequest.getApplicationDTO());
        workflowRequest.setApplicationDTO(applicationSpy);

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        )).thenAnswer(invocation -> {
            StoredProcedure procedure = invocation.getArgument(2);
            if (procedure == StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1 ||
                    procedure == StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3) {
                workflowRequest.getApplicationDTO().setAlertMessage(WRN_MSG_REASSESSMENT);
            }
            return workflowRequest.getApplicationDTO();
        });

        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(ValidationException.class).hasMessage(WRN_MSG_REASSESSMENT);

        verify(applicationSpy, times(1)).setAlertMessage("");
        verify(proceedingsService, times(0)).updateApplication(workflowRequest);
        verify(meansAssessmentService, times(1)).rollback(any());
    }

    @Test
    void givenValidationFailureAtPostAssessmentProcessing_whenCreateIsInvoked_thenRollbackIsInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationSpy = spy(workflowRequest.getApplicationDTO());
        workflowRequest.setApplicationDTO(applicationSpy);

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        )).thenAnswer(invocation -> {
            StoredProcedure procedure = invocation.getArgument(2);
            if (procedure == StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1 ||
                    procedure == StoredProcedure.ASSESSMENT_POST_PROCESSING_PART_1_C3) {
                workflowRequest.getApplicationDTO().setAlertMessage(WRN_MSG_INCOMPLETE_ASSESSMENT);
            }
            return workflowRequest.getApplicationDTO();
        });

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(ValidationException.class).hasMessage(WRN_MSG_INCOMPLETE_ASSESSMENT);
        verify(applicationSpy, times(1)).setAlertMessage("");
        verify(proceedingsService, times(0)).updateApplication(workflowRequest);
        verify(meansAssessmentService, times(1)).rollback(any());
    }

    @Test
    void givenSuccessfulPostAssessmentProcessing_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = MeansAssessmentDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setAlertMessage(MOCK_ALERT);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                                                        any(StoredProcedure.class)
        )).thenReturn(workflowRequest.getApplicationDTO());

        orchestrationService.create(workflowRequest);
        verify(proceedingsService, times(1)).updateApplication(workflowRequest);
        verify(meansAssessmentService, times(0)).rollback(any());
    }
}
