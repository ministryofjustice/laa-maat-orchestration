package uk.gov.justice.laa.crime.orchestration.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final RepOrderService repOrderService;

    public void updateDateModified(WorkflowRequest request, ApplicationDTO applicationDTO) {
        LocalDateTime updatedDateModified = LocalDateTime.now();

        repOrderService.updateRepOrderDateModified(request, updatedDateModified);
        applicationDTO.setTimestamp(updatedDateModified.atZone(ZoneOffset.UTC));
    }
}
