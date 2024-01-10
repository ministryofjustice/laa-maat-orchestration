package uk.gov.justice.laa.crime.orchestration.integration;

import org.junit.jupiter.api.Test;

public class MeansAssessmentTest {

    @Test
    void givenValidIds_whenFindIsInvoked_thenAssessmentIsReturned () throws Exception {
        // TODO: Generate test response object for call to CMA
        // TODO: Setup wiremock to returned test response object when CMA findUrl is called
        // TODO: Generate test ids to use in request to endpoint under test
        // TODO: Use mvc.perform to perform request to find endpoint under test
        // TODO: Assert that the correct response, type and data is returned in response
    }

    @Test
    void givenInvalidIds_whenFindIsInvoked_thenBadRequestIsReturned() throws Exception {

    }

    @Test
    void givenUnknownIds_whenFindIsInvoked_thenInternalServerErrorIsReturned() throws Exception {

    }

    @Test
    void givenValidRequestData_whenCreateIsInvoked_thenAssessmentIsCreated () throws Exception {

    }

    @Test
    void givenInvalidRequestData_whenCreateIsInvoked_thenBadRequestIsReturned() throws Exception {

    }

    @Test
    void givenErrorCallingMaatApi_whenCreateIsInvoked_thenInternalServerErrorIsReturned() throws Exception {

    }

    @Test
    void givenValidRequestData_whenUpdateIsInvoked_thenAssessmentIsUpdated () throws Exception {

    }

    @Test
    void givenInvalidRequestData_whenUpdateIsInvoked_thenBadRequestIsReturned() throws Exception {

    }

    @Test
    void givenErrorCallingMaatApi_whenUpdateIsInvoked_thenInternalServerErrorIsReturned() throws Exception {

    }
}
