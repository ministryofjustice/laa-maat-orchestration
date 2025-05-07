package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.CurrentStatus;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.service.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipOrchestrationService implements AssessmentOrchestrator<HardshipReviewDTO> {
    private final HardshipService hardshipService;
    private final ContributionService contributionService;
    private final ProceedingsService proceedingsService;
    private final AssessmentSummaryService assessmentSummaryService;
    private final MaatCourtDataService maatCourtDataService;
    private final HardshipMapper hardshipMapper;
    private final WorkflowPreProcessorService workflowPreProcessorService;
    private final RepOrderService repOrderService;
    private final ApplicationService applicationService;

    private final ApplicationTrackingMapper applicationTrackingMapper;
    private final ApplicationTrackingDataService applicationTrackingDataService;

    public HardshipReviewDTO find(int hardshipReviewId) {
        return hardshipService.find(hardshipReviewId);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        // invoke the validation service to Check user has rep order reserved

        CourtType courtType = request.getCourtType();
        Action action = (courtType == CourtType.MAGISTRATE) ? Action.CREATE_MAGS_HARDSHIP : Action.CREATE_CROWN_HARDSHIP;
        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(request);

        validate(request, action, repOrderDTO);

        ApplicationDTO application = request.getApplicationDTO();

        ApiPerformHardshipResponse performHardshipResponse = hardshipService.create(request);
        applicationService.updateDateModified(request, application);
        try {
            // Need to refresh from DB as HardshipDetail ids may have changed
            HardshipReviewDTO newHardship = hardshipService.find(performHardshipResponse.getHardshipReviewId());

            if (courtType == CourtType.MAGISTRATE) {
                request.getApplicationDTO().getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship().setMagCourtHardship(newHardship);
                if (isAssessmentComplete(newHardship.getAsessmentStatus())) {
                    application = processMagCourtHardshipRules(request);
                }
            } else if (courtType == CourtType.CROWN_COURT) {
                request.getApplicationDTO().getAssessmentDTO()
                        .getFinancialAssessmentDTO()
                        .getHardship().setCrownCourtHardship(newHardship);
                if (isAssessmentComplete(newHardship.getAsessmentStatus())) {
                    application = checkActionsAndUpdateApplication(request, repOrderDTO);
                }
            }

            // Update assessment summary view - displayed on the application tab
            AssessmentSummaryDTO hardshipSummary = assessmentSummaryService.getSummary(newHardship, courtType);
            assessmentSummaryService.updateApplication(application, hardshipSummary);
        } catch (Exception ex) {
            log.warn("Create Hardship Review failed with the exception: {}", ex.getMessage(), ex);
            hardshipService.rollback(request);
            Sentry.captureException(ex);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        return application;
    }
    
    public ApplicationDTO update(WorkflowRequest request) {
        // invoke the validation service to check that data has not been modified by another user
        // invoke the validation service to Check user has rep order reserved
        CourtType courtType = request.getCourtType();
        Action action = (courtType == CourtType.MAGISTRATE) ? Action.UPDATE_MAGS_HARDSHIP : Action.UPDATE_CROWN_HARDSHIP;
        RepOrderDTO repOrderDTO = repOrderService.getRepOrder(request);

        validate(request, action, repOrderDTO);

        hardshipService.update(request);
        applicationService.updateDateModified(request, request.getApplicationDTO());
        try {
            HardshipOverviewDTO hardshipOverviewDTO = request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO()
                    .getHardship();
            HardshipReviewDTO hardshipReviewDTO = (courtType == CourtType.MAGISTRATE) ?
                    hardshipOverviewDTO.getMagCourtHardship() : hardshipOverviewDTO.getCrownCourtHardship();

            if (isAssessmentComplete(hardshipReviewDTO.getAsessmentStatus())) {
                if (courtType == CourtType.MAGISTRATE) {
                    request.setApplicationDTO(processMagCourtHardshipRules(request));
                } else if (courtType == CourtType.CROWN_COURT) {
                    request.setApplicationDTO(checkActionsAndUpdateApplication(request, repOrderDTO));
                }
            }

            // Update assessment summary view - displayed on the application tab
            AssessmentSummaryDTO hardshipSummary = assessmentSummaryService.getSummary(hardshipReviewDTO, courtType);
            assessmentSummaryService.updateApplication(request.getApplicationDTO(), hardshipSummary);
        } catch (Exception ex) {
            log.warn("Update Hardship Review failed with the exception: {}", ex.getMessage(), ex);
            hardshipService.rollback(request);
            Sentry.captureException(ex);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        return request.getApplicationDTO();
    }

    private void validate(WorkflowRequest request, Action action, RepOrderDTO repOrderDTO) {
        UserActionDTO userActionDTO = hardshipMapper.getUserActionDTO(request, action);

        workflowPreProcessorService.preProcessRequest(request, repOrderDTO, userActionDTO);

    }

    private ApplicationDTO processMagCourtHardshipRules(WorkflowRequest request) {
        // call assessments.determine_mags_rep_decision stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.DETERMINE_MAGS_REP_DECISION
        ));
        if (contributionService.isVariationRequired(request.getApplicationDTO())) {
            return contributionService.calculate(request);
        }
        return request.getApplicationDTO();
    }

    private boolean isAssessmentComplete(AssessmentStatusDTO assessmentStatusDTO) {
        return assessmentStatusDTO != null && CurrentStatus.COMPLETE.getStatus().equals(assessmentStatusDTO.getStatus());
    }

    /**
     * This method performs the logic from the following stored procedures:
     * crown_court.check_crown_court_actions(p_application_object => p_application_object);
     * application.update_cc_application(p_application_object => p_application_object);
     */
    private ApplicationDTO checkActionsAndUpdateApplication(WorkflowRequest request, RepOrderDTO repOrderDTO) {

        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.UPDATE_DBMS_TRANSACTION_ID
        ));

        request.setApplicationDTO(contributionService.calculate(request));

        // call application.pre_update_cc_application stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION
        ));

        proceedingsService.updateApplication(request, repOrderDTO);

        // Call application.handle_eform_result stored procedure OR Equivalent ATS service endpoint
        ApplicationTrackingOutputResult applicationTrackingOutputResult = applicationTrackingMapper.build(request, repOrderDTO);
        if (null != applicationTrackingOutputResult.getUsn()) {
            applicationTrackingDataService.sendTrackingOutputResult(applicationTrackingOutputResult);
        }
        // Call crown_court.xx_process_activity_and_get_correspondence stored procedure
        request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                request.getApplicationDTO(), request.getUserDTO(),
                StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
        ));

        request.getApplicationDTO().setTransactionId(null);

        return request.getApplicationDTO();
    }
}
