package uk.gov.justice.laa.crime.orchestration.common;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.util.NumberUtils;

import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PartnerResolver {

    public static Optional<Integer> getPartnerId(ApplicationDTO applicationDTO) {
        if (applicationDTO.getApplicantLinks() != null) {
            return applicationDTO.getApplicantLinks().stream()
                    .filter(link -> link.getUnlinked() == null)
                    .map(link -> NumberUtils.toInteger(link.getPartnerDTO().getId()))
                    .findFirst();
        }
        return Optional.empty();
    }
}
