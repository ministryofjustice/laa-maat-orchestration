package uk.gov.justice.laa.crime.orchestration.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiCreateMeansAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiGetMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiRollbackMeansAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiUpdateMeansAssessmentRequest;

@HttpExchange
public interface CrimeMeansAssessmentApiClient {
  
  @GetExchange("/{financialAssessmentId}")
  ApiGetMeansAssessmentResponse findMeansAssessment(@PathVariable Integer financialAssessmentId);

  @PostExchange()
  ApiMeansAssessmentResponse createMeansAssessment(@RequestBody ApiCreateMeansAssessmentRequest request);

  @PutExchange()
  ApiMeansAssessmentResponse updateMeansAssessment(@RequestBody ApiUpdateMeansAssessmentRequest request);

  @PatchExchange("/rollback/{financialAssessmentId}")
  ApiRollbackMeansAssessmentResponse rollback(@PathVariable Long financialAssessmentId);
}
