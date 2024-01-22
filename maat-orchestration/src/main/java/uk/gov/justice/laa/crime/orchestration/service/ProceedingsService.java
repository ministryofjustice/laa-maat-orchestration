package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.mapper.ProceedingsMapper;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.orchestration.model.crown_court.ApiUpdateCrownCourtResponse;
import uk.gov.justice.laa.crime.orchestration.service.api.ProceedingsApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProceedingsService {

    private final ProceedingsMapper proceedingsMapper;
    private final ProceedingsApiService proceedingsApiService;

    public void updateApplication(WorkflowRequest request) {
        ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(request);
        ApiUpdateApplicationResponse updateApplicationResponse =
                proceedingsApiService.updateApplication(apiUpdateApplicationRequest);
        proceedingsMapper.updateApplicationResponseToApplicationDto(
                updateApplicationResponse, request.getApplicationDTO()
        );
    }

    public void updateCrownCourt(WorkflowRequest request) {
        ApiUpdateApplicationRequest apiUpdateApplicationRequest =
                proceedingsMapper.workflowRequestToUpdateApplicationRequest(request);
        ApiUpdateCrownCourtResponse updateCrownCourtResponse =
                proceedingsApiService.updateCrownCourt(apiUpdateApplicationRequest);
        proceedingsMapper.updateCrownCourtResponseToApplicationDto(
                updateCrownCourtResponse, request.getApplicationDTO()
        );
    }
}
