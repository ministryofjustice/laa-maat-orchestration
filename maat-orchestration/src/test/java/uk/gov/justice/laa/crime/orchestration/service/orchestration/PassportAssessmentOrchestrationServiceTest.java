package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.service.PassportAssessmentService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PassportAssessmentOrchestrationServiceTest {

    @Mock
    private PassportAssessmentService passportAssessmentService;

    @InjectMocks
    private PassportAssessmentOrchestrationService passportAssessmentOrchestrationService;

    @Test
    void givenValidId_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        PassportedDTO dto = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);

        when(passportAssessmentService.find(Constants.PASSPORT_ASSESSMENT_ID)).thenReturn(dto);

        assertThat(passportAssessmentOrchestrationService.find(Constants.PASSPORT_ASSESSMENT_ID))
                .isEqualTo(dto);
    }
}
