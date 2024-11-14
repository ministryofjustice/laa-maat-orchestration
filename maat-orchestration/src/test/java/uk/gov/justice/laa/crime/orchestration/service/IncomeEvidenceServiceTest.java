package uk.gov.justice.laa.crime.orchestration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IncomeEvidenceMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.EvidenceApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncomeEvidenceServiceTest {

    @Mock
    private EvidenceApiService evidenceApiService;
    @Mock
    private MaatCourtDataApiService maatCourtDataApiService;
    @Mock
    private IncomeEvidenceMapper incomeEvidenceMapper;
    @InjectMocks
    private IncomeEvidenceService incomeEvidenceService;

    @Test
    void givenValidWorkflowRequestAndRepOrder_whenCreateEvidenceIsInvoked_thenApiServicesCalledAndResponseMapped() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();
        RepOrderDTO repOrder = TestModelDataBuilder.buildRepOrderDTO("CURR");

        when(incomeEvidenceMapper.workflowRequestToApiCreateIncomeEvidenceRequest(workflowRequest))
                .thenReturn(new ApiCreateIncomeEvidenceRequest());
        when(evidenceApiService.createEvidence(any(ApiCreateIncomeEvidenceRequest.class)))
                .thenReturn(new ApiCreateIncomeEvidenceResponse());
        when(incomeEvidenceMapper.mapToMaatApiUpdateAssessment(workflowRequest, repOrder, any(ApiCreateIncomeEvidenceResponse.class)))
                .thenReturn(new MaatApiUpdateAssessment());
        when(maatCourtDataApiService.updateFinancialAssessment(any(MaatApiUpdateAssessment.class)))
                .thenReturn(new MaatApiAssessmentResponse());

        incomeEvidenceService.createEvidence(workflowRequest, repOrder);

        verify(evidenceApiService).createEvidence(any(ApiCreateIncomeEvidenceRequest.class));
        verify(maatCourtDataApiService).updateFinancialAssessment(any(MaatApiUpdateAssessment.class));
        verify(incomeEvidenceMapper).maatApiAssessmentResponseToApplicationDTO(any(MaatApiAssessmentResponse.class),
                workflowRequest.getApplicationDTO());
    }
}

