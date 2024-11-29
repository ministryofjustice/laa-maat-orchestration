package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepOrderService {
    private final MaatCourtDataService maatCourtDataService;

    public RepOrderDTO getRepOrder(WorkflowRequest workflowRequest) {
        int repId = workflowRequest.getApplicationDTO().getRepId().intValue();
        return maatCourtDataService.findRepOrder(repId);
    }
}
