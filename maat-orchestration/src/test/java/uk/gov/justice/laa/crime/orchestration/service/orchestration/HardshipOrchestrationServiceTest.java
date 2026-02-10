package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.buildWorkflowRequestWithHardship;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getApiPerformHardshipResponse;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getApplicationDTOWithHardship;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getAssessmentStatusDTO;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getAssessmentSummaryDTO;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getContributionsDTO;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getHardshipOverviewDTO;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.getHardshipReviewDTO;

import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.CCLFUpdateService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.HardshipService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;
import uk.gov.justice.laa.crime.orchestration.utils.WebClientTestUtils;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ExtendWith({MockitoExtension.class})
class HardshipOrchestrationServiceTest {

    @Mock
    private HardshipService hardshipService;

    @Mock
    private ProceedingsService proceedingsService;

    @Mock
    private ContributionService contributionService;

    @Mock
    private AssessmentSummaryService assessmentSummaryService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @Mock
    private HardshipMapper hardshipMapper;

    @Mock
    private ApplicationTrackingDataService applicationTrackingDataService;

    @Mock
    private ApplicationTrackingMapper applicationTrackingMapper;

    @Mock
    private RepOrderService repOrderService;

    @Mock
    private ApplicationService applicationService;

    @Mock
    private WorkflowPreProcessorService workflowPreProcessorService;

    @Mock
    private CCLFUpdateService cclfUpdateService;

    @InjectMocks
    private HardshipOrchestrationService orchestrationService;

    private ApiPerformHardshipResponse performHardshipResponse;
    private AssessmentSummaryDTO assessmentSummaryDTO;

    @BeforeEach
    void setUp() {
        performHardshipResponse = getApiPerformHardshipResponse();
        assessmentSummaryDTO = getAssessmentSummaryDTO();
    }

    @Test
    void givenHardshipReviewId_whenFindIsInvoked_thenHardshipServiceIsCalled() {
        orchestrationService.find(Constants.HARDSHIP_REVIEW_ID);
        verify(hardshipService).find(Constants.HARDSHIP_REVIEW_ID);
    }

    private WorkflowRequest setupCreateStubs(CourtType courtType, CurrentStatus status) {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(courtType);

        when(hardshipService.create(workflowRequest)).thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO = HardshipReviewDTO.builder().build();
        if (CourtType.CROWN_COURT == courtType) {
            hardshipReviewDTO = getHardshipOverviewDTO(courtType).getCrownCourtHardship();
        } else if (CourtType.MAGISTRATE == courtType) {
            hardshipReviewDTO = getHardshipOverviewDTO(courtType).getMagCourtHardship();
        }
        if (status != null) {
            hardshipReviewDTO.setAsessmentStatus(getAssessmentStatusDTO(status));
        } else {
            hardshipReviewDTO.setAsessmentStatus(null);
        }
        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(hardshipReviewDTO);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(courtType)))
                .thenReturn(getAssessmentSummaryDTO());

        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        return workflowRequest;
    }

    @Test
    void givenIsMagsCourtAndNoVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, CurrentStatus.COMPLETE);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(contributionService.isVariationRequired(any(ApplicationDTO.class))).thenReturn(false);

        ApplicationDTO applicationDTO = orchestrationService.create(workflowRequest);

        assertThat(applicationDTO
                        .getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenIsMagsCourtWithVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, CurrentStatus.COMPLETE);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = getApplicationDTOWithHardship(CourtType.MAGISTRATE);
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);
        when(contributionService.isVariationRequired(any(ApplicationDTO.class))).thenReturn(true);

        ApplicationDTO expected = orchestrationService.create(workflowRequest);

        assertThat(expected.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution()).isEqualTo(contributionsDTO);

        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenIsCrownCourt_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT, CurrentStatus.COMPLETE);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = getApplicationDTOWithHardship(CourtType.CROWN_COURT);
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        applicationDTO
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(applicationDTO);
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);

        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(12345));
        when(repOrderService.getRepOrder(any())).thenReturn(repOrderDTO);

        ApplicationDTO expected = orchestrationService.create(workflowRequest);

        assertThat(expected.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getCrownCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution()).isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest, repOrderDTO);

        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourtInProgressHardship_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT, CurrentStatus.IN_PROGRESS);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.setAsessmentStatus(getAssessmentStatusDTO(CurrentStatus.IN_PROGRESS));
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getCrownCourtHardship())
                .isEqualTo(expected);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourtWithMissingAssessment_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT, null);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getCrownCourtHardship())
                .isEqualTo(expected);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourtInProgressHardship_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, CurrentStatus.IN_PROGRESS);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.setAsessmentStatus(getAssessmentStatusDTO(CurrentStatus.IN_PROGRESS));
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(expected);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourtWithMissingAssessment_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, null);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(expected);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourt_whenCreateIsInvokedAndExceptionThrownInCreateHardship_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        when(hardshipService.create(workflowRequest)).thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        hardshipReviewDTO.setAsessmentStatus(null);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(hardshipReviewDTO);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(CourtType.MAGISTRATE)))
                .thenReturn(getAssessmentSummaryDTO());

        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        doThrow(webClientResponseException)
                .when(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourt_whenCreateIsInvokedAndExceptionThrownInFind_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        when(hardshipService.create(workflowRequest)).thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        hardshipReviewDTO.setAsessmentStatus(null);

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenThrow(webClientResponseException);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourt_whenCreateIsInvokedAndExceptionThrownInGetSummary_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        when(hardshipService.create(workflowRequest)).thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        hardshipReviewDTO.setAsessmentStatus(null);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(hardshipReviewDTO);

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(CourtType.MAGISTRATE)))
                .thenThrow(webClientResponseException);

        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);
        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagsCourtAndNoVariation_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(contributionService.isVariationRequired(any(ApplicationDTO.class))).thenReturn(false);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), any()))
                .thenReturn(assessmentSummaryDTO);

        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        ApplicationDTO applicationDTO = orchestrationService.update(workflowRequest);

        assertThat(applicationDTO
                        .getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagsCourtWithVariation_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);
        when(contributionService.isVariationRequired(any(ApplicationDTO.class))).thenReturn(true);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), any()))
                .thenReturn(assessmentSummaryDTO);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        ApplicationDTO expected = orchestrationService.update(workflowRequest);

        assertThat(expected.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution()).isEqualTo(contributionsDTO);

        verify(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourt_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        applicationDTO
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(applicationDTO);

        when(contributionService.calculate(workflowRequest)).thenReturn(applicationDTO);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenReturn(applicationDTO);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), any()))
                .thenReturn(assessmentSummaryDTO);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());
        when(applicationTrackingMapper.build(any(), any(), any(), any()))
                .thenReturn(new ApplicationTrackingOutputResult().withUsn(12345));
        when(repOrderService.getRepOrder(any())).thenReturn(repOrderDTO);

        ApplicationDTO expected = orchestrationService.update(workflowRequest);

        assertThat(expected.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getCrownCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution()).isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest, repOrderDTO);

        verify(assessmentSummaryService).updateApplication(applicationDTO, assessmentSummaryDTO);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourtInProgressHardship_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .getAsessmentStatus()
                .setStatus(AssessmentStatusDTO.INCOMPLETE);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.getAsessmentStatus().setStatus(AssessmentStatusDTO.INCOMPLETE);
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getCrownCourtHardship())
                .isEqualTo(expected);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourtWithMissingAssessment_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setAsessmentStatus(null);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getCrownCourtHardship())
                .isEqualTo(expected);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourtInProgressHardship_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getMagCourtHardship()
                .getAsessmentStatus()
                .setStatus(AssessmentStatusDTO.INCOMPLETE);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.getAsessmentStatus().setStatus(AssessmentStatusDTO.INCOMPLETE);
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(expected);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenMagCourtWithMissingAssessment_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getMagCourtHardship()
                .setAsessmentStatus(null);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected =
                getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship()
                        .getMagCourtHardship())
                .isEqualTo(expected);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourt_whenUpdateIsInvokedAndExceptionThrownInSP_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);

        applicationDTO
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(maatCourtDataService.invokeStoredProcedure(
                        any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)))
                .thenThrow(webClientResponseException);

        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenCrownCourt_whenUpdateIsInvokedAndExceptionThrownInCalculateContribution_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);

        applicationDTO
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());

        WebClientResponseException webClientResponseException =
                WebClientTestUtils.getWebClientResponseException(HttpStatus.INTERNAL_SERVER_ERROR);

        when(contributionService.calculate(workflowRequest)).thenThrow(webClientResponseException);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(MaatOrchestrationException.class);

        verify(applicationService).updateDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenExceptionThrownInCreateHardshipService_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        when(hardshipService.create(any(WorkflowRequest.class))).thenThrow(WebClientResponseException.class);
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(WebClientResponseException.class);

        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenExceptionThrownInUpdateHardshipService_whenUpdateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        doThrow(WebClientResponseException.class).when(hardshipService).update(any(WorkflowRequest.class));
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(WebClientResponseException.class);

        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenAnExceptionThrownInWorkflowPreProcessorService_whenCreateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        when(workflowPreProcessorService.preProcessRequest(any(), any(), any()))
                .thenThrow(new CrimeValidationException(List.of()));
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(CrimeValidationException.class);

        verify(workflowPreProcessorService).preProcessRequest(any(), any(), any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }

    @Test
    void givenAnExceptionThrownInWorkflowPreProcessorService_whenUpdateIsInvoked_thenRollbackIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        when(workflowPreProcessorService.preProcessRequest(any(), any(), any()))
                .thenThrow(new CrimeValidationException(List.of()));
        when(hardshipMapper.getUserActionDTO(any(), any()))
                .thenReturn(UserActionDTO.builder().username("mock-u").build());

        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(CrimeValidationException.class);

        verify(workflowPreProcessorService).preProcessRequest(any(), any(), any());
        verify(repOrderService, times(0)).updateRepOrderDateModified(eq(workflowRequest), any());
    }
}
