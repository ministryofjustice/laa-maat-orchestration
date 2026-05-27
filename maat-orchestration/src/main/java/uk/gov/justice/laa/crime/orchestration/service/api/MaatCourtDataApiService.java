package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.orchestration.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class MaatCourtDataApiService {

    private final MaatCourtDataApiClient maatApiClient;

    public ApplicationDTO executeStoredProcedure(StoredProcedureRequest request) {
        return maatApiClient.executeStoredProcedure(request);
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        return maatApiClient.getRepOrderByRepId(repId);
    }

    public UserSummaryDTO getUserSummary(String username) {
        return maatApiClient.getUserSummary(username);
    }

    public ApplicantDTO getApplicant(int applicantId) {
        return Optional.ofNullable(maatApiClient.getApplicant(applicantId))
                .orElseThrow(() ->
                        new WebClientResponseException(HttpStatus.NOT_FOUND.value(), "Not found", null, null, null));
    }

    public void updateSendToCCLF(SendToCCLFDTO sendToCCLFDTO) {
        maatApiClient.updateSendToCCLF(sendToCCLFDTO);
    }

    public FinancialAssessmentDTO getFinancialAssessment(int financialAssessmentId) {
        return maatApiClient.getFinancialAssessment(financialAssessmentId);
    }

    public MaatApiAssessmentResponse updateFinancialAssessment(MaatApiUpdateAssessment maatApiUpdateAssessment) {
        return maatApiClient.updateFinancialAssessment(maatApiUpdateAssessment);
    }

    public RepOrderDTO patchRepOrder(int repOrderId, Map<String, Object> fieldsToUpdate) {
        return maatApiClient.patchRepOrder(repOrderId, fieldsToUpdate);
    }
}
