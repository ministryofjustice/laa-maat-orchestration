package uk.gov.justice.laa.crime.orchestration.commmon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.common.PartnerResolver;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class PartnerResolverTest {

    @Test
    void givenApplicationWithPartner_whenGetPartnerIdIsInvoked_thenPartnerIdReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        Optional<Integer> partnerId = PartnerResolver.getPartnerId(workflowRequest.getApplicationDTO());

        assertThat(partnerId).contains(Constants.PARTNER_ID);
    }

    @Test
    void givenApplicationWithoutPartner_whenGetPartnerIdIsInvoked_thenNullIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        Optional<Integer> partnerId = PartnerResolver.getPartnerId(workflowRequest.getApplicationDTO());

        assertThat(partnerId).isEmpty();
    }
}
