package uk.gov.justice.laa.crime.orchestration.service.api;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.MockServicesConfiguration;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;

@ExtendWith({MockitoExtension.class})
class ContributionApiServiceTest {

    @Mock
    private RestAPIClient contributionApiClient;

    @InjectMocks
    private ContributionApiService contributionApiService;

    @Spy
    private ServicesConfiguration configuration = MockServicesConfiguration.getConfiguration(1000);

    @Test
    void givenValidRequest_whenCalculateContributionIsInvoked_thenContributionServiceIsCalled() {
        contributionApiService.calculate(new ApiMaatCalculateContributionRequest());
        verify(contributionApiClient)
                .post(any(ApiMaatCalculateContributionRequest.class), any(), anyString(), anyMap());

    }

    @Test
    void givenValidRequest_whenIsContributionRuleIsInvoked_thenContributionServiceIsCalled() {
        contributionApiService.isContributionRule(new ApiMaatCheckContributionRuleRequest());
        verify(contributionApiClient)
                .post(any(ApiMaatCheckContributionRuleRequest.class), any(), anyString(), anyMap());
    }

    @Test
void givenValidRequest_whenGetContributionSummaryIsInvoked_thenContributionServiceIsCalled() {
        contributionApiService.getContributionSummary(1L);
        verify(contributionApiClient)
                .get(any(), anyString(), anyMap(), anyLong());
    }
}
