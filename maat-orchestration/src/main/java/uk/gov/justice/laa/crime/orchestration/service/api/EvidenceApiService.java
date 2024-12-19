package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.commons.client.RestAPIClient;
import uk.gov.justice.laa.crime.orchestration.config.ServicesConfiguration;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EvidenceApiService {

    private static final String REQUEST_STRING = "Request to evidence service: {}";
    private static final String RESPONSE_STRING = "Response from evidence service: {}";
    @Qualifier("evidenceApiClient")
    private final RestAPIClient evidenceApiClient;
    private final ServicesConfiguration configuration;

    public ApiCreateIncomeEvidenceResponse createEvidence(ApiCreateIncomeEvidenceRequest apiCreateIncomeEvidenceRequest) {
        log.debug(REQUEST_STRING, apiCreateIncomeEvidenceRequest);
        ApiCreateIncomeEvidenceResponse apiCreateIncomeEvidenceResponse = evidenceApiClient.post(
                apiCreateIncomeEvidenceRequest,
                new ParameterizedTypeReference<>() {},
                configuration.getEvidenceApi().getEndpoints().getIncomeEvidenceUrl(),
                Map.of()
        );
        log.debug(RESPONSE_STRING, apiCreateIncomeEvidenceResponse);
        return apiCreateIncomeEvidenceResponse;
    }

    public ApiUpdateIncomeEvidenceResponse updateEvidence(ApiUpdateIncomeEvidenceRequest apiUpdateIncomeEvidenceRequest) {
        log.debug(REQUEST_STRING, apiUpdateIncomeEvidenceRequest);
        ApiUpdateIncomeEvidenceResponse apiUpdateIncomeEvidenceResponse = evidenceApiClient.put(
                apiUpdateIncomeEvidenceRequest,
                new ParameterizedTypeReference<>() {},
                configuration.getEvidenceApi().getEndpoints().getIncomeEvidenceUrl(),
                Map.of()
        );
        log.debug(RESPONSE_STRING, apiUpdateIncomeEvidenceResponse);
        return apiUpdateIncomeEvidenceResponse;
    }
}
