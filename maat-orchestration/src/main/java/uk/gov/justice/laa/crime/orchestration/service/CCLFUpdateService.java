package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.commons.exception.APIClientException;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CurrentFeatureToggles;
import uk.gov.justice.laa.crime.orchestration.enums.FeatureToggleAction;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class CCLFUpdateService {

    private final MaatCourtDataApiService maatCourtDataApiService;
    private final FeatureDecisionService featureDecisionService;

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

    public void updateSendToCCLF(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        ApplicationDTO applicationDto = request.getApplicationDTO();
        if (applicationDto == null || applicationDto.getApplicantDTO() == null || repOrderDTO == null || repOrderDTO.getId() == null) {
            throw new ValidationException("Valid ApplicationDTO and RepOrderDTO is required");
        }

        if (featureDecisionService.isFeatureEnabled(request, CurrentFeatureToggles.MAAT_POST_ASSESSMENT_PROCESSING,
                FeatureToggleAction.UPDATE)) {
            if (!compareRepOrderAndApplicationDTO(repOrderDTO, applicationDto)) {
                SendToCCLFDTO sendToCCLFDTO = SendToCCLFDTO.builder().repId(repOrderDTO.getId())
                        .applId(applicationDto.getApplicantDTO().getId())
                        .applHistoryId(applicationDto.getApplicantDTO().getApplicantHistoryId()).build();
                maatCourtDataApiService.updateSendToCCLF(sendToCCLFDTO);
            }
        } else {
            log.error("MAAT_POST_ASSESSMENT_PROCESSING feature is disabled {}", request.getUserDTO().getUserName());
        }
    }

    public void updateSendToCCLF(WorkflowRequest request, Integer repId) {
        RepOrderDTO repOrderDTO = maatCourtDataApiService.getRepOrderByRepId(repId);
        updateSendToCCLF(request, repOrderDTO);
    }

    public Date parseDate(String dateString) {
        if (dateString == null) return null;
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        } catch (ParseException e) {
            throw new APIClientException("Invalid date format");
        }
    }

    public boolean compareRepOrderAndApplicationDTO(RepOrderDTO repOrderDTO, ApplicationDTO applicationDTO) {
        String acctNumber = applicationDTO.getSupplierDTO() != null ? applicationDTO.getSupplierDTO().getAccountNumber() : null;
        String feeLevel = getFeeLevel(applicationDTO);
        LocalDate ccRepOrderDate = getRepOrderDate(applicationDTO);
        LocalDate ccWithdrawalDate = getWithDrawalDate(applicationDTO);
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
                && isEquals(repOrderDTO.getCrownWithdrawalDate(), ccWithdrawalDate)
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

    public LocalDate convertToLocalDateViaSqlDate(Date dateToConvert) {
        if (dateToConvert == null) return null;
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
}
