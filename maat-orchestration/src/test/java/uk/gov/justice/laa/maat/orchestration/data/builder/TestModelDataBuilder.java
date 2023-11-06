package uk.gov.justice.laa.maat.orchestration.data.builder;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.maat.orchestration.dto.GetHardshipDTO;

@Component
public class TestModelDataBuilder {
    public static final Integer HARDSHIP_ID = 1234;

    public static GetHardshipDTO getHardshipDTO() {
       return GetHardshipDTO.builder()
               .hardshipAssessmentId(HARDSHIP_ID)
               .build();
    }
}
