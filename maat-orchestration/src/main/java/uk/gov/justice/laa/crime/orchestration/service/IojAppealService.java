package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IojAppealMapper;
import uk.gov.justice.laa.crime.orchestration.mapper.UserMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class IojAppealService {

    private final AssessmentApiService assessmentApiService;
    private final IojAppealMapper iojAppealMapper;
    private final UserMapper userMapper;

    public IOJAppealDTO find(int appealId) {
        ApiGetIojAppealResponse response = assessmentApiService.find(appealId);

        return iojAppealMapper.apiGetIojAppealResponseToIojAppealDTO(response);
    }

    public ApplicationDTO create(WorkflowRequest request) {
        ApplicationDTO applicationDTO = request.getApplicationDTO();
        IOJAppealDTO iojAppealDto = applicationDTO.getAssessmentDTO().getIojAppeal();

        ApiCreateIojAppealRequest iojAppealRequest =
                iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(request);
        ApiCreateIojAppealResponse iojAppealResponse = assessmentApiService.create(iojAppealRequest);

        iojAppealDto.setIojId(iojAppealResponse.getLegacyAppealId().longValue());

        return applicationDTO;
    }
}
