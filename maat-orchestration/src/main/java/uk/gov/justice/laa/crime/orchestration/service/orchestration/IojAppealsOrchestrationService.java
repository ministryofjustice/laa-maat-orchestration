package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;
import uk.gov.justice.laa.crime.orchestration.exception.MaatOrchestrationException;
import uk.gov.justice.laa.crime.orchestration.exception.RollbackException;
import uk.gov.justice.laa.crime.orchestration.mapper.ApplicationTrackingMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationService;
import uk.gov.justice.laa.crime.orchestration.service.ApplicationTrackingDataService;
import uk.gov.justice.laa.crime.orchestration.service.AssessmentSummaryService;
import uk.gov.justice.laa.crime.orchestration.service.ContributionService;
import uk.gov.justice.laa.crime.orchestration.service.IojAppealService;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;
import uk.gov.justice.laa.crime.orchestration.service.RepOrderService;
import uk.gov.justice.laa.crime.orchestration.service.WorkflowPreProcessorService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealsOrchestrationService {

    private final AssessmentSummaryService assessmentSummaryService;
    private final IojAppealService iojAppealService;
    private final MaatCourtDataService maatCourtDataService;
    private final ContributionService contributionService;
    private final IojAppealMapper iojAppealMapper;
    private final ProceedingsService proceedingsService;
    private final RepOrderService repOrderService;
    private final WorkflowPreProcessorService workflowPreProcessorService;
    private final ApplicationTrackingMapper applicationTrackingMapper;
    private final ApplicationTrackingDataService applicationTrackingDataService;
    private final ApplicationService applicationService;

    public IOJAppealDTO find(int appealId) {
        return iojAppealService.find(appealId);
    }

    public ApplicationDTO create(WorkflowRequest request) {

        validateRequiredCreateFields(request);

        RepOrderDTO repOrderDto = repOrderService.getRepOrder(request);

        if (repOrderDto == null) {
            log.error("Could not find rep order for request {}", request);
            throw new MaatOrchestrationException(request.getApplicationDTO());
        }

        UserActionDTO userActionDTO = iojAppealMapper.getUserActionDTO(request);
        workflowPreProcessorService.preProcessRequest(request, repOrderDto, userActionDTO);

        String appealId = iojAppealService.create(request);

        try {
            proceedingsService.determineMagsRepDecision(request);
            request.setApplicationDTO(contributionService.calculate(request));
            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    request.getApplicationDTO(), request.getUserDTO(), StoredProcedure.PRE_UPDATE_CC_APPLICATION));

            proceedingsService.updateApplication(request, repOrderDto);

            request.setApplicationDTO(maatCourtDataService.invokeStoredProcedure(
                    request.getApplicationDTO(),
                    request.getUserDTO(),
                    StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE));

            AssessmentSummaryDTO assessmentSummaryDTO = assessmentSummaryService.getSummary(
                    request.getApplicationDTO().getAssessmentDTO().getIojAppeal());
            assessmentSummaryService.updateApplication(request.getApplicationDTO(), assessmentSummaryDTO);

            applicationService.updateDateModified(request, request.getApplicationDTO());

            // Call Application Tracking Service endpoint
            ApplicationTrackingOutputResult applicationTrackingOutputResult = applicationTrackingMapper.build(
                    request, repOrderDto, AssessmentType.IOJ, RequestSource.PASSPORT_IOJ);
            if (null != applicationTrackingOutputResult.getUsn()) {
                applicationTrackingDataService.sendTrackingOutputResult(applicationTrackingOutputResult);
            }
        } catch (Exception ex) {
            log.error("IoJ Appeal Post Processing failed, rolling back create IoJ Appeal", ex);
            Sentry.captureException(ex);

            try {
                iojAppealService.rollback(appealId, request);
            } catch (Exception exception) {
                log.error("Rollback also failed for appealId {}", appealId, exception);

                RollbackException rollbackException = exception instanceof RollbackException existingRollbackException
                        ? existingRollbackException
                        : new RollbackException(request.getApplicationDTO(), exception);

                rollbackException.addSuppressed(ex);
                throw rollbackException;
            }
            throw new MaatOrchestrationException(request.getApplicationDTO(), ex);
        }

        return request.getApplicationDTO();
    }

    /**
     * Utility function
     * Adds field to missingFields
     * returns inverse of failureCondition
     *
     * @param fieldName
     * @param failureCondition
     * @param list
     * @return
     */
    static boolean checkValid(String fieldName, boolean failureCondition, List<String> list) {
        if (failureCondition) {
            list.add(fieldName);
        }
        return !failureCondition;
    }

    /**
     * Validate fields required to create Appeal.
     * Service will return status 400, through {@code CrimeValidationExceptionHandler}
     */
    private void validateRequiredCreateFields(WorkflowRequest request) {
        List<String> missingFields = new ArrayList<>();

        ApplicationDTO applicationDTO = request != null ? request.getApplicationDTO() : null;
        if (checkValid("applicationDTO", applicationDTO == null, missingFields)) {
            checkValid("applicationDTO.repId", applicationDTO.getRepId() == null, missingFields);
            checkValid("applicationDTO.dateReceived", applicationDTO.getDateReceived() == null, missingFields);
            if (checkValid("applicationDTO.assessmentDTO", applicationDTO.getAssessmentDTO() == null, missingFields)) {
                if (checkValid(
                        "applicationDTO.assessmentDTO.iojAppeal",
                        applicationDTO.getAssessmentDTO().getIojAppeal() == null,
                        missingFields)) {
                    IOJAppealDTO iojAppeal = applicationDTO.getAssessmentDTO().getIojAppeal();
                    checkValid(
                            "applicationDTO.assessmentDTO.iojAppeal.cmuId",
                            iojAppeal.getCmuId() == null,
                            missingFields);
                    checkValid(
                            "applicationDTO.assessmentDTO.iojAppeal.receivedDate",
                            iojAppeal.getReceivedDate() == null,
                            missingFields);
                    if (checkValid(
                            "applicationDTO.assessmentDTO.iojAppeal.newWorkReasonDTO",
                            iojAppeal.getNewWorkReasonDTO() == null,
                            missingFields)) {
                        checkValid(
                                "applicationDTO.assessmentDTO.iojAppeal.newWorkReasonDTO.code",
                                iojAppeal.getNewWorkReasonDTO().getCode() == null,
                                missingFields);
                    }
                    if (checkValid(
                            "applicationDTO.assessmentDTO.iojAppeal.appealReason",
                            iojAppeal.getAppealReason() == null,
                            missingFields)) {
                        checkValid(
                                "applicationDTO.assessmentDTO.iojAppeal.appealReason.code",
                                iojAppeal.getAppealReason().getCode() == null,
                                missingFields);
                    }
                }
            }
        }

        if (!missingFields.isEmpty()) {
            throw new ValidationException("CreateIoJAppeal request is missing required fields: " + missingFields);
        }
    }
}
