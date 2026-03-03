package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;

@ExtendWith(MockitoExtension.class)
class PassportAssessmentOrchestrationServiceTest {

    @Mock
    private PassportAssessmentService passportAssessmentService;

    @InjectMocks
    private PassportAssessmentOrchestrationService passportAssessmentOrchestrationService;

    @Test
    void givenValidLegacyId_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        PassportedDTO dto = PassportAssessmentDataBuilder.getPassportedDTO();

        when(passportAssessmentService.find(PASSPORT_ASSESSMENT_ID)).thenReturn(dto);

        assertThat(passportAssessmentOrchestrationService.find(PASSPORT_ASSESSMENT_ID))
            .isEqualTo(dto);
    }
}
