package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.*;


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

    @InjectMocks
    private HardshipOrchestrationService orchestrationService;

    @Test
    void givenHardshipReviewId_whenFindIsInvoked_thenHardshipServiceIsCalled() {
        orchestrationService.find(Constants.HARDSHIP_REVIEW_ID);
        verify(hardshipService).find(Constants.HARDSHIP_REVIEW_ID);
    }

    private WorkflowRequest setupCreateStubs(CourtType courtType, CurrentStatus status) {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(courtType);

        ApiPerformHardshipResponse performHardshipResponse = getApiPerformHardshipResponse();
        when(hardshipService.createHardship(workflowRequest))
                .thenReturn(performHardshipResponse);

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

        return workflowRequest;
    }

    @Test
    void givenIsMagsCourtAndNoVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {

        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, CurrentStatus.COMPLETE);

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(false);

        ApplicationDTO applicationDTO = orchestrationService.create(workflowRequest);

        assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        verify(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
    }

    @Test
    void givenIsMagsCourtWithVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, CurrentStatus.COMPLETE);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = getApplicationDTOWithHardship(CourtType.MAGISTRATE);
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(applicationDTO);
        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(true);

        ApplicationDTO expected = orchestrationService.create(workflowRequest);

        assertThat(expected.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
    }

    @Test
    void givenIsCrownCourt_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT, CurrentStatus.COMPLETE);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = getApplicationDTOWithHardship(CourtType.CROWN_COURT);
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(applicationDTO);

        ApplicationDTO expected = orchestrationService.create(workflowRequest);

        assertThat(expected.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest);

        verify(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
    }

    @Test
    void givenCrownCourtInProgressHardship_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT, CurrentStatus.IN_PROGRESS);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.setAsessmentStatus(getAssessmentStatusDTO(CurrentStatus.IN_PROGRESS));
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(expected);
    }

    @Test
    void givenCrownCourtWithMissingAssessment_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT, null);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(expected);
    }

    @Test
    void givenMagCourtInProgressHardship_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, CurrentStatus.IN_PROGRESS);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.setAsessmentStatus(getAssessmentStatusDTO(CurrentStatus.IN_PROGRESS));
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(expected);
    }

    @Test
    void givenMagCourtWithMissingAssessment_whenCreateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE, null);

        ApplicationDTO actual = orchestrationService.create(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(expected);
    }

    @Test
    void givenMagCourt_whenCreateIsInvokedAndExceptionThrownInCreateHardship_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        ApiPerformHardshipResponse performHardshipResponse = getApiPerformHardshipResponse();
        when(hardshipService.createHardship(workflowRequest))
                .thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        hardshipReviewDTO.setAsessmentStatus(null);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(hardshipReviewDTO);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(CourtType.MAGISTRATE)))
                .thenReturn(getAssessmentSummaryDTO());

        doThrow(new APIClientException()).when(assessmentSummaryService).updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(APIClientException.class);
        Mockito.verify(hardshipService, times(1)).rollbackHardship(any());
    }

    @Test
    void givenMagCourt_whenCreateIsInvokedAndExceptionThrownInFind_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        ApiPerformHardshipResponse performHardshipResponse = getApiPerformHardshipResponse();
        when(hardshipService.createHardship(workflowRequest))
                .thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        hardshipReviewDTO.setAsessmentStatus(null);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenThrow(new APIClientException());

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(APIClientException.class);
        Mockito.verify(hardshipService, times(1)).rollbackHardship(any());
    }

    @Test
    void givenMagCourt_whenCreateIsInvokedAndExceptionThrownInGetSummary_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        ApiPerformHardshipResponse performHardshipResponse = getApiPerformHardshipResponse();
        when(hardshipService.createHardship(workflowRequest))
                .thenReturn(performHardshipResponse);

        HardshipReviewDTO hardshipReviewDTO = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        hardshipReviewDTO.setAsessmentStatus(null);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(hardshipReviewDTO);

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(CourtType.MAGISTRATE)))
                .thenThrow(new APIClientException());

        assertThatThrownBy(() -> orchestrationService.create(workflowRequest))
                .isInstanceOf(APIClientException.class);
        Mockito.verify(hardshipService, times(1)).rollbackHardship(any());
    }

    @Test
    void givenMagsCourtAndNoVariation_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {

        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(workflowRequest.getApplicationDTO());
        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(false);

        ApplicationDTO applicationDTO = orchestrationService.update(workflowRequest);

        assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());
    }

    @Test
    void givenMagsCourtWithVariation_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(applicationDTO);
        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(true);

        ApplicationDTO expected = orchestrationService.update(workflowRequest);

        assertThat(expected.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);
    }

    @Test
    void givenCrownCourt_whenUpdateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);

        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenReturn(applicationDTO);

        ApplicationDTO expected = orchestrationService.update(workflowRequest);

        assertThat(expected.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(getHardshipReviewDTO());

        assertThat(expected.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest);

    }

    @Test
    void givenCrownCourtInProgressHardship_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);
        workflowRequest.getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .getAsessmentStatus().setStatus(AssessmentStatusDTO.INCOMPLETE);

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.getAsessmentStatus().setStatus(AssessmentStatusDTO.INCOMPLETE);
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(expected);

    }

    @Test
    void givenCrownCourtWithMissingAssessment_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);
        workflowRequest.getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setAsessmentStatus(null);

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.CROWN_COURT).getCrownCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(expected);

    }

    @Test
    void givenMagCourtInProgressHardship_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        workflowRequest.getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getMagCourtHardship()
                .getAsessmentStatus().setStatus(AssessmentStatusDTO.INCOMPLETE);

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.getAsessmentStatus().setStatus(AssessmentStatusDTO.INCOMPLETE);
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(expected);

    }

    @Test
    void givenMagCourtWithMissingAssessment_whenUpdateIsInvoked_thenCheckActionsIsNotInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.MAGISTRATE);
        workflowRequest.getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getMagCourtHardship()
                .setAsessmentStatus(null);

        ApplicationDTO actual = orchestrationService.update(workflowRequest);

        HardshipReviewDTO expected = getHardshipOverviewDTO(CourtType.MAGISTRATE).getMagCourtHardship();
        expected.setAsessmentStatus(null);
        assertThat(actual.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(expected);

    }

    @Test
    void givenCrownCourt_whenUpdateIsInvokedAndExceptionThrownInSP_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);

        applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);
        when(maatCourtDataService.invokeStoredProcedure(any(ApplicationDTO.class), any(UserDTO.class), any(), any()))
                .thenThrow(new APIClientException());
        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(APIClientException.class);
        Mockito.verify(hardshipService, times(1)).rollbackHardship(any());
    }

    @Test
    void givenCrownCourt_whenUpdateIsInvokedAndExceptionThrownInCalculateContribution_thenRollbackHardshipIsInvoked() {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);

        applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship()
                .setSolictorsCosts(TestModelDataBuilder.getHRSolicitorsCostsDTO());
        when(contributionService.calculateContribution(workflowRequest))
                .thenThrow(new APIClientException());
        assertThatThrownBy(() -> orchestrationService.update(workflowRequest))
                .isInstanceOf(APIClientException.class);
        Mockito.verify(hardshipService, times(1)).rollbackHardship(any());
    }

}
