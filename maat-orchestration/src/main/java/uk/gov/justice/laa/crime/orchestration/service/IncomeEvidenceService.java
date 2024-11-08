package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IncomeEvidenceMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.EvidenceApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final EvidenceApiService evidenceApiService;
    private final MaatCourtDataApiService maatCourtDataApiService;
    private final IncomeEvidenceMapper incomeEvidenceMapper;
    
    public ??? createEvidence(WorkflowRequest request, RepOrderDTO repOrderDTO) {
        ApiCreateIncomeEvidenceRequest evidenceRequest = incomeEvidenceMapper.workflowRequestToApiCreateIncomeEvidenceRequest(request);
        ApiCreateIncomeEvidenceResponse evidenceResponse = evidenceApiService.createEvidence(evidenceRequest);
        // TODO: Map MaatApiUpdateAssessment for call to maat api
        MaatApiAssessmentResponse bla2 = maatCourtDataApiService.updateFinancialAssessment();
    }

    // TODO: Complete private method to get date received if evidence received in rep order otherwise set to null
    // Use RepOrderDTO here
    private ??? getDateReceived() {

    }
}
