package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.ProceedingsMapper;

import uk.gov.justice.laa.crime.orchestration.service.api.ProceedingsApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class ProceedingsServiceTest {

    @Mock
    private ProceedingsMapper proceedingsMapper;

    @Mock
    private ProceedingsApiService proceedingsApiService;

    @InjectMocks
    private ProceedingsService proceedingsService;

    @Mock
    private FeatureDecisionService featureDecisionService;

    @Mock
    private CCLFUpdateService cclfUpdateService;

    @Test
    void givenWorkflowRequest_whenUpdateApplicationIsInvoked_thenApiServiceIsCalledAndApplicationUpdated() {
        when(proceedingsMapper.workflowRequestToUpdateApplicationRequest(any(ApplicationDTO.class), any(UserDTO.class)))
                .thenReturn(new ApiUpdateApplicationRequest());

        when(proceedingsApiService.updateApplication(any(ApiUpdateApplicationRequest.class)))
                .thenReturn(new ApiUpdateApplicationResponse());
        proceedingsService.updateApplication(WorkflowRequest.builder().applicationDTO(new ApplicationDTO()).userDTO(new UserDTO()).build(),
                new RepOrderDTO());

        verify(proceedingsMapper)
                .updateApplicationResponseToApplicationDto(any(ApiUpdateApplicationResponse.class),
                                                           any(ApplicationDTO.class)
                );
    }

    @Test
    void givenWorkflowRequest_whenUpdateCrownCourtIsInvoked_thenApiServiceIsCalledAndApplicationUpdated() {
        when(proceedingsMapper.workflowRequestToUpdateCrownCourtRequest(any(ApplicationDTO.class), any()))
                .thenReturn(new ApiUpdateCrownCourtRequest());

        when(proceedingsApiService.updateCrownCourt(any(ApiUpdateCrownCourtRequest.class)))
                .thenReturn(new ApiUpdateCrownCourtOutcomeResponse());

        proceedingsService.updateCrownCourt(WorkflowRequest.builder().applicationDTO(new ApplicationDTO()).userDTO(new UserDTO()).build(),
                new RepOrderDTO());

        verify(proceedingsMapper)
                .updateCrownCourtResponseToApplicationDto(any(ApiUpdateCrownCourtOutcomeResponse.class),
                                                          any(ApplicationDTO.class)
                );
    }
}
