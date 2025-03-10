package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.service.api.CrimeApplicationTrackingApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrimeApplicationTrackingService {

    private final CrimeApplicationTrackingApiService service;


    public void sendApplicationTrackingData(ApplicationTrackingOutputResult request) {
        if (null != request.getUsn()) {
            service.sendApplicationTrackingData(request);
        }
    }
}
