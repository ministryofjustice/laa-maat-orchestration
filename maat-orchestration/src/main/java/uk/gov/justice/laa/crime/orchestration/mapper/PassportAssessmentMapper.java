package uk.gov.justice.laa.crime.orchestration.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;

@Component
@RequiredArgsConstructor
public class PassportAssessmentMapper {

    public PassportedDTO apiGetPassportedAssessmentResponseToPassportedDTO(
        ApiGetPassportedAssessmentResponse response) {

    }

}
