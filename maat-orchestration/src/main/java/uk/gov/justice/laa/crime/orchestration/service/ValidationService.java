package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.enums.RepOrderStatus;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.RepStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserValidationDTO;
import uk.gov.justice.laa.crime.orchestration.exception.CrimeValidationException;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class ValidationService {
    public static final String CANNOT_MODIFY_APPLICATION_ERROR =
            "Application has been modified by another user";
    public static final String CANNOT_UPDATE_APPLICATION_STATUS =
            "Cannot update case in status of %s";
    public static final String USER_HAVE_AN_EXISTING_RESERVATION_RESERVATION_NOT_ALLOWED =
            "User have an existing reservation, so reservation not allowed";
    private static final String ACTION_NEW_WORK_REASON_AND_SESSION_DOES_NOT_EXIST =
            "Action, New work reason and Session does not exist";
    private static final String USER_DOES_NOT_HAVE_A_ROLE_CAPABLE_OF_PERFORMING_THIS_ACTION =
            "User does not have a role capable of performing this action";
    private static final String USER_DOES_NOT_HAVE_A_VALID_NEW_WORK_REASON_CODE =
            "User does not have a valid New Work Reason Code";
    private final MaatCourtDataService maatCourtDataService;
    private final MaatCourtDataApiService maatCourtDataApiService;

    public static String getRepOrderCcOutcome(RepOrderDTO repOrderDTO) {
        if (repOrderDTO.getRepOrderCCOutcome() == null || repOrderDTO.getRepOrderCCOutcome().isEmpty()) return null;
        return repOrderDTO.getRepOrderCCOutcome().get(0).getOutcome();
    }

    public static String getAppealType(ApplicationDTO applicationDTO) {
        String appealType = null;
        if (applicationDTO.getCrownCourtOverviewDTO() != null
                && applicationDTO.getCrownCourtOverviewDTO().getAppealDTO() != null
                && applicationDTO.getCrownCourtOverviewDTO().getAppealDTO().getAppealTypeDTO() != null)
            appealType = applicationDTO.getCrownCourtOverviewDTO().getAppealDTO().getAppealTypeDTO().getCode();
        return appealType;
    }

    public static String getOutcome(ApplicationDTO applicationDTO) {
        String outcome = null;
        if ((applicationDTO.getCrownCourtOverviewDTO() != null && applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO() != null)
                && (applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getCcOutcome() != null)) {
                outcome = applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getCcOutcome().getOutcome();
            }
        return outcome;
    }

    public static String getFeeLevel(ApplicationDTO applicationDTO) {
        String feeLevel = null;
        if (applicationDTO.getCrownCourtOverviewDTO() != null
                && applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO() != null
                && applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getEvidenceProvisionFee() != null) {
            feeLevel = applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getEvidenceProvisionFee().getFeeLevel();
        }
        return feeLevel;
    }

    private static boolean isEquals(Object s1, Object s2) {
        return Objects.equals(s1, s2);
    }

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

    public void updateSendToCCLF(WorkflowRequest request, RepOrderDTO repOrderDTO, String action) {
        ApplicationDTO applicationDto = request.getApplicationDTO();
        if (applicationDto == null || applicationDto.getApplicantDTO() == null || repOrderDTO == null || repOrderDTO.getId() == null) {
            throw new ValidationException("Valid ApplicationDTO and RepOrderDTO is required");
        }

        String _action = (action != null) ? action : "UPDATE";
        Date cclfDate = parseDate("2021-04-01");
        Date decisionDate = applicationDto.getDecisionDate();

        if ((decisionDate.after(cclfDate) || decisionDate.equals(cclfDate)) &&
                (_action.equals("CREATE") || !compareRepOrderAndApplicationDTO(repOrderDTO, applicationDto))) {
            SendToCCLFDTO sendToCCLFDTO = SendToCCLFDTO.builder().repId(repOrderDTO.getId())
                    .applId(applicationDto.getApplicantDTO().getId())
                    .applHistoryId(applicationDto.getApplicantDTO().getApplicantHistoryId()).build();
            maatCourtDataApiService.updateSendToCCLF(sendToCCLFDTO);
        }
    }

    public boolean compareRepOrderAndApplicationDTO(RepOrderDTO repOrderDTO, ApplicationDTO applicationDTO) {
        String acctNumber = applicationDTO.getSupplierDTO() != null ? applicationDTO.getSupplierDTO().getAccountNumber() : null;
        String feeLevel = getFeeLevel(applicationDTO);
        LocalDate ccRepOrderDate = getRepOrderDate(applicationDTO);
        LocalDate ccWithDrawalDate = getWithDrawalDate(applicationDTO);
        String outcome = getOutcome(applicationDTO);
        String court = applicationDTO.getMagsCourtDTO() != null ? applicationDTO.getMagsCourtDTO().getCourt() : null;
        String magsOutcome = applicationDTO.getMagsOutcomeDTO() != null ? applicationDTO.getMagsOutcomeDTO().getOutcome() : null;
        String offenceType = applicationDTO.getOffenceDTO() != null ? applicationDTO.getOffenceDTO().getOffenceType() : null;
        Long applicantHistoryId = (applicationDTO.getApplicantDTO() != null
                && applicationDTO.getApplicantDTO().getApplicantHistoryId() != null) ? applicationDTO.getApplicantDTO().getApplicantHistoryId() : -1;
        String status = applicationDTO.getStatusDTO() != null ? applicationDTO.getStatusDTO().getStatus() : null;
        String appealType = getAppealType(applicationDTO);
        LocalDate lDateReceived = convertToLocalDateViaSqlDate(applicationDTO.getDateReceived());
        LocalDate lCommittalDate = convertToLocalDateViaSqlDate(applicationDTO.getCommittalDate());

        Long rApplicantHistoryId = repOrderDTO.getApplicantHistoryId() != null ? Long.valueOf(repOrderDTO.getApplicantHistoryId()) : -1;
        String rOutcome = getRepOrderCcOutcome(repOrderDTO);

        return isEquals(repOrderDTO.getArrestSummonsNo(), applicationDTO.getArrestSummonsNo())
                && isEquals(repOrderDTO.getSuppAccountCode(), acctNumber)
                && isEquals(repOrderDTO.getEvidenceFeeLevel(), feeLevel)
                && isEquals(repOrderDTO.getMacoCourt(), court)
                && isEquals(repOrderDTO.getMagsOutcome(), magsOutcome)
                && isEquals(repOrderDTO.getDateReceived(), lDateReceived)
                && isEquals(repOrderDTO.getCrownRepOrderDate(), ccRepOrderDate)
                && isEquals(repOrderDTO.getOftyOffenceType(), offenceType)
                && isEquals(repOrderDTO.getCrownWithdrawalDate(), ccWithDrawalDate)
                && isEquals(repOrderDTO.getCaseId(), applicationDTO.getCaseId())
                && isEquals(repOrderDTO.getCommittalDate(), lCommittalDate)
                && isEquals(rApplicantHistoryId, applicantHistoryId)
                && isEquals(repOrderDTO.getRorsStatus(), status)
                && isEquals(rOutcome, outcome)
                && isEquals(repOrderDTO.getAppealTypeCode(), appealType);
    }

    public LocalDate getWithDrawalDate(ApplicationDTO applicationDTO) {
        LocalDate ccWithDrawalDate = null;
        if ((applicationDTO.getCrownCourtOverviewDTO() != null
                && applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO() != null)
            && (applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getCcWithDrawalDate() != null)) {
                ccWithDrawalDate = convertToLocalDateViaSqlDate(applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getCcWithDrawalDate());
            }
        return ccWithDrawalDate;
    }

    public LocalDate getRepOrderDate(ApplicationDTO applicationDTO) {
        LocalDate ccRepOrderDate = null;
        if ((applicationDTO.getCrownCourtOverviewDTO() != null
                && applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO() != null)
            && (applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getCcRepOrderDate() != null)) {
                ccRepOrderDate = convertToLocalDateViaSqlDate(applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getCcRepOrderDate());
            }
        return ccRepOrderDate;
    }

    public Date parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            throw new APIClientException("Invalid date format");
        }
    }


    public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        if (dateToConvert == null) return null;
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }


}




