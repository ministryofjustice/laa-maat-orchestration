package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
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
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
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
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
        LocalDateTime dateCompleted = Constants.ASSESSMENT_COMPLETED_DATETIME;
        RepOrderDTO updatedRepOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        updatedRepOrderDTO.setAssessmentDateCompleted(DateUtil.parseLocalDate(dateCompleted));
        ApplicationDTO applicationDTOManagedEvidence = workflowRequest.getApplicationDTO();
        applicationDTOManagedEvidence.getPassportedDTO().setTimestamp(dateCompleted.atZone(ZoneId.systemDefault()));
        ApplicationDTO applicationDTOWithContributions = workflowRequest.getApplicationDTO();
        applicationDTOWithContributions
                .getCrownCourtOverviewDTO()
                .setContribution(TestModelDataBuilder.getContributionsDTO());

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(passportAssessmentMapper.getUserActionDTO(workflowRequest))
                .thenReturn(TestModelDataBuilder.getUserActionDTO());
        when(passportAssessmentService.create(workflowRequest)).thenReturn(Constants.PASSPORT_ASSESSMENT_ID);
        when(repOrderService.updateRepOrderAssessmentDateCompleted(
                        eq(workflowRequest), eq(repOrderDTO), any(LocalDateTime.class)))
                .thenReturn(updatedRepOrderDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.MANAGE_PASSPORT_EVIDENCE))
                .thenReturn(applicationDTOManagedEvidence);
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTOWithContributions);
        when(assessmentSummaryService.getSummary(
                        workflowRequest.getApplicationDTO().getPassportedDTO()))
                .thenReturn(TestModelDataBuilder.getAssessmentSummaryDTOFromPassportedDTO());
        when(applicationTrackingMapper.build(
                        workflowRequest, updatedRepOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ))
                .thenReturn(TestModelDataBuilder.getApplicationTrackingOutputResult());

        ApplicationDTO returnedApplicationDTO = passportAssessmentOrchestrationService.create(workflowRequest);

        verify(workflowPreProcessorService)
                .validatePassportRequest(any(WorkflowRequest.class), any(RepOrderDTO.class), any(UserActionDTO.class));
        verify(proceedingsService).determineMagsRepDecision(any(WorkflowRequest.class));
        verify(proceedingsService).updateApplication(any(WorkflowRequest.class), any(RepOrderDTO.class));
        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(any(WorkflowRequest.class), any(ApplicationDTO.class));
        verify(applicationTrackingDataService).sendTrackingOutputResult(any(ApplicationTrackingOutputResult.class));

        assertThat(returnedApplicationDTO.getPassportedDTO().getTimestamp())
                .isEqualTo(dateCompleted.atZone(ZoneId.systemDefault()));
        assertThat(returnedApplicationDTO
                        .getCrownCourtOverviewDTO()
                        .getContribution()
                        .getId())
                .isEqualTo(Long.valueOf(Constants.CONTRIBUTIONS_ID));
    }

    @Test
    void givenNoRepOrderRetrieved_whenCreateIsInvoked_thenExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(null);

        assertThatThrownBy(() -> passportAssessmentOrchestrationService.create(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);
    }

    @Test
    void givenWorkReasonNotFMA_whenCreateIsInvoked_thenMatrixAndCorrespondenceStoredProcedureIsCalled() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        LocalDateTime dateCompleted = Constants.ASSESSMENT_COMPLETED_DATETIME;
        RepOrderDTO updatedRepOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        updatedRepOrderDTO.setAssessmentDateCompleted(DateUtil.parseLocalDate(dateCompleted));
        ApplicationDTO applicationDTOManagedEvidence = workflowRequest.getApplicationDTO();
        applicationDTOManagedEvidence.getPassportedDTO().setTimestamp(dateCompleted.atZone(ZoneId.systemDefault()));
        ApplicationDTO applicationDTOWithContributions = workflowRequest.getApplicationDTO();
        applicationDTOWithContributions
                .getCrownCourtOverviewDTO()
                .setContribution(TestModelDataBuilder.getContributionsDTO());

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(passportAssessmentMapper.getUserActionDTO(workflowRequest))
                .thenReturn(TestModelDataBuilder.getUserActionDTO());
        when(passportAssessmentService.create(workflowRequest)).thenReturn(Constants.PASSPORT_ASSESSMENT_ID);
        when(repOrderService.updateRepOrderAssessmentDateCompleted(
                        eq(workflowRequest), eq(repOrderDTO), any(LocalDateTime.class)))
                .thenReturn(updatedRepOrderDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.MANAGE_PASSPORT_EVIDENCE))
                .thenReturn(applicationDTOManagedEvidence);
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTOWithContributions);
        when(maatCourtDataService.invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE))
                .thenReturn(applicationDTOWithContributions);
        when(assessmentSummaryService.getSummary(
                        workflowRequest.getApplicationDTO().getPassportedDTO()))
                .thenReturn(TestModelDataBuilder.getAssessmentSummaryDTOFromPassportedDTO());
        when(applicationTrackingMapper.build(
                        workflowRequest, updatedRepOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ))
                .thenReturn(TestModelDataBuilder.getApplicationTrackingOutputResult());

        ApplicationDTO returnedApplicationDTO = passportAssessmentOrchestrationService.create(workflowRequest);

        verify(workflowPreProcessorService)
                .validatePassportRequest(any(WorkflowRequest.class), any(RepOrderDTO.class), any(UserActionDTO.class));
        verify(proceedingsService).determineMagsRepDecision(any(WorkflowRequest.class));
        verify(proceedingsService).updateApplication(any(WorkflowRequest.class), any(RepOrderDTO.class));
        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(any(WorkflowRequest.class), any(ApplicationDTO.class));
        verify(applicationTrackingDataService).sendTrackingOutputResult(any(ApplicationTrackingOutputResult.class));

        assertThat(returnedApplicationDTO.getPassportedDTO().getTimestamp())
                .isEqualTo(dateCompleted.atZone(ZoneId.systemDefault()));
        assertThat(returnedApplicationDTO
                        .getCrownCourtOverviewDTO()
                        .getContribution()
                        .getId())
                .isEqualTo(Long.valueOf(Constants.CONTRIBUTIONS_ID));
    }

    @Test
    void givenNoApplicationTrackingLink_whenCreateIsInvoked_thenApplicationTrackingNotUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest
                .getApplicationDTO()
                .setPassportedDTO(PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        LocalDateTime dateCompleted = Constants.ASSESSMENT_COMPLETED_DATETIME;
        RepOrderDTO updatedRepOrderDTO = TestModelDataBuilder.buildRepOrderDTO(RepOrderStatus.CURR.getCode());
        updatedRepOrderDTO.setAssessmentDateCompleted(DateUtil.parseLocalDate(dateCompleted));
        ApplicationDTO applicationDTOManagedEvidence = workflowRequest.getApplicationDTO();
        applicationDTOManagedEvidence.getPassportedDTO().setTimestamp(dateCompleted.atZone(ZoneId.systemDefault()));
        ApplicationDTO applicationDTOWithContributions = workflowRequest.getApplicationDTO();
        applicationDTOWithContributions
                .getCrownCourtOverviewDTO()
                .setContribution(TestModelDataBuilder.getContributionsDTO());

        when(repOrderService.getRepOrder(workflowRequest)).thenReturn(repOrderDTO);
        when(passportAssessmentMapper.getUserActionDTO(workflowRequest))
                .thenReturn(TestModelDataBuilder.getUserActionDTO());
        when(passportAssessmentService.create(workflowRequest)).thenReturn(Constants.PASSPORT_ASSESSMENT_ID);
        when(repOrderService.updateRepOrderAssessmentDateCompleted(
                        eq(workflowRequest), eq(repOrderDTO), any(LocalDateTime.class)))
                .thenReturn(updatedRepOrderDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        workflowRequest.getApplicationDTO(),
                        workflowRequest.getUserDTO(),
                        StoredProcedure.MANAGE_PASSPORT_EVIDENCE))
                .thenReturn(applicationDTOManagedEvidence);
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTOWithContributions);
        when(assessmentSummaryService.getSummary(
                        workflowRequest.getApplicationDTO().getPassportedDTO()))
                .thenReturn(TestModelDataBuilder.getAssessmentSummaryDTOFromPassportedDTO());
        when(applicationTrackingMapper.build(
                        workflowRequest, updatedRepOrderDTO, AssessmentType.PASSPORT, RequestSource.PASSPORT_IOJ))
                .thenReturn(new ApplicationTrackingOutputResult());

        ApplicationDTO returnedApplicationDTO = passportAssessmentOrchestrationService.create(workflowRequest);

        verify(workflowPreProcessorService)
                .validatePassportRequest(any(WorkflowRequest.class), any(RepOrderDTO.class), any(UserActionDTO.class));
        verify(proceedingsService).determineMagsRepDecision(any(WorkflowRequest.class));
        verify(proceedingsService).updateApplication(any(WorkflowRequest.class), any(RepOrderDTO.class));
        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(any(WorkflowRequest.class), any(ApplicationDTO.class));
        verifyNoInteractions(applicationTrackingDataService);

        assertThat(returnedApplicationDTO.getPassportedDTO().getTimestamp())
                .isEqualTo(dateCompleted.atZone(ZoneId.systemDefault()));
        assertThat(returnedApplicationDTO
                        .getCrownCourtOverviewDTO()
                        .getContribution()
                        .getId())
                .isEqualTo(Long.valueOf(Constants.CONTRIBUTIONS_ID));
    }
}
