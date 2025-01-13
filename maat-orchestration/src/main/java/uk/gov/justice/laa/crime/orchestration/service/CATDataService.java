package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.SysGenString;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.CATApiService;
import uk.gov.justice.laa.crime.util.DateUtil;

import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType.MEANS_FULL;
import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType.MEANS_INIT;
import static uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource.MEANS_ASSESSMENT;

@Slf4j
@Service
@RequiredArgsConstructor
public class CATDataService {

    private final CATApiService service;


    public void handleEformResult(ApplicationTrackingOutputResult request) {
        service.handleEformResult(request);
    }
}
