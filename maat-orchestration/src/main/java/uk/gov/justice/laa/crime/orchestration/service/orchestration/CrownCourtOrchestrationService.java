package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtOverviewDTO;
import uk.gov.justice.laa.crime.orchestration.enums.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrownCourtOrchestrationService {

    private final ProceedingsService proceedingsService;
    private final MaatCourtDataService maatCourtDataService;

    public ApplicationDTO update(WorkflowRequest request) {

        log.info("calling update()");
        ApplicationDTO application = request.getApplicationDTO();

        log.info("before calling UPDATE_DBMS_TRANSACTION_ID");
        application = maatCourtDataService.invokeStoredProcedure(
                application, request.getUserDTO(), StoredProcedure.UPDATE_DBMS_TRANSACTION_ID
        );

        log.info("before calling PRE_UPDATE_CHECKS");
        
        application = maatCourtDataService.invokeStoredProcedure(
                application, request.getUserDTO(), StoredProcedure.PRE_UPDATE_CHECKS
        );
        log.info("after calling PRE_UPDATE_CHECKS");

        log.info("before calling updateCrownCourt()");
        application = proceedingsService.updateCrownCourt(application, request.getUserDTO());
        log.info("after calling updateCrownCourt()");

        if (hasNewOutcome(application)) {
            log.info("hasNewOutcome--TRUE");
            log.info("before calling xx_process_activity_and_get_correspondence()");
            application = maatCourtDataService.invokeStoredProcedure(
                    application, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
            );
            log.info("after calling xx_process_activity_and_get_correspondence()");
        }
        log.info("before calling UPDATE_CC_APPLICANT_AND_APPLICATION()");
        application = maatCourtDataService.invokeStoredProcedure(
                application, request.getUserDTO(), StoredProcedure.UPDATE_CC_APPLICANT_AND_APPLICATION
        );
        log.info("after calling UPDATE_CC_APPLICANT_AND_APPLICATION()");

        application.setTransactionId(null);

        return application;
    }

    private boolean hasNewOutcome(ApplicationDTO application) {
        CrownCourtOverviewDTO crownCourtOverview = application.getCrownCourtOverviewDTO();
        if (null != crownCourtOverview.getCrownCourtSummaryDTO()
                && null != crownCourtOverview.getCrownCourtSummaryDTO().getOutcomeDTOs()) {
            return crownCourtOverview.getCrownCourtSummaryDTO().getOutcomeDTOs().stream()
                    .anyMatch(outcomeDTO -> null == outcomeDTO.getDateSet());
        }
        return false;
    }
}
