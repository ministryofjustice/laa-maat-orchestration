package uk.gov.justice.laa.crime.orchestration.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest {
    @NotNull
    private UserDTO userDTO;

    @NotNull
    private ApplicationDTO applicationDTO;

    private CourtType courtType;
}
