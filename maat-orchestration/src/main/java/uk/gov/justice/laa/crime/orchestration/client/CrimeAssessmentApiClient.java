package uk.gov.justice.laa.crime.orchestration.client;

import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

@HttpExchange
public interface CrimeAssessmentApiClient {

    @GetExchange("/iojappeal/lookup-by-legacy-id/{legacyAppealId}")
    ApiGetIojAppealResponse getIojAppeal(@PathVariable Integer legacyAppealId);
}
