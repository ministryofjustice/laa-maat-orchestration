package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.orchestration.client.EvidenceApiClient;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceApiService {

    private static final String REQUEST_STRING = "Request to evidence service: {}";

    private final EvidenceApiClient evidenceApiClient;

    public ApiCreateIncomeEvidenceResponse createEvidence(
            ApiCreateIncomeEvidenceRequest apiCreateIncomeEvidenceRequest) {
        log.debug(REQUEST_STRING, apiCreateIncomeEvidenceRequest);
        return evidenceApiClient.createEvidence(apiCreateIncomeEvidenceRequest);
    }

    public ApiUpdateIncomeEvidenceResponse updateEvidence(
            ApiUpdateIncomeEvidenceRequest apiUpdateIncomeEvidenceRequest) {
        log.debug(REQUEST_STRING, apiUpdateIncomeEvidenceRequest);
        return evidenceApiClient.updateEvidence(apiUpdateIncomeEvidenceRequest);
    }

    public ApiGetPassportEvidenceResponse getPassportEvidence(int passportAssessmentId) {
        log.debug("Request to evidence service to retrieve evidence for passport assessment: {}", passportAssessmentId);
        return evidenceApiClient.findPassportEvidence(passportAssessmentId);
    }
}
