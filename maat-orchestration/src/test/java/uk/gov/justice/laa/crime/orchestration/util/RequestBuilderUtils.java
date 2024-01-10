package uk.gov.justice.laa.crime.orchestration.util;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import uk.gov.justice.laa.crime.commons.common.Constants;

public class RequestBuilderUtils {

    private static final String MOCK_TOKEN = "token";

    public static MockHttpServletRequestBuilder buildRequest(HttpMethod method, String endpoint) {
        return buildRequest(method, endpoint, true);
    }

    public static MockHttpServletRequestBuilder buildRequest(HttpMethod method, String endpoint, Boolean withAuth) {
        return buildRequest(method, endpoint, withAuth);
    }

    public static MockHttpServletRequestBuilder buildRequest(HttpMethod method, String endpoint, boolean withAuth) {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.request(method, endpoint);
        if (withAuth) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_TOKEN);
            requestBuilder.header(Constants.LAA_TRANSACTION_ID, "laa-transaction-id");
        }
        return requestBuilder;
    }

    public static MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content,
                                                                         String endpointUrl) {
        return buildRequestGivenContent(method, content, endpointUrl, true);
    }

    public static MockHttpServletRequestBuilder buildRequestGivenContent(HttpMethod method, String content,
                                                                         String endpointUrl, boolean withAuth) {
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.request(method, endpointUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);
        if (withAuth) {
            requestBuilder.header(HttpHeaders.AUTHORIZATION, "Bearer " + MOCK_TOKEN);
            requestBuilder.header(Constants.LAA_TRANSACTION_ID, "laa-transaction-id");
        }
        return requestBuilder;
    }
}
