package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiUserSession;

@Component
public class UserMapper {
    protected ApiUserSession userDtoToUserSession(UserDTO user) {
        return new ApiUserSession()
                .withUserName(user.getUserName())
                .withSessionId(user.getUserSession());
    }
}
