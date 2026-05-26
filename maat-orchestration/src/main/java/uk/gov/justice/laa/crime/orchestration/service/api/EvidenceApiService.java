package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.orchestration.client.EvidenceApiClient;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EvidenceApiService {

    private final EvidenceApiClient evidenceApiClient;

    public ApiCreateIncomeEvidenceResponse createEvidence(
            ApiCreateIncomeEvidenceRequest apiCreateIncomeEvidenceRequest) {
        return evidenceApiClient.createEvidence(apiCreateIncomeEvidenceRequest);
    }

    public ApiUpdateIncomeEvidenceResponse updateEvidence(
            ApiUpdateIncomeEvidenceRequest apiUpdateIncomeEvidenceRequest) {
        return evidenceApiClient.updateEvidence(apiUpdateIncomeEvidenceRequest);
    }

    public ApiGetPassportEvidenceResponse getPassportEvidence(int passportAssessmentId) {
        return evidenceApiClient.findPassportEvidence(passportAssessmentId);
    }
}
