package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.client.ApplicationTrackingApiClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationTrackingApiService {

    private static final String REQUEST_STRING = "Request to Application Tracking Service: {}";
    private final ApplicationTrackingApiClient applicationTrackingApiClient;

    public void handleCrimeApplyResult(ApplicationTrackingOutputResult request) {
        log.debug(REQUEST_STRING, request);
        applicationTrackingApiClient.handleCrimeApplyResult(request);
    }
}
