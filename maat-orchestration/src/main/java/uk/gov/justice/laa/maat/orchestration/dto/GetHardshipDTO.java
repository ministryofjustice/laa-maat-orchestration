package uk.gov.justice.laa.maat.orchestration.dto;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class GetHardshipDTO {

    private UserDTO userDTO;
    private Integer hardshipAssessmentId;

}
