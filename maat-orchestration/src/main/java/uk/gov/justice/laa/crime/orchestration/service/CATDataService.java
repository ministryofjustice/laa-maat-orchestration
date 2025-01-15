package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.service.api.CATApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CATDataService {

    private final CATApiService service;


    public void handleEformResult(ApplicationTrackingOutputResult request) {
        service.handleEformResult(request);
    }
}
