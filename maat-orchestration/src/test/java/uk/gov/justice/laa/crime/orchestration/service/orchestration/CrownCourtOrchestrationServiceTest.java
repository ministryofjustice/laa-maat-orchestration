package uk.gov.justice.laa.crime.orchestration.service.orchestration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.orchestration.StoredProcedure;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.OutcomeDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.service.MaatCourtDataService;
import uk.gov.justice.laa.crime.orchestration.service.ProceedingsService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith({MockitoExtension.class})
class CrownCourtOrchestrationServiceTest {

    @Mock
    private ProceedingsService proceedingsService;

    @Mock
    private MaatCourtDataService maatCourtDataService;

    @InjectMocks
    private CrownCourtOrchestrationService orchestrationService;

    private static final String TRANSACTION_ID = "5fg0cay5-d28e-471a-babf-c3992c0h5582";

    void setupStubs(ApplicationDTO applicationDTO) {
        when(maatCourtDataService.invokeStoredProcedure(
                any(ApplicationDTO.class), any(UserDTO.class), any(StoredProcedure.class)
        )).thenReturn(applicationDTO);

        when(proceedingsService.updateCrownCourt(any(ApplicationDTO.class), any(UserDTO.class)))
                .thenReturn(applicationDTO);
    }

    void checkStoredProcedureInvocations(WorkflowRequest request) {
        ApplicationDTO applicationDTO = request.getApplicationDTO();

        verify(maatCourtDataService).invokeStoredProcedure(
                applicationDTO, request.getUserDTO(), StoredProcedure.UPDATE_DBMS_TRANSACTION_ID
        );

        verify(maatCourtDataService).invokeStoredProcedure(
                applicationDTO, request.getUserDTO(), StoredProcedure.PRE_UPDATE_CHECKS
        );

        verify(maatCourtDataService).invokeStoredProcedure(
                applicationDTO, request.getUserDTO(), StoredProcedure.UPDATE_CC_APPLICANT_AND_APPLICATION
        );
    }

    @Test
    void givenNoNewOutcomes_whenUpdateIsInvoked_thenTransactionIdIsReset() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = request.getApplicationDTO();

        setupStubs(applicationDTO);
        applicationDTO.setTransactionId(TRANSACTION_ID);

        orchestrationService.update(request);
        checkStoredProcedureInvocations(request);
        assertThat(applicationDTO.getTransactionId()).isNull();
    }

    @Test
    void givenNoNewOutcomes_whenUpdateIsInvoked_thenApplicationIsUpdatedAndCorrectStoredProceduresAreInvoked() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = request.getApplicationDTO();

        setupStubs(applicationDTO);
        orchestrationService.update(request);
        checkStoredProcedureInvocations(request);
    }

    @Test
    void givenNewOutcome_whenUpdateIsInvoked_thenApplicationIsUpdatedAndCorrectStoredProceduresAreInvoked() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = request.getApplicationDTO();

        List<OutcomeDTO> outcomes = List.of(OutcomeDTO.builder().dateSet(null).build());
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setOutcomeDTOs(outcomes);

        setupStubs(applicationDTO);
        orchestrationService.update(request);
        checkStoredProcedureInvocations(request);

        verify(maatCourtDataService).invokeStoredProcedure(
                applicationDTO, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
        );
    }

    @Test
    void givenNoCrownCourtSummary_whenUpdateIsInvoked_thenApplicationIsUpdatedAndCorrectStoredProceduresAreInvoked() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = request.getApplicationDTO();

        applicationDTO.getCrownCourtOverviewDTO().setCrownCourtSummaryDTO(null);

        setupStubs(applicationDTO);
        orchestrationService.update(request);
        checkStoredProcedureInvocations(request);

        verify(maatCourtDataService, never()).invokeStoredProcedure(
                applicationDTO, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
        );
    }

    @Test
    void givenNoOutcomes_whenUpdateIsInvoked_thenApplicationIsUpdatedAndCorrectStoredProceduresAreInvoked() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = request.getApplicationDTO();

        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setOutcomeDTOs(null);

        setupStubs(applicationDTO);
        orchestrationService.update(request);
        checkStoredProcedureInvocations(request);

        verify(maatCourtDataService, never()).invokeStoredProcedure(
                applicationDTO, request.getUserDTO(), StoredProcedure.PROCESS_ACTIVITY_AND_GET_CORRESPONDENCE
        );
    }

}
