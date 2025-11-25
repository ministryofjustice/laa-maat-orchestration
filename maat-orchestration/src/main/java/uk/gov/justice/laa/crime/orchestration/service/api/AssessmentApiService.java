package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentApiService {

    private static final String REQUEST_STRING = "Request to Assessment Service: {}";
    private static final String RESPONSE_STRING = "Response from Assessment Service: {}";

    private final CrimeAssessmentApiClient assessmentApiClient;

    public ApiGetIojAppealResponse find(int appealId) {
        log.debug("Request to Assessment Service for IoJ Appeal ID: {}", appealId);

        // 404s are intercepted by the WebClientFilters, so we re-throw the exception here to be
        // caught by our DefaultExceptionHandler
        ApiGetIojAppealResponse apiGetIojAppealResponse = Optional.ofNullable(
                        assessmentApiClient.getIojAppeal(appealId))
                .orElseThrow(() ->
                        new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null));

        log.debug(RESPONSE_STRING, apiGetIojAppealResponse);
        return apiGetIojAppealResponse;
    }

    public ApiCreateIojAppealResponse create(ApiCreateIojAppealRequest request) {
        log.debug(REQUEST_STRING, request);
        ApiCreateIojAppealResponse response = assessmentApiClient.createIojAppeal(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }
}
