package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class ContributionServiceTest {

    @Mock
    private ContributionMapper contributionMapper;

    @Mock
    private ContributionApiService contributionApiService;

    @InjectMocks
    private ContributionService contributionService;

    @Test
    void givenWorkflowRequest_whenCalculateContributionIsInvoked_thenContributionServiceIsCalledAndResponseIsMapped() {
        WorkflowRequest request = WorkflowRequest.builder().build();
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
}