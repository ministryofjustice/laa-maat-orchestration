package uk.gov.justice.laa.crime.orchestration.client;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;

@HttpExchange
public interface ApplicationTrackingApiClient {
  
  @PostExchange("/application-tracking-output-result")
  void sendTrackingOutputResult(@RequestBody ApplicationTrackingOutputResult request);
}
