package uk.gov.justice.laa.crime.orchestration.dto.maat;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class PassportedDTO extends GenericDTO {
    private static final long serialVersionUID = 1L;

    private Long passportedId;
    private Long cmuId;
    private Long usn;

    private Date date;

    @Builder.Default
    private AssessmentStatusDTO assessementStatusDTO = new AssessmentStatusDTO();
    @Builder.Default
    private PassportConfirmationDTO passportConfirmationDTO = new PassportConfirmationDTO();
    @Builder.Default
    private NewWorkReasonDTO newWorkReason = new NewWorkReasonDTO();
    @Builder.Default
    private ReviewTypeDTO reviewType = new ReviewTypeDTO();

    private String dwpResult;

    @Builder.Default
    private Boolean benefitIncomeSupport = false;
    @Builder.Default
    private JobSeekerDTO benefitJobSeeker = new JobSeekerDTO();
    @Builder.Default
    private Boolean benefitGaurenteedStatePension = false;
    @Builder.Default
    private Boolean benefitClaimedByPartner = false;
    @Builder.Default
    private Boolean benefitEmploymentSupport = false;
    @Builder.Default
    private Boolean benefitUniversalCredit = false;
    @Builder.Default
    private PartnerDTO partnerDetails = new PartnerDTO();
    @Builder.Default
    private String notes = "";
    private String result;
    @Builder.Default
    private Boolean under18HeardYouthCourt = false;
    @Builder.Default
    private Boolean under18HeardMagsCourt = false;
    @Builder.Default
    private Boolean under18FullEducation = false;
    private Boolean under16;
    private Boolean between1617;

    @Builder.Default
    private IncomeEvidenceSummaryDTO passportSummaryEvidenceDTO = new IncomeEvidenceSummaryDTO();

    private String whoDwpChecked;

}