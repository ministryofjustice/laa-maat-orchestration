package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtContributionsApiClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class ContributionApiServiceTest {

    @Mock
    private CrownCourtContributionsApiClient contributionApiClient;

    @InjectMocks
    private ContributionApiService contributionApiService;

    @Test
    void givenValidRequest_whenCalculateContributionIsInvoked_thenContributionServiceIsCalled() {
        contributionApiService.calculate(new ApiMaatCalculateContributionRequest());
        verify(contributionApiClient).calculate(any(ApiMaatCalculateContributionRequest.class));

    }

    @Test
    void givenValidRequest_whenIsContributionRuleIsInvoked_thenContributionServiceIsCalled() {
        contributionApiService.isContributionRule(new ApiMaatCheckContributionRuleRequest());
        verify(contributionApiClient)
            .isContributionRule(any(ApiMaatCheckContributionRuleRequest.class));
    }

    @Test
void givenValidRequest_whenGetContributionSummaryIsInvoked_thenContributionServiceIsCalled() {
        contributionApiService.getContributionSummary(1L);
        verify(contributionApiClient).getContributionSummary(anyLong());
    }
}
