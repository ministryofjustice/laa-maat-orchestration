package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith({MockitoExtension.class})
class IojAppealsOrchestrationServiceTest {
    private static final int EXISTING_APPEAL_ID = 1;

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

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(iojAppealMapper.getUserActionDTO(workflowRequest)).thenReturn(userActionDTO);
        when(proceedingsService.determineMagsRepDecision(workflowRequest))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(contributionService.calculate(workflowRequest)).thenReturn(workflowRequest.getApplicationDTO());
        when(maatCourtDataService.invokeStoredProcedure(any(), any(), any()))
                .thenReturn(workflowRequest.getApplicationDTO());

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

        verify(assessmentSummaryService, times(1)).getSummary(iojAppealDTO);
        verify(assessmentSummaryService).updateApplication(workflowRequest.getApplicationDTO(), assessmentSummaryDTO);
    }
}
