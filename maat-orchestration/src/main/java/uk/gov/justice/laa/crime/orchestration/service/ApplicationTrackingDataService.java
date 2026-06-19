package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.ApplicationTrackingApiService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationTrackingDataService {

    private final ApplicationTrackingApiService service;
    private final ApplicationTrackingMapper applicationTrackingMapper;

    public void sendTrackingOutputResult(
            WorkflowRequest workflowRequest,
            RepOrderDTO repOrderDTO,
            AssessmentType assessmentType,
            RequestSource requestSource) {

        Long usn;

        log.info("TESTING: STARTING TRACKING");
        switch (requestSource) {
            case HARDSHIP, MEANS_ASSESSMENT ->
                usn = workflowRequest
                        .getApplicationDTO()
                        .getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getUsn();
            case PASSPORT_IOJ -> usn = workflowRequest.getApplicationDTO().getUsn();
            default -> throw new IllegalStateException("Unknown request source " + requestSource);
        }

        log.info("USN: " + usn);
        if (usn == null) {
            return;
        }

        ApplicationTrackingOutputResult request =
                applicationTrackingMapper.build(workflowRequest, repOrderDTO, assessmentType, requestSource, usn);

        log.info("TESTING: SENDING TRACKING");
        service.sendTrackingOutputResult(request);
    }
}
