package uk.gov.justice.laa.crime.orchestration.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowRequest {
    private UserDTO userDTO;
    private ApplicationDTO applicationDTO;
}
