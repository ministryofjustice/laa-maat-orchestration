package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.enums.HardshipReviewStatus;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.HardshipOverviewDTO;
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

    public void rollback(WorkflowRequest request) {
        HardshipReviewDTO hardshipReviewDTO = hardshipMapper.getHardshipReviewDTO(request.getApplicationDTO());
        hardshipApiService.rollback(hardshipReviewDTO.getId());
        hardshipReviewDTO.setReviewResult(null);
        hardshipReviewDTO.getAsessmentStatus().setStatus(HardshipReviewStatus.IN_PROGRESS.getStatus());
        hardshipReviewDTO.getAsessmentStatus().setDescription(HardshipReviewStatus.IN_PROGRESS.getDescription());
    }
}
