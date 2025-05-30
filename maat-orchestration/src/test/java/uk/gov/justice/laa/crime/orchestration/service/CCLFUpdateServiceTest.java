package uk.gov.justice.laa.crime.orchestration.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.enums.AppealType;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.exception.ValidationException;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.*;

@ExtendWith(MockitoExtension.class)
class CCLFUpdateServiceTest {
    private static final LocalDateTime CC_WITHDRAWAL_DATETIME = LocalDateTime.of(2022, 10, 14, 0, 0, 0);
    private static final LocalDateTime CC_REP_ORDER_DATETIME = LocalDateTime.of(2022, 10, 13, 0, 0, 0);

    @InjectMocks
    private CCLFUpdateService cclfUpdateService;
    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;
    @Mock
    FeatureDecisionService featureDecisionService;


    @Test
    void givenInValidInputWithoutApplicationDTO_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.setApplicationDTO(null);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        assertThatThrownBy(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }

    @Test
    void givenInValidInputWithoutApplicantDTO_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        workflowRequest.getApplicationDTO().setApplicantDTO(null);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        assertThatThrownBy(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }

    @Test
    void givenInValidInputWithoutRepId_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.setId(null);
        assertThatThrownBy(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }

    @Test
    void givenInValidInputWithoutRepOrderDTO_whenUpdateSendToCCLFIsInvoked_thenValidationExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        assertThatThrownBy(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, (RepOrderDTO) null))
                .isInstanceOf(ValidationException.class)
                .hasMessage("Valid ApplicationDTO and RepOrderDTO is required");
    }


    @Test
    void givenValidInputWithUpdateAction_whenUpdateSendToCCLFIsInvoked_thenNoExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        Assertions.assertThatCode(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO)).doesNotThrowAnyException();
    }

    @Test
    void givenValidInputWithNullAction_whenUpdateSendToCCLFIsInvoked_thenNoExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        Assertions.assertThatCode(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO)).doesNotThrowAnyException();
    }

    @Test
    void givenValidInputWithBeforeDecisionDate_whenUpdateSendToCCLFIsInvoked_thenNoExceptionIsThrown() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        LocalDateTime DECISION_DATETIME = LocalDateTime.of(2000, 10, 13, 0, 0, 0);
        workflowRequest.getApplicationDTO().setDecisionDate(Date.from(DECISION_DATETIME.atZone(ZoneId.systemDefault()).toInstant()));
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        Assertions.assertThatCode(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO)).doesNotThrowAnyException();
    }

    @Test
    void givenValidInputWithDifferentObject_whenCompareRepOrderAndApplicationDTOIsInvoked_thenOKResponseIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        assertThat(cclfUpdateService.compareRepOrderAndApplicationDTO(repOrderDTO, workflowRequest.getApplicationDTO())).isFalse();
    }

    @Test
    void givenValidInputWithSameAttributes_whenCompareRepOrderAndApplicationDTOIsInvoked_thenOKResponseIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = getTestApplicationDTO(workflowRequest);
        RepOrderDTO repOrderDTO = getTestRepOrderDTO(applicationDTO);
        assertThat(cclfUpdateService.compareRepOrderAndApplicationDTO(repOrderDTO, workflowRequest.getApplicationDTO())).isTrue();
    }


    @Test
    void givenValidInput_whenUpdateSendToCCLFIsInvoked_thenOKResponseIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        Assertions.assertThatCode(() -> cclfUpdateService.updateSendToCCLF(workflowRequest, repOrderDTO)).doesNotThrowAnyException();
        verify(maatCourtDataApiService).updateSendToCCLF(any());
    }

    @Test
    void givenValidInput_whenGetWithdrawalDateIsInvoked_thenOKResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcWithDrawalDate(Date.from(CC_WITHDRAWAL_DATETIME.atZone(ZoneId.systemDefault()).toInstant()));
        assertThat(cclfUpdateService.getWithDrawalDate(applicationDTO)).isEqualTo(LocalDate.from(CC_WITHDRAWAL_DATETIME));
    }

    @Test
    void givenInputWithOutCCO_whenGetWithdrawalDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.setCrownCourtOverviewDTO(null);
        assertThat(cclfUpdateService.getWithDrawalDate(applicationDTO)).isNull();
    }

    @Test
    void givenInputWithOutCCSummary_whenGetWithdrawalDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setCrownCourtSummaryDTO(null);
        assertThat(cclfUpdateService.getWithDrawalDate(applicationDTO)).isNull();
    }

    @Test
    void givenInputWithOutCCWithdrawal_whenGetWithdrawalDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcWithDrawalDate(null);
        assertThat(cclfUpdateService.getWithDrawalDate(applicationDTO)).isNull();
    }

    @Test
    void givenValidInput_whenGetRepOrderDateIsInvoked_thenOKResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcRepOrderDate(Date.from(CC_REP_ORDER_DATETIME.atZone(ZoneId.systemDefault()).toInstant()));
        assertThat(cclfUpdateService.getRepOrderDate(applicationDTO)).isEqualTo(LocalDate.from(CC_REP_ORDER_DATETIME));
    }

    @Test
    void givenInputWithOutCCO_whenGetRepOrderDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.setCrownCourtOverviewDTO(null);
        assertThat(cclfUpdateService.getRepOrderDate(applicationDTO)).isNull();
    }

    @Test
    void givenInputWithOutCCSummary_whenGetRepOrderDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().setCrownCourtSummaryDTO(null);
        assertThat(cclfUpdateService.getRepOrderDate(applicationDTO)).isNull();
    }

    @Test
    void givenInputWithOutCCRepOrder_whenGetRepOrderDateIsInvoked_thenNullIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcRepOrderDate(null);
        assertThat(cclfUpdateService.getRepOrderDate(applicationDTO)).isNull();
    }

    @Test
    void givenValidInput_whenGetRepOrderCcOutcomeIsInvoked_thenValidResponseIsReturned() {
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.getRepOrderCCOutcome().add(TestModelDataBuilder.getRepOrderCCOutcomeDTO());
        assertThat("CONVICTED").isEqualTo(CCLFUpdateService.getRepOrderCcOutcome(repOrderDTO));
    }

    @Test
    void givenValidInput_whenGetAppealTypeIsInvoked_thenValidResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getAppealDTO().getAppealTypeDTO().setCode(AppealType.ACS.getCode());
        assertThat(AppealType.ACS.getCode()).isEqualTo(CCLFUpdateService.getAppealType(applicationDTO));
    }

    @Test
    void givenValidInput_whenGetOutcomeIsInvoked_thenValidResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().setCcOutcome(TestModelDataBuilder.getOutcomeDTO());
        assertThat(CrownCourtOutcome.SUCCESSFUL.getCode()).isEqualTo(CCLFUpdateService.getOutcome(applicationDTO));
    }

    @Test
    void givenValidInput_whenGetFeeLevelIsInvoked_thenValidResponseIsReturned() {
        ApplicationDTO applicationDTO = TestModelDataBuilder.buildWorkFlowRequest().getApplicationDTO();
        applicationDTO.getCrownCourtOverviewDTO().getCrownCourtSummaryDTO().getEvidenceProvisionFee().setFeeLevel("TEST");
        assertThat("TEST").isEqualTo(CCLFUpdateService.getFeeLevel(applicationDTO));
    }
}
