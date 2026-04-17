package uk.gov.justice.laa.crime.orchestration.data.builder;

import static uk.gov.justice.laa.crime.util.DateUtil.parseLocalDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toDate;
import static uk.gov.justice.laa.crime.util.DateUtil.toZonedDateTime;

import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiPassportEvidenceMetadata;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceTypeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ExtraEvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IncomeEvidenceSummaryDTO;

import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class EvidenceDataBuilder {

    private static ApiPassportEvidenceMetadata getApiPassportEvidenceMetadata() {
        return new ApiPassportEvidenceMetadata()
                .withEvidenceDueDate(parseLocalDate(Constants.INCOME_EVIDENCE_DUE_DATE))
                .withEvidenceReceivedDate(parseLocalDate(Constants.INCOME_EVIDENCE_RECEIVED_DATE))
                .withUpliftAppliedDate(parseLocalDate(Constants.INCOME_UPLIFT_APPLY_DATE))
                .withUpliftRemovedDate(parseLocalDate(Constants.INCOME_UPLIFT_REMOVE_DATE))
                .withFirstReminderDate(parseLocalDate(Constants.FIRST_REMINDER_DATE))
                .withSecondReminderDate(parseLocalDate(Constants.SECOND_REMINDER_DATE))
                .withIncomeEvidenceNotes(Constants.NOTES);
    }

    private static EvidenceTypeDTO getEvidenceTypeDTO() {
        return EvidenceTypeDTO.builder()
                .evidence(Constants.INCOME_EVIDENCE)
                .description(Constants.INCOME_EVIDENCE_DESCRIPTION)
                .build();
    }

    private static EvidenceTypeDTO getExtraEvidenceTypeDTO() {
        return EvidenceTypeDTO.builder()
                .evidence(Constants.EXTRA_INCOME_EVIDENCE)
                .description(Constants.EXTRA_INCOME_EVIDENCE_DESCRIPTION)
                .build();
    }

    private static ExtraEvidenceDTO getExtraIncomeEvidenceDTO(boolean isPartner) {
        return ExtraEvidenceDTO.builder()
                .adhoc(isPartner ? "P" : "A")
                .id(Constants.EXTRA_EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getExtraEvidenceTypeDTO())
                .dateReceived(toDate(Constants.EXTRA_INCOME_EVIDENCE_RECEIVED_DATE))
                .otherText(Constants.OTHER_DESCRIPTION)
                .mandatory(true)
                .timestamp(toZonedDateTime(Constants.DATE_MODIFIED))
                .build();
    }

    private static ApiIncomeEvidence getApiIncomeEvidence(boolean isPartner) {
        return new ApiIncomeEvidence()
                .withId(isPartner ? Constants.PARTNER_EVIDENCE_ID : Constants.APPLICANT_EVIDENCE_ID)
                .withDateReceived(parseLocalDate(Constants.INCOME_EVIDENCE_RECEIVED_DATE))
                .withEvidenceType(IncomeEvidenceType.TAX_RETURN)
                .withMandatory(true);
    }

    private static ApiIncomeEvidence getExtraApiIncomeEvidence() {
        return new ApiIncomeEvidence()
                .withId(Constants.EXTRA_EVIDENCE_ID)
                .withDateReceived(parseLocalDate(Constants.INCOME_EVIDENCE_RECEIVED_DATE))
                .withEvidenceType(IncomeEvidenceType.OTHER_ADHOC)
                .withDescription(Constants.OTHER_DESCRIPTION)
                .withMandatory(true);
    }

    public static ApiGetPassportEvidenceResponse getApiGetPassportEvidenceResponse() {
        return new ApiGetPassportEvidenceResponse()
                .withPassportEvidenceMetadata(getApiPassportEvidenceMetadata())
                .withApplicantEvidenceItems(List.of(getApiIncomeEvidence(false), getExtraApiIncomeEvidence()))
                .withPartnerEvidenceItems(List.of(getApiIncomeEvidence(true)));
    }

    public static EvidenceDTO getApplicantEvidenceDTO() {
        return EvidenceDTO.builder()
                .id(Constants.APPLICANT_EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(toDate(Constants.INCOME_EVIDENCE_RECEIVED_DATE))
                .timestamp(toZonedDateTime(Constants.DATE_MODIFIED))
                .build();
    }

    public static EvidenceDTO getPartnerEvidenceDTO() {
        return EvidenceDTO.builder()
                .id(Constants.PARTNER_EVIDENCE_ID.longValue())
                .evidenceTypeDTO(getEvidenceTypeDTO())
                .dateReceived(toDate(Constants.INCOME_EVIDENCE_RECEIVED_DATE))
                .timestamp(toZonedDateTime(Constants.DATE_MODIFIED))
                .build();
    }

    public static IncomeEvidenceSummaryDTO getIncomeEvidenceSummaryDTO() {
        return IncomeEvidenceSummaryDTO.builder()
                .upliftAppliedDate(toDate(Constants.INCOME_UPLIFT_APPLY_DATE))
                .upliftRemovedDate(toDate(Constants.INCOME_UPLIFT_REMOVE_DATE))
                .incomeEvidenceNotes(Constants.NOTES)
                .applicantIncomeEvidenceList(List.of(getApplicantEvidenceDTO()))
                .partnerIncomeEvidenceList(List.of(getPartnerEvidenceDTO()))
                .extraEvidenceList(List.of(getExtraIncomeEvidenceDTO(false)))
                .evidenceReceivedDate(toDate(Constants.INCOME_EVIDENCE_RECEIVED_DATE))
                .evidenceDueDate(toDate(Constants.INCOME_EVIDENCE_DUE_DATE))
                .firstReminderDate(toDate(Constants.FIRST_REMINDER_DATE))
                .secondReminderDate(toDate(Constants.SECOND_REMINDER_DATE))
                .enabled(Boolean.FALSE)
                .build();
    }
}
