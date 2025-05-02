package uk.gov.justice.laa.crime.orchestration.client;

import java.util.List;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionRequest;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCalculateContributionResponse;
import uk.gov.justice.laa.crime.common.model.contribution.ApiMaatCheckContributionRuleRequest;
import uk.gov.justice.laa.crime.common.model.contribution.common.ApiContributionSummary;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

@HttpExchange
public interface CrownCourtContributionsApiClient {

  @PostExchange("/contribution/calculate-contribution")
  ApiMaatCalculateContributionResponse calculate(@RequestBody ApiMaatCalculateContributionRequest request);

  @PostExchange("/contribution/check-contribution-rule")
  Boolean isContributionRule(@RequestBody ApiMaatCheckContributionRuleRequest request);

  @GetExchange("/contribution/summaries/{repId}")
  List<ApiContributionSummary> getContributionSummary(@PathVariable Long repId);
}
