package uk.gov.justice.laa.crime.orchestration.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PostExchange;
import org.springframework.web.service.annotation.PutExchange;
import uk.gov.justice.laa.crime.common.model.hardship.ApiFindHardshipResponse;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipRequest;
import uk.gov.justice.laa.crime.common.model.hardship.ApiPerformHardshipResponse;

@HttpExchange
public interface HardshipApiClient {

  @GetExchange("/{hardshipReviewId}")
  ApiFindHardshipResponse getHardshipReview(@PathVariable Integer hardshipReviewId);

  @PostExchange
  ApiPerformHardshipResponse createHardshipReview(@RequestBody ApiPerformHardshipRequest request);

  @PutExchange
  ApiPerformHardshipResponse updateHardshipReview(@RequestBody ApiPerformHardshipRequest request);

  @PatchExchange("/{hardshipReviewId}")
  void rollback(@PathVariable Integer hardshipReviewId);
}
