package uk.gov.justice.laa.crime.orchestration.client;

import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;

@HttpExchange
public interface CrownCourtProceedingApiClient {

    @PostExchange("/determine-mags-rep-decision")
    ApiDetermineMagsRepDecisionResponse determineMagsRepDecision(
            @RequestBody ApiDetermineMagsRepDecisionRequest request);

    @PutExchange
    ApiUpdateApplicationResponse updateApplication(@RequestBody ApiUpdateApplicationRequest request);

    @PutExchange("/update-crown-court")
    ApiUpdateCrownCourtOutcomeResponse updateCrownCourt(@RequestBody ApiUpdateCrownCourtRequest request);
}
