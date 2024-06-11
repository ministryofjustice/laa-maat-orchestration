package uk.gov.justice.laa.crime.orchestration.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import uk.gov.justice.laa.crime.common.model.orchestration.means_assessment.*;

@HttpExchange
public interface CmaApiClient {

    @PutExchange("/api/internal/v1/assessment/means")
    ApiMeansAssessmentResponse update(@RequestBody ApiUpdateMeansAssessmentRequest request);

    @PostExchange("/api/internal/v1/assessment/means")
    ApiMeansAssessmentResponse create(@RequestBody ApiCreateMeansAssessmentRequest request);

    @PutExchange("/api/internal/v1/assessment/means/rollback/{financialAssessmentId}")
    ApiRollbackMeansAssessmentResponse rollback(@PathVariable long financialAssessmentId);

    @GetExchange("/api/internal/v1/assessment/means/{financialAssessmentId}")
    ApiGetMeansAssessmentResponse find(@PathVariable long financialAssessmentId);
}
