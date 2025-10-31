package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.client.ApplicationTrackingApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationTrackingApiService {

    private static final String REQUEST_STRING = "Request to Application Tracking Service: {}";
    private final ApplicationTrackingApiClient applicationTrackingApiClient;

    public void sendTrackingOutputResult(ApplicationTrackingOutputResult request) {
        log.debug(REQUEST_STRING, request);
        applicationTrackingApiClient.sendTrackingOutputResult(request);
    }
}
