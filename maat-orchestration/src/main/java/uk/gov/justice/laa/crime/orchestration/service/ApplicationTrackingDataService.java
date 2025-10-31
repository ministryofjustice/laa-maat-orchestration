package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.service.api.ApplicationTrackingApiService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationTrackingDataService {

    private final ApplicationTrackingApiService service;

    public void sendTrackingOutputResult(ApplicationTrackingOutputResult request) {
        service.sendTrackingOutputResult(request);
    }
}
