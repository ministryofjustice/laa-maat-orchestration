package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class})
class MeansAssessmentOrchestrationServiceTest {

    @Mock
    private MeansAssessmentService meansAssessmentService;

    @Mock
    private ProceedingsService proceedingsService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private MeansAssessmentOrchestrationService orchestrationService;

    @Test
    void givenFinancialAssessmentId_whenFindIsInvoked_thenMeansAssessmentServiceIsCalled() {
        orchestrationService.find(Constants.TEST_FINANCIAL_ASSESSMENT_ID);
        verify(meansAssessmentService).find(Constants.TEST_FINANCIAL_ASSESSMENT_ID);
    }

    @Test
    void givenARequestWithC3Enabled_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithContribution() {

        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ContributionsDTO contributionsDTO = TestModelDataBuilder.getContributionsDTO();
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(applicationDTO);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        assertThat(actual.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest);
    }

    @Test
    void givenARequestWithC3NotEnabled_whenCreateIsInvoked_thenApplicationDTOIsNotUpdatedWithContribution() {

        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.setC3Enabled(false);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(workflowRequest.getApplicationDTO());

        orchestrationService.create(workflowRequest);
        verify(contributionService, times(0)).calculateContribution(workflowRequest);

        verify(proceedingsService, times(1)).updateApplication(workflowRequest);
    }

    @Test
    void givenARequestWithC3Enabled_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithContribution() {

        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ContributionsDTO contributionsDTO = TestModelDataBuilder.getContributionsDTO();
        ApplicationDTO applicationDTO = TestModelDataBuilder.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(applicationDTO);

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        assertThat(actual.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest);
    }

    @Test
    void givenARequestWithC3NotEnabled_whenUpdateIsInvoked_thenApplicationDTOIsNotUpdatedWithContribution() {

        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.setC3Enabled(false);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(workflowRequest.getApplicationDTO());

        orchestrationService.update(workflowRequest);
        verify(contributionService, times(0)).calculateContribution(workflowRequest);

        verify(proceedingsService, times(1)).updateApplication(workflowRequest);
    }

}
