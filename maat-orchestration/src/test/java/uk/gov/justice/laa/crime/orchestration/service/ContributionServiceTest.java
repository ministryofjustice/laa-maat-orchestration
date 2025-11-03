package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.CONTRIBUTIONS_ID;

import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.enums.InitAssessmentResult;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CaseDetailDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.FullAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.InitialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.ContributionApiService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
        contributionService.calculate(request);
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
    void givenApplicationDTOWithNoAssessment_whenIsRecalculationRequiredIsInvoked_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder().build();
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithNoFinancialAssessment_whenIsRecalculationRequiredIsInvoked_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder().build())
                .build();
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithNoInitialAssessment_whenIsRecalculationRequiredIsInvoked_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder()
                        .financialAssessmentDTO(FinancialAssessmentDTO.builder().build())
                        .build())
                .build();
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isFalse();
    }

    @Test
    void givenApplicationDTOWithInProgressInitialAssessment_whenIsRecalculationRequiredIsInvoked_thenFalseIsReturned() {
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
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isFalse();
    }

    @Test
    @DisplayName(
            "Application DTO With In Progress Full And Passed Init Assessment - isRecalculationRequired returns True")
    void givenApplicationDTOWithFullAndInitAssessment_whenIsRecalculationRequiredIsInvoked_thenTrueIsReturned() {
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
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isTrue();
    }

    @Test
    void givenAppealCC_whenIsRecalculationRequiredIsInvoked_thenTrueIsReturned() {
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
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isTrue();
    }

    @Test
    void givenApplicationDTOWithInProgressPassported_whenIsRecalculationRequiredIsInvoked_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .passportedDTO(PassportedDTO.builder()
                        .assessementStatusDTO(AssessmentStatusDTO.builder()
                                .status(CurrentStatus.IN_PROGRESS.getStatus())
                                .build())
                        .build())
                .build();
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isFalse();
    }

    @Test
    void givenInitialAssessmentWithNoStatus_whenIsRecalculationRequiredIsInvoked_thenFalseIsReturned() {
        ApplicationDTO applicationDTO = ApplicationDTO.builder()
                .assessmentDTO(AssessmentDTO.builder()
                        .financialAssessmentDTO(FinancialAssessmentDTO.builder()
                                .initial(InitialAssessmentDTO.builder().build())
                                .build())
                        .build())
                .build();
        assertThat(contributionService.isRecalculationRequired(applicationDTO)).isFalse();
    }
}
