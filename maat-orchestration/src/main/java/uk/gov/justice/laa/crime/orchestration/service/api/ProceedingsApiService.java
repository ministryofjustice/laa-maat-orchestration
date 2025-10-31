package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtProceedingApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsApiService {

    private final CrownCourtProceedingApiClient crownCourtProceedingApiClient;
    private static final String RESPONSE_STRING = "Response from Proceedings Service: {}";
    private static final String REQUEST_STRING = "Request to Proceedings Service: {}";

    public ApiUpdateApplicationResponse updateApplication(ApiUpdateApplicationRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiUpdateApplicationResponse response = crownCourtProceedingApiClient.updateApplication(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public ApiUpdateCrownCourtOutcomeResponse updateCrownCourt(ApiUpdateCrownCourtRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiUpdateCrownCourtOutcomeResponse response = crownCourtProceedingApiClient.updateCrownCourt(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
