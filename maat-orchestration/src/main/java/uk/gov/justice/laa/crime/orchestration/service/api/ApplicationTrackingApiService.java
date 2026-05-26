package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.client.ApplicationTrackingApiClient;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicationTrackingApiService {

    private final ApplicationTrackingApiClient applicationTrackingApiClient;

    public void sendTrackingOutputResult(ApplicationTrackingOutputResult request) {
        applicationTrackingApiClient.sendTrackingOutputResult(request);
    }
}
