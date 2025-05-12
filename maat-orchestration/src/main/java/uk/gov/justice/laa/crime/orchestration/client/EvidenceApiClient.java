package uk.gov.justice.laa.crime.orchestration.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;

@HttpExchange
public interface EvidenceApiClient {

  @PostExchange
  ApiCreateIncomeEvidenceResponse createEvidence(@RequestBody ApiCreateIncomeEvidenceRequest request);

  @PutExchange
  ApiUpdateIncomeEvidenceResponse updateEvidence(@RequestBody ApiUpdateIncomeEvidenceRequest request);
}
