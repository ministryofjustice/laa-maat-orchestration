package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.service.*;

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
        workflowRequest.setC3Enabled(false);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                any(StoredProcedure.class)
        ))
                .thenReturn(workflowRequest.getApplicationDTO());

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
        workflowRequest.setC3Enabled(false);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class),
                any(StoredProcedure.class)
        ))
                .thenReturn(workflowRequest.getApplicationDTO());

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

}
