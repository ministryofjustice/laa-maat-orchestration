package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipReviewDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.HardshipMapper;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.orchestration.model.hardship.ApiPerformHardshipResponse;
import uk.gov.justice.laa.crime.orchestration.service.api.HardshipApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class HardshipService {

    private final HardshipMapper hardshipMapper;
    private final HardshipApiService hardshipApiService;

    public HardshipReviewDTO find(int hardshipReviewId) {
        ApiFindHardshipResponse hardship = hardshipApiService.find(hardshipReviewId);
        return hardshipMapper.findHardshipResponseToHardshipDto(hardship);
    }

    public ApiPerformHardshipResponse create(WorkflowRequest request) {
        ApiPerformHardshipRequest performHardshipRequest =
                hardshipMapper.workflowRequestToPerformHardshipRequest(request);
        return hardshipApiService.create(performHardshipRequest);
    }

    public void update(WorkflowRequest request) {
        ApiPerformHardshipRequest performHardshipRequest =
                hardshipMapper.workflowRequestToPerformHardshipRequest(request);
        ApiPerformHardshipResponse apiPerformHardshipResponse = hardshipApiService.update(performHardshipRequest);
        hardshipMapper.performHardshipResponseToApplicationDTO(apiPerformHardshipResponse, request.getApplicationDTO());
    }

    public ApiPerformHardshipResponse rollback(WorkflowRequest request) {
        ApiPerformHardshipRequest performHardshipRequest =
                hardshipMapper.workflowRequestToPerformHardshipRequest(request);
        return hardshipApiService.rollback(performHardshipRequest);
    }
}
