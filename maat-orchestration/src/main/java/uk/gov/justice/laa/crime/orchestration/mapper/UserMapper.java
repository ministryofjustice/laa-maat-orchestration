package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;

@Component
public class UserMapper {
    public ApiUserSession userDtoToUserSession(UserDTO user) {
        return new ApiUserSession()
                .withUserName(user.getUserName())
                .withSessionId(user.getUserSession());
    }
}
