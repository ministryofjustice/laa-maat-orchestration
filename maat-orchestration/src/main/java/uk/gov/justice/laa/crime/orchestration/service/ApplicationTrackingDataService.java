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

        log.info("TESTING: STARTING APPLICATION TRACKING");
        Long usn = workflowRequest.getApplicationDTO().getUsn();
        log.info("TESTING: USN FROM APPLICATIONDTO: " + usn);
        // Use the USN from the financialAssessment if there isn't one on the applicationDTO
        if (usn == null
                && workflowRequest.getApplicationDTO().getAssessmentDTO() != null
                && workflowRequest.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO() != null
                && workflowRequest
                                .getApplicationDTO()
                                .getAssessmentDTO()
                                .getFinancialAssessmentDTO()
                                .getUsn()
                        != null) {
            usn = workflowRequest
                    .getApplicationDTO()
                    .getAssessmentDTO()
                    .getFinancialAssessmentDTO()
                    .getUsn();

            log.info("TESTING: USN FROM FINANCIAL ASSESSMENT: " + usn);
        }

        if (usn == null) {
            return;
        }

        log.info("TESTING: BUILDING APPLICATION TRACKING");
        ApplicationTrackingOutputResult request =
                applicationTrackingMapper.build(workflowRequest, repOrderDTO, assessmentType, requestSource, usn);

        log.info("TESTING: CALLING APPLICATION TRACKING");
        service.sendTrackingOutputResult(request);
    }
}
