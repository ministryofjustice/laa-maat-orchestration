package uk.gov.justice.laa.crime.orchestration.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

@ExtendWith(MockitoExtension.class)
class RepOrderServiceTest {
    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private RepOrderService repOrderService;

    @Test
    void givenUnknownRepOrder_whenGetRepOrderIsInvoked_thenNoRepOrderIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        when(maatCourtDataService.findRepOrder(any())).thenReturn(null);

        RepOrderDTO actualRepOrder = repOrderService.getRepOrder(workflowRequest);

        assertThat(actualRepOrder).isNull();
    }

    @Test
    void givenExistingRepOrder_whenGetRepOrderIsInvoked_thenRepOrderIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        int repOrderId = workflowRequest.getApplicationDTO().getRepId().intValue();
        RepOrderDTO expectedRepOrder = RepOrderDTO.builder()
            .id(repOrderId)
            .build();

        when(maatCourtDataService.findRepOrder(repOrderId)).thenReturn(expectedRepOrder);

        RepOrderDTO actualRepOrder = repOrderService.getRepOrder(workflowRequest);

        assertThat(actualRepOrder).isEqualTo(expectedRepOrder);
    }
}
