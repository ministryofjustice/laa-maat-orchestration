package uk.gov.justice.laa.crime.orchestration.client;

import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange
public interface CrimeAssessmentApiClient {

    @GetExchange("/ioj-appeals/lookup-by-legacy-id/{legacyAppealId}")
    ApiGetIojAppealResponse getIojAppeal(@PathVariable Integer legacyAppealId);

    @PostExchange("/ioj-appeals")
    ApiCreateIojAppealResponse createIojAppeal(@RequestBody ApiCreateIojAppealRequest request);
}
