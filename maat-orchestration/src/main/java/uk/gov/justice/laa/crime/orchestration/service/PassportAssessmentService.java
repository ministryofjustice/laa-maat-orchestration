package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

@Service
@RequiredArgsConstructor
public class PassportAssessmentService {

    private final AssessmentApiService assessmentApiService;
    private final PassportAssessmentMapper passportAssessmentMapper;

    public PassportedDTO find(int assessmentId) {
        ApiGetPassportedAssessmentResponse response = assessmentApiService.findPassportAssessment(
            assessmentId);

        return passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response);
    }

}
