package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiDetermineMagsRepDecisionRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateApplicationRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.request.ApiUpdateCrownCourtRequest;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiDetermineMagsRepDecisionResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateApplicationResponse;
import uk.gov.justice.laa.crime.common.model.proceeding.response.ApiUpdateCrownCourtOutcomeResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrownCourtProceedingApiClient;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProceedingsApiService {

    private final CrownCourtProceedingApiClient crownCourtProceedingApiClient;

    public ApiDetermineMagsRepDecisionResponse determineMagsRepDecision(ApiDetermineMagsRepDecisionRequest request) {
        return crownCourtProceedingApiClient.determineMagsRepDecision(request);
    }

    public ApiUpdateApplicationResponse updateApplication(ApiUpdateApplicationRequest request) {
        return crownCourtProceedingApiClient.updateApplication(request);
    }

    public ApiUpdateCrownCourtOutcomeResponse updateCrownCourt(ApiUpdateCrownCourtRequest request) {
        return crownCourtProceedingApiClient.updateCrownCourt(request);
    }
}
