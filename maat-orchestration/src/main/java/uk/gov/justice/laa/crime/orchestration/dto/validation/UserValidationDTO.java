package uk.gov.justice.laa.crime.orchestration.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.orchestration.enums.Action;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserValidationDTO {
    private String username;
    private Action action;
    private NewWorkReason newWorkReason;
    private String sessionId;
}
