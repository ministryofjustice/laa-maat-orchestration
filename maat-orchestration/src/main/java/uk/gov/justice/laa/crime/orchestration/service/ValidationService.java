package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserValidationDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {
    public static final String CANNOT_MODIFY_APPLICATION_ERROR =
            "Application has been modified by another user";
    public static final String CANNOT_UPDATE_APPLICATION_STATUS =
            "Cannot update case in status of %s";
    private static final String ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST =
            "Action, New work reason and Session does not exist";
    private static final String USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION =
            "User does not have a role capable of performing this action";
    private static final String USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE =
            "User does not have a valid New Work Reason Code";
    public static final String USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED =
            "User have an existing reservation, so reservation not allowed";

    private final MaatCourtDataService maatCourtDataService;

    public void validate(WorkflowRequest request) {
        int repId = request.getApplicationDTO().getRepId().intValue();
        RepOrderDTO repOrderDTO = maatCourtDataService.findRepOrder(repId);
        validateApplicationTimestamp(request, repOrderDTO);
        validateApplicationStatus(request, repOrderDTO);
    }

    private void validateApplicationTimestamp(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        LocalDateTime repOrderCreatedDate = DateUtil.convertDateToDateTime(repOrderDTO.getDateCreated());
        LocalDateTime repOrderUpdatedDate = repOrderDTO.getDateModified();
        LocalDateTime repOrderTimestamp = (null != repOrderUpdatedDate) ? repOrderUpdatedDate : repOrderCreatedDate;
        LocalDateTime applicationTimestamp = request.getApplicationDTO().getTimestamp().toLocalDateTime();
        if (!applicationTimestamp.isEqual(repOrderTimestamp)) {
            throw new ValidationException(CANNOT_MODIFY_APPLICATION_ERROR);
        }
    }

    private void validateApplicationStatus(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        RepStatusDTO statusDTO = request.getApplicationDTO().getStatusDTO();
        boolean isUpdateAllowedOnApplication = statusDTO.getUpdateAllowed();
        String rorsStatus = repOrderDTO.getRorsStatus();
        boolean isUpdateAllowedOnRepOrder = (null == rorsStatus) || RepOrderStatus.getFrom(rorsStatus).isUpdateAllowed();
        if (!isUpdateAllowedOnRepOrder && !isUpdateAllowedOnApplication) {
            throw new ValidationException(String.format(CANNOT_UPDATE_APPLICATION_STATUS, statusDTO.getDescription()));
        }
    }

    public Boolean isUserActionValid(UserValidationDTO request) {
        List<String> crimeValidationExceptionList = new ArrayList<>();

        if (request.getAction() == null && request.getNewWorkReason() == null && request.getSessionId() == null) {
            throw new IllegalArgumentException(ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST);
        }
        UserSummaryDTO userSummaryDTO = maatCourtDataService.getUserSummary(request.getUsername());

        if (request.getAction() != null && (userSummaryDTO.getRoleActions() == null
                || !userSummaryDTO.getRoleActions().contains(request.getAction().getCode()))) {
            crimeValidationExceptionList.add(USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION);
        }

        if (request.getNewWorkReason() != null && (userSummaryDTO.getNewWorkReasons() == null
                || !userSummaryDTO.getNewWorkReasons().contains(request.getNewWorkReason().getCode()))) {
            crimeValidationExceptionList.add(USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE);
        }

        if (request.getSessionId() != null && (userSummaryDTO.getReservationsEntity() != null
                && userSummaryDTO.getReservationsEntity().getUserSession().equalsIgnoreCase(request.getSessionId()))) {
            crimeValidationExceptionList.add(USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED);
        }

        if (!crimeValidationExceptionList.isEmpty()) {
            throw new CrimeValidationException(crimeValidationExceptionList);
        }

        return true;
    }

}
