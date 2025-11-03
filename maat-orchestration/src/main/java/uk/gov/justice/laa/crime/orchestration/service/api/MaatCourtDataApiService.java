package uk.gov.justice.laa.crime.orchestration.service.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.orchestration.client.MaatCourtDataApiClient;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;

import java.util.Map;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MaatCourtDataApiService {

    private static final String RESPONSE_STRING = "Response from MAAT Court Data Service: {}";
    private static final String REQUEST_STRING = "Request to MAAT Court Data Service: {}";
    private final MaatCourtDataApiClient maatApiClient;

    public ApplicationDTO executeStoredProcedure(StoredProcedureRequest request) {
        log.debug(REQUEST_STRING, request);
        ApplicationDTO response = maatApiClient.executeStoredProcedure(request);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public RepOrderDTO getRepOrderByRepId(Integer repId) {
        log.info(REQUEST_STRING, repId);
        RepOrderDTO response = maatApiClient.getRepOrderByRepId(repId);
        log.debug(RESPONSE_STRING, response);
        return response;
    }

    public UserSummaryDTO getUserSummary(String username) {
        log.info(REQUEST_STRING, username);
        UserSummaryDTO userSummaryDTO = maatApiClient.getUserSummary(username);
        log.debug(RESPONSE_STRING, userSummaryDTO);
        return userSummaryDTO;
    }

    public void updateSendToCCLF(SendToCCLFDTO sendToCCLFDTO) {
        log.debug(REQUEST_STRING, sendToCCLFDTO);
        maatApiClient.updateSendToCCLF(sendToCCLFDTO);
    }

    public FinancialAssessmentDTO getFinancialAssessment(int financialAssessmentId) {
        log.info(REQUEST_STRING, financialAssessmentId);
        FinancialAssessmentDTO financialAssessmentDTO = maatApiClient.getFinancialAssessment(financialAssessmentId);
        log.debug(RESPONSE_STRING, financialAssessmentDTO);
        return financialAssessmentDTO;
    }

    public MaatApiAssessmentResponse updateFinancialAssessment(MaatApiUpdateAssessment maatApiUpdateAssessment) {
        log.debug(REQUEST_STRING, maatApiUpdateAssessment);
        MaatApiAssessmentResponse financialAssessmentDTO =
                maatApiClient.updateFinancialAssessment(maatApiUpdateAssessment);
        log.debug(RESPONSE_STRING, financialAssessmentDTO);
        return financialAssessmentDTO;
    }

    public void patchRepOrder(int repOrderId, Map<String, Object> fieldsToUpdate) {
        log.debug(REQUEST_STRING, fieldsToUpdate);
        maatApiClient.patchRepOrder(repOrderId, fieldsToUpdate);
    }
}
