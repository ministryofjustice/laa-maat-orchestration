package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.CONTRIBUTIONS_ID;

@ExtendWith({MockitoExtension.class})
class ContributionServiceTest {

    @Mock
    private ContributionMapper contributionMapper;

    @Mock
    private ContributionApiService contributionApiService;
    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private ContributionService contributionService;

    @Test
    void givenWorkflowRequest_whenCalculateContributionIsInvoked_thenContributionServiceIsCalledAndResponseIsMapped() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkflowRequestWithHardship(CourtType.CROWN_COURT);
        when(contributionApiService.calculate(any()))
                .thenReturn(new ApiMaatCalculateContributionResponse().withContributionId(CONTRIBUTIONS_ID));
        contributionService.calculateContribution(request);
        verify(contributionApiService).calculate(any());
        verify(contributionMapper).workflowRequestToMaatCalculateContributionRequest(any());
        verify(contributionMapper).maatCalculateContributionResponseToContributionsDto(any());
    }

    @Test
    void givenApplicationDTO_whenIsVariationRequiredIsInvoked_thenContributionServiceIsCalled() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder().build();
        contributionService.isVariationRequired(applicationDTO);
        verify(contributionApiService).isContributionRule(any());
        verify(contributionMapper).applicationDtoToCheckContributionRuleRequest(any(ApplicationDTO.class));
    }

    @Test
    void givenApplicationDTOWithNoAssessment_whenIsCalculateContributionReqdIsCalled_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder().build();
        assertThat(contributionService.isCalculateContributionReqd(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithNoFinancialAssessment_whenIsCalculateContributionReqdIsCalled_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder().build())
                .build();
        assertThat(contributionService.isCalculateContributionReqd(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithNoInitialAssessment_whenIsCalculateContributionReqdIsCalled_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder()
                        .financialAssessmentDTO(FinancialAssessmentDTO.builder().build())
                        .build())
                .build();
        assertThat(contributionService.isCalculateContributionReqd(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithInProgressInitialAssessment_whenIsCalculateContributionReqdIsCalled_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder()
                        .financialAssessmentDTO(FinancialAssessmentDTO.builder()
                                .initial(InitialAssessmentDTO.builder()
                                        .assessmnentStatusDTO(AssessmentStatusDTO.builder()
                                                .status(CurrentStatus.IN_PROGRESS.getStatus())
                                                .build())
                                        .build())
                                .build())
                        .build())
                .build();
        assertThat(contributionService.isCalculateContributionReqd(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithInProgressFullAndPassedInitAssessment_whenIsCalculateContributionReqdIsCalled_thenTrueIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder()
                        .financialAssessmentDTO(FinancialAssessmentDTO.builder()
                                .full(FullAssessmentDTO.builder()
                                        .assessmnentStatusDTO(AssessmentStatusDTO.builder()
                                                .status(CurrentStatus.IN_PROGRESS.getStatus())
                                                .build())
                                        .build())
                                .initial(InitialAssessmentDTO.builder()
                                        .assessmnentStatusDTO(AssessmentStatusDTO.builder()
                                                .status(CurrentStatus.COMPLETE.getStatus())
                                                .build())
                                        .result(InitAssessmentResult.PASS.getResult())
                                        .build())
                                .build())
                        .build())
                .build();
        assertThat(contributionService.isCalculateContributionReqd(applicationDTO)).isTrue();
    }

    @Test
    void givenAppealCC_whenIsCalculateContributionReqdIsCalled_thenTrueIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .caseDetailsDTO(CaseDetailDTO.builder()
                        .caseType(CaseType.APPEAL_CC.getCaseType())
                        .build())
                .assessmentDTO(AssessmentDTO.builder()
                        .financialAssessmentDTO(FinancialAssessmentDTO.builder()
                                .initial(InitialAssessmentDTO.builder()
                                        .assessmnentStatusDTO(AssessmentStatusDTO.builder()
                                                .status(CurrentStatus.COMPLETE.getStatus())
                                                .build())
                                        .result(InitAssessmentResult.FAIL.getResult())
                                        .build())
                                .build())
                        .build())
                .build();
        assertThat(contributionService.isCalculateContributionReqd(applicationDTO)).isTrue();
    }}
