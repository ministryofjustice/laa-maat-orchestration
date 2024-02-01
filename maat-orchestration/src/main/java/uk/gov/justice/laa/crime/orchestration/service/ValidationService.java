package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepStatusDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {
    public static final String CANNOT_MODIFY_APPLICATION_ERROR =
            "Application has been modified by another user";
    public static final String CANNOT_UPDATE_APPLICATION_STATUS =
            "Cannot update case in status of %s";

    public void validate(WorkflowRequest request, RepOrderDTO repOrderDTO) {
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
        boolean isUpdateAllowedOnRepOrder = null == rorsStatus || RepOrderStatus.getFrom(rorsStatus).isUpdateAllowed();
        if (!isUpdateAllowedOnRepOrder && !isUpdateAllowedOnApplication) {
            throw new ValidationException(String.format(CANNOT_UPDATE_APPLICATION_STATUS, statusDTO.getDescription()));
        }
    }
}
