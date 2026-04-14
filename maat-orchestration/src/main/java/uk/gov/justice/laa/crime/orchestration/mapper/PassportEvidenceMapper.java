package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.evidence.ApiPassportEvidenceMetadata;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.EvidenceTypeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ExtraEvidenceDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IncomeEvidenceSummaryDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PassportEvidenceMapper {

    private EvidenceTypeDTO incomeEvidenceTypeToEvidenceTypeDTO(IncomeEvidenceType evidenceType) {
        return EvidenceTypeDTO.builder()
                .evidence(evidenceType.getName())
                .description(evidenceType.getDescription())
                .build();
    }

    private EvidenceDTO apiIncomeEvidenceToEvidenceDTO(ApiIncomeEvidence evidenceItem) {
        return EvidenceDTO.builder()
                .id(Long.valueOf(evidenceItem.getId()))
                .evidenceTypeDTO(incomeEvidenceTypeToEvidenceTypeDTO(evidenceItem.getEvidenceType()))
                .otherDescription(evidenceItem.getDescription())
                .dateReceived(
                        evidenceItem.getDateReceived() != null ? DateUtil.asDate(evidenceItem.getDateReceived()) : null)
                .build();
    }

    private ExtraEvidenceDTO apiIncomeEvidenceToExtraEvidenceDTO(ApiIncomeEvidence evidenceItem, boolean isPartner) {
        return ExtraEvidenceDTO.builder()
                .otherText(evidenceItem.getDescription())
                .mandatory(evidenceItem.getMandatory())
                .adhoc(isPartner ? "P" : "A")
                .build();
    }

    public IncomeEvidenceSummaryDTO apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(
            ApiGetPassportEvidenceResponse evidence) {
        ApiPassportEvidenceMetadata metadata = evidence.getPassportEvidenceMetadata();

        Map<Boolean, List<ApiIncomeEvidence>> applicantEvidenceItems = evidence.getApplicantEvidenceItems().stream()
                .collect(Collectors.partitioningBy(
                        evidenceItem -> evidenceItem.getEvidenceType().isExtra()));
        Map<Boolean, List<ApiIncomeEvidence>> partnerEvidenceItems = evidence.getPartnerEvidenceItems().stream()
                .collect(Collectors.partitioningBy(
                        evidenceItem -> evidenceItem.getEvidenceType().isExtra()));
        Collection<ExtraEvidenceDTO> extraEvidenceItems = Stream.concat(
                        applicantEvidenceItems.get(true).stream()
                                .map(evidenceItem -> apiIncomeEvidenceToExtraEvidenceDTO(evidenceItem, false)),
                        partnerEvidenceItems.get(true).stream()
                                .map(evidenceItem -> apiIncomeEvidenceToExtraEvidenceDTO(evidenceItem, true)))
                .toList();

        return IncomeEvidenceSummaryDTO.builder()
                .evidenceDueDate(
                        metadata.getEvidenceDueDate() != null ? DateUtil.asDate(metadata.getEvidenceDueDate()) : null)
                .evidenceReceivedDate(
                        metadata.getEvidenceReceivedDate() != null
                                ? DateUtil.asDate(metadata.getEvidenceReceivedDate())
                                : null)
                .upliftAppliedDate(
                        metadata.getUpliftAppliedDate() != null
                                ? DateUtil.asDate(metadata.getUpliftAppliedDate())
                                : null)
                .upliftRemovedDate(
                        metadata.getUpliftRemovedDate() != null
                                ? DateUtil.asDate(metadata.getUpliftRemovedDate())
                                : null)
                .firstReminderDate(
                        metadata.getFirstReminderDate() != null
                                ? DateUtil.asDate(metadata.getFirstReminderDate())
                                : null)
                .secondReminderDate(
                        metadata.getSecondReminderDate() != null
                                ? DateUtil.asDate(metadata.getSecondReminderDate())
                                : null)
                .incomeEvidenceNotes(metadata.getIncomeEvidenceNotes())
                .applicantIncomeEvidenceList(applicantEvidenceItems.get(false).stream()
                        .map(this::apiIncomeEvidenceToEvidenceDTO)
                        .toList())
                .partnerIncomeEvidenceList(partnerEvidenceItems.get(false).stream()
                        .map(this::apiIncomeEvidenceToEvidenceDTO)
                        .toList())
                .extraEvidenceList(extraEvidenceItems)
                .build();
    }
}
