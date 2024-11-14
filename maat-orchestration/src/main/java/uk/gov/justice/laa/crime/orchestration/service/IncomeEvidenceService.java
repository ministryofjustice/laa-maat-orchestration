package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.IncomeEvidenceMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.EvidenceApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Slf4j
@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final EvidenceApiService evidenceApiService;
    private final MaatCourtDataApiService maatCourtDataApiService;
    private final IncomeEvidenceMapper incomeEvidenceMapper;

    public void createEvidence(WorkflowRequest request, RepOrderDTO repOrder) {
        log.debug("Creating evidence items for financialAssessmentId: {}",
                request.getApplicationDTO().getAssessmentDTO().getFinancialAssessmentDTO().getId());

        ApiCreateIncomeEvidenceRequest evidenceRequest =
                incomeEvidenceMapper.workflowRequestToApiCreateIncomeEvidenceRequest(request);
        ApiCreateIncomeEvidenceResponse evidenceResponse =
                evidenceApiService.createEvidence(evidenceRequest);

        MaatApiUpdateAssessment maatApiRequest =
                incomeEvidenceMapper.mapToMaatApiUpdateAssessment(request, repOrder, evidenceResponse);
        MaatApiAssessmentResponse maatApiResponse = maatCourtDataApiService.updateFinancialAssessment(maatApiRequest);
        incomeEvidenceMapper.maatApiAssessmentResponseToApplicationDTO(maatApiResponse, request.getApplicationDTO());
    }
}
