package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class RepOrderService {
    private final MaatCourtDataService maatCourtDataService;

    public RepOrderDTO getRepOrder(WorkflowRequest workflowRequest) {
        int repId = workflowRequest.getApplicationDTO().getRepId().intValue();
        return maatCourtDataService.findRepOrder(repId);
    }

    public RepOrderDTO updateRepOrderDateModified(WorkflowRequest workflowRequest, LocalDateTime dateModified) {
        int repId = workflowRequest.getApplicationDTO().getRepId().intValue();
        Map<String, Object> fieldsToUpdate = Map.of("dateModified", dateModified);

        maatCourtDataService.updateRepOrderDateModified(repId, fieldsToUpdate);

        return getRepOrder(workflowRequest);
    }
}
