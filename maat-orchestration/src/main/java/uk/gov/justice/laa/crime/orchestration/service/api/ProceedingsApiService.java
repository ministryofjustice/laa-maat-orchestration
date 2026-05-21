package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtProceedingApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsApiService {

    private final CrownCourtProceedingApiClient crownCourtProceedingApiClient;
    private static final String REQUEST_STRING = "Request to Proceedings Service: {}";

    public ApiDetermineMagsRepDecisionResponse determineMagsRepDecision(ApiDetermineMagsRepDecisionRequest request) {
        log.debug(REQUEST_STRING, request);
        return crownCourtProceedingApiClient.determineMagsRepDecision(request);
    }

    public ApiUpdateApplicationResponse updateApplication(ApiUpdateApplicationRequest request) {
        log.debug(REQUEST_STRING, request);
        return crownCourtProceedingApiClient.updateApplication(request);
    }

    public ApiUpdateCrownCourtOutcomeResponse updateCrownCourt(ApiUpdateCrownCourtRequest request) {
        log.debug(REQUEST_STRING, request);
        return crownCourtProceedingApiClient.updateCrownCourt(request);
    }
}
