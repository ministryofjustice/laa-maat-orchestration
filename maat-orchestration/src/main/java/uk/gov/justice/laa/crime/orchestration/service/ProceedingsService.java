package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ProceedingsMapper;

import uk.gov.justice.laa.crime.orchestration.service.api.ProceedingsApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsService {

    private final ProceedingsMapper proceedingsMapper;
    private final ProceedingsApiService proceedingsApiService;

    public void updateApplication(WorkflowRequest request) {
        ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(request.getApplicationDTO(), request.getUserDTO());
        ApiUpdateApplicationResponse updateApplicationResponse =
                proceedingsApiService.updateApplication(apiUpdateApplicationRequest);
        proceedingsMapper.updateApplicationResponseToApplicationDto(
                updateApplicationResponse, request.getApplicationDTO()
        );
    }

    public ApplicationDTO updateCrownCourt(ApplicationDTO application, UserDTO userDTO) {
        ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(application, userDTO);
        ApiUpdateCrownCourtOutcomeResponse updateCrownCourtResponse =
                proceedingsApiService.updateCrownCourt(apiUpdateApplicationRequest);
        log.info("response--" + updateCrownCourtResponse);
        return proceedingsMapper.updateCrownCourtResponseToApplicationDto(
                updateCrownCourtResponse, application
        );
    }
}
