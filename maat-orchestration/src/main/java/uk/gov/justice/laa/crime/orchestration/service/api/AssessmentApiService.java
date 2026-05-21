package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealRequest;
import uk.gov.justice.laa.crime.common.model.ioj.ApiCreateIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiGetIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.ioj.ApiRollbackIojAppealResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiCreatePassportedAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.passported.ApiCreatePassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.orchestration.client.CrimeAssessmentApiClient;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssessmentApiService {

    private static final String REQUEST_STRING = "Request to Assessment Service: {}";

    private final CrimeAssessmentApiClient assessmentApiClient;

    public ApiGetIojAppealResponse findIojAppeal(int appealId) {
        log.debug("Request to Assessment Service for IoJ Appeal ID: {}", appealId);

        // 404s are intercepted by the WebClientFilters, so we re-throw the exception here to be
        // caught by our DefaultExceptionHandler
        return Optional.ofNullable(assessmentApiClient.getIojAppeal(appealId))
                .orElseThrow(() ->
                        new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null));
    }

    public ApiCreateIojAppealResponse createIojAppeal(ApiCreateIojAppealRequest request) {
        log.debug(REQUEST_STRING, request);
        return assessmentApiClient.createIojAppeal(request);
    }

    public ApiGetPassportedAssessmentResponse findPassportAssessment(int id) {
        log.debug("Request to Assessment Service to retrieve Passport Assessment: {}", id);

        return Optional.ofNullable(assessmentApiClient.getPassportAssessment(id))
                .orElseThrow(() ->
                        new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null));
    }

    public ApiCreatePassportedAssessmentResponse createPassportAssessment(
            ApiCreatePassportedAssessmentRequest request) {
        log.debug(REQUEST_STRING, request);
        return assessmentApiClient.createPassportAssessment(request);
    }

    public ApiRollbackIojAppealResponse rollback(String appealId) {
        log.debug(REQUEST_STRING, appealId);
        return assessmentApiClient.rollback(appealId);
    }
}
