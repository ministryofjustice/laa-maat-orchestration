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
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ContributionsDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.helper.CrownCourtHelper;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @InjectMocks
    private HardshipOrchestrationService orchestrationService;

    @Test
    void givenHardshipReviewId_whenFindIsInvoked_thenHardshipServiceIsCalled() {
        orchestrationService.find(Constants.TEST_HARDSHIP_REVIEW_ID);
        verify(hardshipService).find(Constants.TEST_HARDSHIP_REVIEW_ID);
    }

    private WorkflowRequest setupCreateStubs(CourtType courtType) {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkflowRequestWithHardship(courtType);

        when(crownCourtHelper.getCourtType(any(ApplicationDTO.class)))
                .thenReturn(courtType);

        ApiPerformHardshipResponse performHardshipResponse = TestModelDataBuilder.getApiPerformHardshipResponse();
        when(hardshipService.createHardship(workflowRequest))
                .thenReturn(performHardshipResponse);

        when(hardshipService.find(performHardshipResponse.getHardshipReviewId()))
                .thenReturn(TestModelDataBuilder.getHardshipReviewDTO());

        when(assessmentSummaryService.getSummary(any(HardshipReviewDTO.class), eq(courtType)))
                .thenReturn(TestModelDataBuilder.getAssessmentSummaryDTO());

        return workflowRequest;
    }

    @Test
    void givenIsMagsCourtAndNoVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {

        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE);

        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(false);

        ApplicationDTO applicationDTO = orchestrationService.create(workflowRequest);

        assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(TestModelDataBuilder.getHardshipReviewDTO());

        verify(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
    }

    @Test
    void givenIsMagsCourtWithVariation_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.MAGISTRATE);

        when(contributionService.isVariationRequired(any(ApplicationDTO.class)))
                .thenReturn(true);

        ContributionsDTO contributionsDTO = TestModelDataBuilder.getContributionsDTO();
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(contributionsDTO);

        ApplicationDTO applicationDTO = orchestrationService.create(workflowRequest);

        assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getMagCourtHardship())
                .isEqualTo(TestModelDataBuilder.getHardshipReviewDTO());

        assertThat(applicationDTO.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
    }

    @Test
    void givenIsCrownCourt_whenCreateIsInvoked_thenApplicationDTOIsUpdatedWithNewHardship() {
        WorkflowRequest workflowRequest = setupCreateStubs(CourtType.CROWN_COURT);

        ContributionsDTO contributionsDTO = TestModelDataBuilder.getContributionsDTO();
        when(contributionService.calculateContribution(workflowRequest))
                .thenReturn(contributionsDTO);

        ApplicationDTO applicationDTO = orchestrationService.create(workflowRequest);

        assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getHardship().getCrownCourtHardship())
                .isEqualTo(TestModelDataBuilder.getHardshipReviewDTO());

        assertThat(applicationDTO.getCrownCourtOverviewDTO().getContribution())
                .isEqualTo(contributionsDTO);

        verify(proceedingsService).updateApplication(workflowRequest);

        verify(assessmentSummaryService)
                .updateApplication(any(ApplicationDTO.class), any(AssessmentSummaryDTO.class));
    }


}