package uk.gov.justice.laa.maat.orchestration.dto;

import java.util.ArrayList;
import java.util.Collection;

public class CrownCourtOverviewDTO extends GenericDTO {
    private Boolean available;
    private CrownCourtSummaryDTO crownCourtSummaryDTO;
    private ContributionsDTO contribution;
    private Collection<ContributionSummaryDTO> contributionSummary;
    private ApplicantPaymentDetailsDTO applicantPaymentDetailsDTO;
    private Collection<CorrespondenceDTO> correspondence;
    private AppealDTO appealDTO;

    public CrownCourtOverviewDTO() {
        reset();
    }

    public void reset() {
        this.crownCourtSummaryDTO = new CrownCourtSummaryDTO();
        this.applicantPaymentDetailsDTO = new ApplicantPaymentDetailsDTO();
        this.correspondence = new ArrayList<>();
        this.contributionSummary = new ArrayList<>();
        this.contribution = new ContributionsDTO();
        this.appealDTO = new AppealDTO();

    }

    @Override
    public Object getKey() {
        // There is no key for this dto
        return null;
    }

    public CrownCourtSummaryDTO getCrownCourtSummaryDTO() {
        return crownCourtSummaryDTO;
    }

    public void setCrownCourtSummaryDTO(CrownCourtSummaryDTO crownCourtSummaryDTO) {
        this.crownCourtSummaryDTO = crownCourtSummaryDTO;
    }

    public ApplicantPaymentDetailsDTO getApplicantPaymentDetailsDTO() {
        return applicantPaymentDetailsDTO;
    }

    public void setApplicantPaymentDetailsDTO(
            ApplicantPaymentDetailsDTO applicantPaymentDetailsDTO) {
        this.applicantPaymentDetailsDTO = applicantPaymentDetailsDTO;
    }

    public Collection<CorrespondenceDTO> getCorrespondence() {
        return correspondence;
    }

    public void setCorrespondence(Collection<CorrespondenceDTO> correspondence) {
        this.correspondence = correspondence;
    }

    public ContributionsDTO getContribution() {
        return contribution;
    }

    public void setContribution(ContributionsDTO contribution) {
        this.contribution = contribution;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public Collection<ContributionSummaryDTO> getContributionSummary() {
        return contributionSummary;
    }

    public void setContributionSummary(
            Collection<ContributionSummaryDTO> contributionSummary) {
        this.contributionSummary = contributionSummary;
    }

    public AppealDTO getAppealDTO() {
        return appealDTO;
    }

    public void setAppealDTO(AppealDTO appealDTO) {
        this.appealDTO = appealDTO;
    }
}
