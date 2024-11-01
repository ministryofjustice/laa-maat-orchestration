package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.service.api.EvidenceApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Service
@RequiredArgsConstructor
public class IncomeEvidenceService {

    private final EvidenceApiService evidenceApiService;
    private final MaatCourtDataApiService maatCourtDataApiService;

    // TODO: Complete method to call evidence service, map data and then call maat api to perist evidence
    public ??? createEvidence() {
        ApiCreateIncomeEvidenceResponse bla = evidenceApiService.createEvidence();
        MaatApiAssessmentResponse bla2 = maatCourtDataApiService.updateFinancialAssessment();
    }

    // TODO: Complete private method to get date received if evidence received in rep order otherwise set to null
    private ??? getDateReceived() {

    }
}
