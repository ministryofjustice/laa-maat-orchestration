package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @Mock
    private RepOrderService repOrderService;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void givenValidRequest_whenUpdateDateModifiedIsInvoked_thenDateModifiedIsUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.setTimestamp(LocalDateTime.MIN.atZone(ZoneOffset.UTC));

        ZonedDateTime updatedDateModified = LocalDateTime.MIN.atZone(ZoneOffset.UTC);
        when(repOrderService.updateRepOrderDateModified(eq(workflowRequest), any()))
                .thenReturn(RepOrderDTO.builder()
                        .dateModified(updatedDateModified.toLocalDateTime())
                        .build());

        applicationService.updateDateModified(workflowRequest, workflowRequest.getApplicationDTO());

        assertThat(applicationDTO.getTimestamp()).isEqualTo(updatedDateModified);
    }
}
