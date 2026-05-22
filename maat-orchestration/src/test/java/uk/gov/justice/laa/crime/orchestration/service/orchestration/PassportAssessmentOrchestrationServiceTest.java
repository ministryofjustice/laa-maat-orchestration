package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassportAssessmentOrchestrationServiceTest {

    @Mock
    private PassportAssessmentService passportAssessmentService;

    @Mock
    private RepOrderService repOrderService;

    @Mock
    private PassportAssessmentMapper passportAssessmentMapper;

    @Mock
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private ProceedingsService proceedingsService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private AssessmentSummaryService assessmentSummaryService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private ApplicationTrackingMapper applicationTrackingMapper;

    @Mock
    private ApplicationTrackingDataService applicationTrackingDataService;

    @InjectMocks
    private PassportAssessmentOrchestrationService passportAssessmentOrchestrationService;

    private void stubCommonCreateInteractions(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);

        when(passportAssessmentMapper.getUserActionDTO(workflowRequest))
                .thenReturn(TestModelDataBuilder.getUserActionDTO());

        when(passportAssessmentService.create(workflowRequest)).thenReturn(Constants.PASSPORT_ASSESSMENT_ID);

        when(repOrderService.updateRepOrderAssessmentDateCompleted(
                        eq(workflowRequest), eq(repOrderDTO), any(LocalDateTime.class)))
                .thenReturn(repOrderDTO);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class),
                        eq(workflowRequest.getUserDTO()),
                        eq(StoredProcedure.MANAGE_PASSPORT_EVIDENCE)))
                .thenReturn(applicationDTO);

        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class),
                        eq(workflowRequest.getUserDTO()),
                        eq(StoredProcedure.PRE_UPDATE_CC_APPLICATION)))
                .thenReturn(applicationDTO);

        when(assessmentSummaryService.getSummary(applicationDTO.getPassportedDTO()))
                .thenReturn(TestModelDataBuilder.getAssessmentSummaryDTOFromPassportedDTO());
    }

    private void verifyCommonCreateInteractions() {
        verify(workflowPreProcessorService)
                .preProcessRequest(any(WorkflowRequest.class), any(RepOrderDTO.class), any(UserActionDTO.class));

        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), eq(StoredProcedure.MANAGE_PASSPORT_EVIDENCE));

        verify(contributionService).calculate(any(WorkflowRequest.class));

        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), eq(StoredProcedure.PRE_UPDATE_CC_APPLICATION));

        verify(proceedingsService).determineMagsRepDecision(any(WorkflowRequest.class));

        verify(proceedingsService).updateApplication(any(WorkflowRequest.class), any(RepOrderDTO.class));

        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(any(WorkflowRequest.class), any(ApplicationDTO.class));
    }

    @Test
    void givenValidId_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        PassportedDTO dto = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);

        when(passportAssessmentService.find(Constants.PASSPORT_ASSESSMENT_ID)).thenReturn(dto);

        assertThat(passportAssessmentOrchestrationService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .isEqualTo(dto);
    }

    @Test
    void givenValidWorkflowRequest_whenCreateIsInvoked_thenApplicationDTOIsUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest
                .getApplicationDTO()
                .setPassportedDTO(PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        stubCommonCreateInteractions(workflowRequest, repOrderDTO);
        when(applicationTrackingMapper.build(
                        workflowRequest, repOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ))
                .thenReturn(TestModelDataBuilder.getApplicationTrackingOutputResult());

        ApplicationDTO applicationDTO = passportAssessmentOrchestrationService.create(workflowRequest);

        assertThat(applicationDTO).isEqualTo(workflowRequest.getApplicationDTO());
        verifyCommonCreateInteractions();
    }

    @Test
    void givenWorkReasonNotFMA_whenCreateIsInvoked_thenMatrixAndCorrespondenceStoredProcedureIsCalled() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        stubCommonCreateInteractions(workflowRequest, repOrderDTO);
        when(applicationTrackingMapper.build(
                        workflowRequest, repOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ))
                .thenReturn(TestModelDataBuilder.getApplicationTrackingOutputResult());
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class),
                        eq(workflowRequest.getUserDTO()),
                        eq(StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE)))
                .thenReturn(workflowRequest.getApplicationDTO());

        ApplicationDTO applicationDTO = passportAssessmentOrchestrationService.create(workflowRequest);

        assertThat(applicationDTO).isEqualTo(workflowRequest.getApplicationDTO());
        verifyCommonCreateInteractions();
        verify(maatCourtDataService)
                .invokeStoredProcedure(
                        any(ApplicationDTO.class),
                        any(UserDTO.class),
                        eq(StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));
    }

    @Test
    void givenNoApplicationTrackingLink_whenCreateIsInvoked_thenApplicationTrackingNotUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest
                .getApplicationDTO()
                .setPassportedDTO(PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        stubCommonCreateInteractions(workflowRequest, repOrderDTO);
        when(applicationTrackingMapper.build(
                        workflowRequest, repOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ))
                .thenReturn(new ApplicationTrackingOutputResult());

        ApplicationDTO applicationDTO = passportAssessmentOrchestrationService.create(workflowRequest);

        assertThat(applicationDTO).isEqualTo(workflowRequest.getApplicationDTO());
        verifyCommonCreateInteractions();
        verifyNoInteractions(applicationTrackingDataService);
    }
}
