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
public class JobSeekerDTO extends GenericDTO {
    @Builder.Default
    private Boolean isJobSeeker = false;

    private Date lastSignedOn;
}
