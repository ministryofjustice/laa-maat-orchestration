package uk.gov.justice.laa.crime.orchestration.client;

import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface CrownCourtContributionsApiClient {

    @PostExchange("/calculate-contribution")
    ApiMaatCalculateContributionResponse calculateContribution(
            @RequestBody ApiMaatCalculateContributionRequest request);

    @PostExchange("/check-contribution-rule")
    Boolean isContributionRule(@RequestBody ApiMaatCheckContributionRuleRequest request);

    @GetExchange("/summaries/{repId}")
    List<ApiContributionSummary> getContributionSummary(@PathVariable Long repId);
}
