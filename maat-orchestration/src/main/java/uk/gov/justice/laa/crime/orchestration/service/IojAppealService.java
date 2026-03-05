package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiRollbackIojAppealResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJAppealDTO;
import uk.gov.justice.laa.crime.orchestration.exception.RollbackException;
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
        ApiGetIojAppealResponse response = assessmentApiService.findIojAppeal(appealId);

        return iojAppealMapper.apiGetIojAppealResponseToIojAppealDTO(response);
    }

    public String create(WorkflowRequest request) {
        ApplicationDTO applicationDTO = request.getApplicationDTO();
        IOJAppealDTO iojAppealDto = applicationDTO.getAssessmentDTO().getIojAppeal();

        ApiCreateIojAppealRequest iojAppealRequest =
                iojAppealMapper.mapIojAppealDtoToApiCreateIojAppealRequest(request);
        ApiCreateIojAppealResponse iojAppealResponse = assessmentApiService.createIojAppeal(iojAppealRequest);

        iojAppealDto.setIojId(iojAppealResponse.getLegacyAppealId().longValue());

        return iojAppealResponse.getAppealId();
    }

    public String rollback(String appealId, WorkflowRequest request) {
        request.getApplicationDTO().getAssessmentDTO().getIojAppeal().setIojId(null);
        ApiRollbackIojAppealResponse apiRollbackIojAppealResponse = assessmentApiService.rollback(appealId);
        if (Boolean.TRUE.equals(apiRollbackIojAppealResponse.getRollbackSuccessful())) {
            log.info("IoJ Appeal rolled back successfully for Appeal Id: {}", appealId);
            return apiRollbackIojAppealResponse.getAppealId();
        } else {
            log.error("Unable to rollback IoJ Appeal for Appeal Id: {}", appealId);
            throw new RollbackException(request.getApplicationDTO());
        }
    }
}
