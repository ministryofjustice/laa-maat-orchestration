package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.orchestration.data.Constants.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

@ExtendWith(MockitoExtension.class)
class PassportAssessmentServiceTest {

    @Mock
    private AssessmentApiService assessmentApiService;
    @Mock
    private PassportAssessmentMapper passportAssessmentMapper;
    @InjectMocks
    private PassportAssessmentService passportAssessmentService;

    @Test
    void givenValidLegacyId_whenFindIsInvoked_thenPassportedDTOIsReturned() {
        ApiGetPassportedAssessmentResponse response = PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse();
        PassportedDTO dto = PassportAssessmentDataBuilder.getPassportedDTO();

        when(assessmentApiService.findPassportAssessment(PASSPORT_ASSESSMENT_ID))
            .thenReturn(response);
        when(passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response))
            .thenReturn(dto);

        assertThat(passportAssessmentService.find(PASSPORT_ASSESSMENT_ID)).isEqualTo(dto);
    }

}
