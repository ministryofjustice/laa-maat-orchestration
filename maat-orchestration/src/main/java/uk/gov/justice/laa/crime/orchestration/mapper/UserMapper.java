package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;

@Component
public class UserMapper {
    public ApiUserSession userDtoToUserSession(UserDTO user) {
        return new ApiUserSession()
                .withUserName(user.getUserName())
                .withSessionId(user.getUserSession());
    }

    public UserActionDTO getUserActionDTO(
        WorkflowRequest request,
        Action action,
        NewWorkReason newWorkReason) {
        UserDTO userDTO = request.getUserDTO();

        return UserActionDTO.builder()
            .username(userDTO.getUserName())
            .sessionId(userDTO.getUserSession())
            .newWorkReason(newWorkReason)
            .action(action)
            .build();
    }
}
