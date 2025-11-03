package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final RepOrderService repOrderService;

    public void updateDateModified(WorkflowRequest request, ApplicationDTO applicationDTO) {
        RepOrderDTO updatedRepOrder = repOrderService.updateRepOrderDateModified(request, LocalDateTime.now());

        applicationDTO.setTimestamp(updatedRepOrder.getDateModified().atZone(ZoneOffset.UTC));
    }
}
