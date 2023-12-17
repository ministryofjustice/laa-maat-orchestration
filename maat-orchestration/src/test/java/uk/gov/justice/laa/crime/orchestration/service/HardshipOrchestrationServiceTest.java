package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.helper.CrownCourtHelper;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.*;

@ExtendWith({MockitoExtension.class})
class HardshipOrchestrationServiceTest {

    @Mock
    private HardshipService hardshipService;

    @Mock
    private CrownCourtHelper crownCourtHelper;

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
        orchestrationService.find(Constants.TEST_HARDSHIP_REVIEW_ID);
        verify(hardshipService).find(Constants.TEST_HARDSHIP_REVIEW_ID);
    }

    private WorkflowRequest setupCreateStubs(CourtType courtType) {
        WorkflowRequest workflowRequest = buildWorkflowRequestWithHardship(courtType);

        when(crownCourtHelper.getCourtType(any(ApplicationDTO.class)))
                .thenReturn(courtType);

        ApiPerformHardshipResponse performHardshipResponse = getApiPerformHardshipResponse();
        when(hardshipService.createHardship(workflowRequest))
                .thenReturn(performHardshipResponse);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(getHardshipReviewDTO());

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(courtType)))
                .thenReturn(getAssessmentSummaryDTO());

        return workflowRequest;
    }

    @Test
    void givenIsMagsCourtAndNoVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {

        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE);

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
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE);

        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(true);


        ContributionsDTO contributionsDTO = getContributionsDTO();
        ApplicationDTO applicationDTO = getApplicationDTOWithHardship(CourtType.MAGISTRATE);
        applicationDTO.getCrownCourtOverviewDTO().setContribution(contributionsDTO);
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(applicationDTO);

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
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT);

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


}
