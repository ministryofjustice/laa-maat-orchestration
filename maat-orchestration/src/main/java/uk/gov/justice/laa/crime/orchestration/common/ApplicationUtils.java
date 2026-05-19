package uk.gov.justice.laa.crime.orchestration.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicantLinkDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

import java.util.Collection;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApplicationUtils {

    public static Integer getPartnerId(ApplicationDTO applicationDTO) {
        Collection<ApplicantLinkDTO> applicantLinks = applicationDTO.getApplicantLinks();

        if (null != applicantLinks && !applicantLinks.isEmpty()) {
            return applicantLinks.stream()
                    .filter(applicant -> applicant.getUnlinked() == null)
                    .map(applicant -> applicant.getPartnerDTO().getId().intValue())
                    .findFirst()
                    .orElse(null);
        }

        return null;
    }
}
