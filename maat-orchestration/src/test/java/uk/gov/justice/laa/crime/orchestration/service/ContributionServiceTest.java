package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.mapper.ContributionMapper;
import uk.gov.justice.laa.crime.orchestration.model.contribution.ApiMaatCalculateContributionResponse;

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
}
