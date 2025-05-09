package uk.gov.justice.laa.crime.orchestration.client;

import java.util.Map;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.orchestration.dto.StoredProcedureRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.SendToCCLFDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserSummaryDTO;

@HttpExchange
public interface MaatCourtDataApiClient {

  @PostExchange("/assessment/execute-stored-procedure")
  ApplicationDTO executeStoredProcedure(@RequestBody StoredProcedureRequest request);

  @GetExchange("/assessment/rep-orders/{repId}")
  RepOrderDTO getRepOrderByRepId(@PathVariable Integer repId);

  @GetExchange("/users/summary/{username}")
  UserSummaryDTO getUserSummary(@PathVariable String username);

  @PutExchange("/application/applicant/update-cclf")
  void updateSendToCCLF(@RequestBody SendToCCLFDTO request);

  @GetExchange("/assessment/financial-assessments")
  FinancialAssessmentDTO getFinancialAssessment(@RequestBody int financialAssessmentId);

  @PutExchange("/assessment/financial-assessments")
  MaatApiAssessmentResponse updateFinancialAssessment(@RequestBody MaatApiUpdateAssessment request);
  
  @PatchExchange("/assessment/rep-orders/{repId}")
  void patchRepOrder(@PathVariable int repId, @RequestBody Map<String, Object> fieldsToUpdate);
  
  
}
