package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {
    @Mock
    private RepOrderService repOrderService;

    @InjectMocks
    private ApplicationService applicationService;

    @Test
    void givenValidRequest_whenUpdateDateModifiedIsInvoked_thenDateModifiedIsUpdated() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ZonedDateTime initialDateModified = LocalDateTime.MIN.atZone(ZoneOffset.UTC);

        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        applicationDTO.setTimestamp(initialDateModified);

        applicationService.updateDateModified(workflowRequest, workflowRequest.getApplicationDTO());

        assertThat(applicationDTO.getTimestamp()).isNotEqualTo(initialDateModified);
    }
}
