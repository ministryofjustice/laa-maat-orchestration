package uk.gov.justice.laa.crime.orchestration.commmon;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.common.ApplicationUtils;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;

import org.junit.jupiter.api.Test;

class ApplicationUtilsTest {

    @Test
    void givenApplicationWithPartner_whenGetPartnerIdIsInvoked_thenPartnerIdReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        Integer partnerId = ApplicationUtils.getPartnerId(workflowRequest.getApplicationDTO());

        assertThat(partnerId).isEqualTo(Constants.PARTNER_ID);
    }

    @Test
    void givenApplicationWithoutPartner_whenGetPartnerIdIsInvoked_thenNullIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        Integer partnerId = ApplicationUtils.getPartnerId(workflowRequest.getApplicationDTO());

        assertThat(partnerId).isNull();
    }
}
