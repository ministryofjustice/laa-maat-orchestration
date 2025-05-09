package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.evidence.ApiUpdateIncomeEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiUpdateAssessment;
import uk.gov.justice.laa.crime.enums.AssessmentType;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.evidence.IncomeEvidenceType;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static uk.gov.justice.laa.crime.enums.AssessmentType.FULL;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class IncomeEvidenceMapperTest {

    private static final LocalDateTime EVIDENCE_RECEIVED_DATE = DateUtil
            .convertDateToDateTime(TestModelDataBuilder.ALL_EVIDENCE_RECEIVED_DATE);
    private static final LocalDateTime INCOME_UPLIFT_APPLY_DATE = DateUtil
            .convertDateToDateTime(TestModelDataBuilder.UPLIFT_APPLIED_DATE);
    private static final LocalDateTime INCOME_UPLIFT_REMOVED_DATE = DateUtil
            .convertDateToDateTime(TestModelDataBuilder.UPLIFT_REMOVED_DATE);

    @Mock
    private UserMapper userMapper;
    @Mock
    private MeansAssessmentMapper meansAssessmentMapper;
    @InjectMocks
    private IncomeEvidenceMapper incomeEvidenceMapper;
    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void givenValidWorkflowRequest_whenWorkflowRequestToApiCreateIncomeEvidenceRequestIsInvoked_thenApiCreateIncomeEvidenceRequestIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);

        when(userMapper.userDtoToUserSession(any(UserDTO.class)))
                .thenReturn(TestModelDataBuilder.getApiUserSession());

        ApiCreateIncomeEvidenceRequest apiCreateIncomeEvidenceRequest =
                incomeEvidenceMapper.workflowRequestToApiCreateIncomeEvidenceRequest(workflowRequest);

        softly.assertThat(apiCreateIncomeEvidenceRequest)
                .usingRecursiveComparison()
                .isEqualTo(TestModelDataBuilder.getApiCreateEvidenceRequest(false));
        softly.assertAll();
    }

    @Test
    void givenWorkflowRequestWithPartner_whenWorkflowRequestToApiCreateIncomeEvidenceRequestIsInvoked_thenApiCreateIncomeEvidenceRequestIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest();

        when(userMapper.userDtoToUserSession(any(UserDTO.class)))
                .thenReturn(TestModelDataBuilder.getApiUserSession());

        ApiCreateIncomeEvidenceRequest apiCreateIncomeEvidenceRequest =
                incomeEvidenceMapper.workflowRequestToApiCreateIncomeEvidenceRequest(workflowRequest);

        softly.assertThat(apiCreateIncomeEvidenceRequest)
                .usingRecursiveComparison()
                .isEqualTo(TestModelDataBuilder.getApiCreateEvidenceRequest(true));
        softly.assertAll();
    }

    @Test
    void givenInitialAssessment_whenMapToMaatApiUpdateAssessmentIsInvoked_thenMaatApiUpdateAssessmentIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(); // need to set full available to false
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.setFinancialAssessments(List.of(TestModelDataBuilder.getMaatApiFinancialAssessmentDTO()));
        ApiCreateIncomeEvidenceResponse apiCreateIncomeEvidenceResponse = TestModelDataBuilder.getCreateIncomeEvidenceResponse();

        when(meansAssessmentMapper.assessmentDetailsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentDetail()));
        when(meansAssessmentMapper.childWeightingsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentChildWeighting()));

        MaatApiUpdateAssessment maatApiUpdateAssessment =
                incomeEvidenceMapper.mapToMaatApiUpdateAssessment(workflowRequest, repOrderDTO, apiCreateIncomeEvidenceResponse);

        softly.assertThat(maatApiUpdateAssessment)
                .usingRecursiveComparison()
                .ignoringFields("laaTransactionId")
                .isEqualTo(TestModelDataBuilder.getMaatApiUpdateAssessment(AssessmentType.INIT, Boolean.TRUE));
        softly.assertAll();
    }

    @Test
    void givenFullAssessment_whenMapToMaatApiUpdateAssessmentIsInvoked_thenMaatApiUpdateAssessmentIsReturned() {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT); // full
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTO("CURR");
        repOrderDTO.setFinancialAssessments(List.of(TestModelDataBuilder.getMaatApiFinancialAssessmentDTO()));
        ApiCreateIncomeEvidenceResponse apiCreateIncomeEvidenceResponse = TestModelDataBuilder.getCreateIncomeEvidenceResponse();

        when(meansAssessmentMapper.assessmentDetailsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentDetail()));
        when(meansAssessmentMapper.childWeightingsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentChildWeighting()));

        MaatApiUpdateAssessment maatApiUpdateAssessment =
                incomeEvidenceMapper.mapToMaatApiUpdateAssessment(workflowRequest, repOrderDTO, apiCreateIncomeEvidenceResponse);

        softly.assertThat(maatApiUpdateAssessment)
                .usingRecursiveComparison()
                .ignoringFields("laaTransactionId")
                .isEqualTo(TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE));
        softly.assertAll();
    }

    @Test
    void givenValidParams_whenMaatApiAssessmentResponseToApplicationDTOIsInvoked_thenApplicationDTOIsUpdated() {
        List<ApiIncomeEvidence> incomeEvidences = MeansAssessmentDataBuilder.getIncomeEvidence(false);
        MaatApiAssessmentResponse maatApiAssessmentResponse =
                new MaatApiAssessmentResponse().withFinAssIncomeEvidences(incomeEvidences);
        ApplicationDTO applicationDTO = MeansAssessmentDataBuilder.getApplicationDTO();

        when(meansAssessmentMapper.getEvidenceDTO(incomeEvidences.get(0)))
                .thenReturn(MeansAssessmentDataBuilder.getApplicantEvidenceDTO());
        when(meansAssessmentMapper.getEvidenceDTO(incomeEvidences.get(1)))
                .thenReturn(MeansAssessmentDataBuilder.getPartnerEvidenceDTO());

        incomeEvidenceMapper.maatApiAssessmentResponseToApplicationDTO(maatApiAssessmentResponse, applicationDTO);

        softly.assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence().getApplicantIncomeEvidenceList())
                .isEqualTo(List.of(MeansAssessmentDataBuilder.getApplicantEvidenceDTO()));
        softly.assertThat(applicationDTO.getAssessmentDTO().getFinancialAssessmentDTO().getIncomeEvidence().getPartnerIncomeEvidenceList())
                .isEqualTo(List.of(MeansAssessmentDataBuilder.getPartnerEvidenceDTO()));
        softly.assertAll();
    }

    @ParameterizedTest
    @MethodSource("existingEvidences")
    void givenExistingEvidences_whenMapToMaatApiUpdateAssessmentIsInvoked_thenMaatApiUpdateAssessmentIsReturned(
            RepOrderDTO repOrderDTO, MaatApiUpdateAssessment expectedAssessment
    ) {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT); // full
        ApiCreateIncomeEvidenceResponse apiCreateIncomeEvidenceResponse = TestModelDataBuilder.getCreateIncomeEvidenceResponse();

        when(meansAssessmentMapper.assessmentDetailsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentDetail()));
        when(meansAssessmentMapper.childWeightingsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentChildWeighting()));

        MaatApiUpdateAssessment maatApiUpdateAssessment =
                incomeEvidenceMapper.mapToMaatApiUpdateAssessment(workflowRequest, repOrderDTO, apiCreateIncomeEvidenceResponse);

        softly.assertThat(maatApiUpdateAssessment)
                .usingRecursiveComparison()
                .ignoringFields("laaTransactionId")
                .isEqualTo(expectedAssessment);
        softly.assertAll();
    }



    private static Stream<Arguments> existingEvidences() {

        MaatApiUpdateAssessment noExistingEvidences = TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE);
        noExistingEvidences.getFinAssIncomeEvidences().forEach(evidence -> evidence.setDateReceived(null));

        MaatApiUpdateAssessment existingEvidencesAssessment = TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE);
        RepOrderDTO existingEvidencesRepOrderDTO = RepOrderDTO.builder()
                .passportAssessments(List.of(TestModelDataBuilder.getPassportAssessmentDTO()))
                .build();

        MaatApiUpdateAssessment existingFinEvidencesAssessment = TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE);
        RepOrderDTO existingFinEvidenceRepOrderDTO = RepOrderDTO.builder()
                .financialAssessments(List.of(TestModelDataBuilder.getMaatApiFinancialAssessmentDTO()))
                .build();
        RepOrderDTO existingBothEvidencesRepOrderDTO = RepOrderDTO.builder()
                .financialAssessments(List.of(TestModelDataBuilder.getMaatApiFinancialAssessmentDTO()))
                .passportAssessments(List.of(TestModelDataBuilder.getPassportAssessmentDTO()))
                .build();

        return Stream.of(
                Arguments.of(RepOrderDTO.builder().build(), noExistingEvidences),
                Arguments.of(existingEvidencesRepOrderDTO, existingEvidencesAssessment),
                Arguments.of(existingFinEvidenceRepOrderDTO, existingFinEvidencesAssessment),
                Arguments.of(existingBothEvidencesRepOrderDTO, existingFinEvidencesAssessment)
        );
    }


    private static Stream<Arguments> parameterizedUpdateIncomeEvidences() {

        ApiUpdateIncomeEvidenceResponse apiUpdateIncomeEvidenceResponse = TestModelDataBuilder.getUpdateIncomeEvidenceResponse(Boolean.TRUE, IncomeEvidenceType.TAX_RETURN);

        MaatApiUpdateAssessment noExistingEvidences = TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE, IncomeEvidenceType.CDS15);
        noExistingEvidences.getFinAssIncomeEvidences().forEach(evidence -> evidence.setDateReceived(null));
        ApiUpdateIncomeEvidenceResponse apiEmptyDateReceivedResponse = TestModelDataBuilder.getUpdateIncomeEvidenceResponse(Boolean.TRUE, IncomeEvidenceType.CDS15);
        apiEmptyDateReceivedResponse.getApplicantEvidenceItems().getIncomeEvidenceItems().forEach(apiIncomeEvidence -> apiIncomeEvidence.setDateReceived(null));
        apiEmptyDateReceivedResponse.getPartnerEvidenceItems().getIncomeEvidenceItems().forEach(apiIncomeEvidence -> apiIncomeEvidence.setDateReceived(null));


        MaatApiUpdateAssessment existingEvidencesAssessment = TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE);
        RepOrderDTO existingEvidencesRepOrderDTO = RepOrderDTO.builder()
                .passportAssessments(List.of(TestModelDataBuilder.getPassportAssessmentDTO()))
                .build();


        MaatApiUpdateAssessment existingFinEvidencesAssessment = TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.TRUE);
        RepOrderDTO existingFinEvidenceRepOrderDTO = RepOrderDTO.builder()
                .financialAssessments(List.of(TestModelDataBuilder.getMaatApiFinancialAssessmentDTO()))
                .build();
        RepOrderDTO existingBothEvidencesRepOrderDTO = RepOrderDTO.builder()
                .financialAssessments(List.of(TestModelDataBuilder.getMaatApiFinancialAssessmentDTO()))
                .passportAssessments(List.of(TestModelDataBuilder.getPassportAssessmentDTO()))
                .build();

        return Stream.of(
                Arguments.of(RepOrderDTO.builder().build(), noExistingEvidences,  apiEmptyDateReceivedResponse),
                Arguments.of(existingEvidencesRepOrderDTO, existingEvidencesAssessment,  apiUpdateIncomeEvidenceResponse),
                Arguments.of(existingFinEvidenceRepOrderDTO, existingFinEvidencesAssessment, apiUpdateIncomeEvidenceResponse),
                Arguments.of(existingBothEvidencesRepOrderDTO, existingFinEvidencesAssessment, apiUpdateIncomeEvidenceResponse),
                Arguments.of(existingBothEvidencesRepOrderDTO, TestModelDataBuilder.getMaatApiUpdateAssessment(FULL, Boolean.FALSE),
                        TestModelDataBuilder.getUpdateIncomeEvidenceResponse(Boolean.FALSE, IncomeEvidenceType.TAX_RETURN))
        );
    }

    @ParameterizedTest
    @MethodSource("updateIncomeEvidences")
    void givenValidWorkflowRequest_whenWorkflowRequestToApiUpdateIncomeEvidenceRequestIsInvoked_thenApiUpdateIncomeEvidenceRequestIsReturned(
            WorkflowRequest workflowRequest,
            ApiUpdateIncomeEvidenceRequest expectedRequest) {
        ApplicationDTO applicationDTO = workflowRequest.getApplicationDTO();
        UserDTO userDTO = workflowRequest.getUserDTO();

        when(userMapper.userDtoToUserSession(userDTO))
                .thenReturn(TestModelDataBuilder.getApiUserSession());

        ApiUpdateIncomeEvidenceRequest actualRequest =
                incomeEvidenceMapper.workflowRequestToApiUpdateIncomeEvidenceRequest(applicationDTO, userDTO);

        assertThat(actualRequest).usingRecursiveComparison().isEqualTo(expectedRequest);
    }

    private static Stream<Arguments> updateIncomeEvidences() {
        return Stream.of(
                Arguments.of(TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT),
                        TestModelDataBuilder.getApiUpdateEvidenceRequest(false)),
                Arguments.of(TestModelDataBuilder.buildWorkFlowRequest(),
                        TestModelDataBuilder.getApiUpdateEvidenceRequest(true))
        );
    }

    @ParameterizedTest
    @MethodSource("parameterizedUpdateIncomeEvidences")
    void givenExistingEvidences_whenMapUpdateEvidenceToMaatApiUpdateAssessmentIsInvoked_thenMaatApiUpdateAssessmentIsReturned(
            RepOrderDTO repOrderDTO, MaatApiUpdateAssessment expectedAssessment , ApiUpdateIncomeEvidenceResponse apiUpdateIncomeEvidenceResponse) {
        WorkflowRequest workflowRequest = TestModelDataBuilder.buildWorkFlowRequest(CourtType.CROWN_COURT);
        expectedAssessment.setIncomeEvidenceDueDate(TestModelDataBuilder.EVIDENCE_DUE_DATE);
        expectedAssessment.setEvidenceReceivedDate(EVIDENCE_RECEIVED_DATE);
        expectedAssessment.setIncomeUpliftApplyDate(INCOME_UPLIFT_APPLY_DATE);
        expectedAssessment.setIncomeUpliftRemoveDate(INCOME_UPLIFT_REMOVED_DATE);

        when(meansAssessmentMapper.assessmentDetailsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentDetail()));
        when(meansAssessmentMapper.childWeightingsBuilder(anyList()))
                .thenReturn(List.of(TestModelDataBuilder.getAssessmentChildWeighting()));

        MaatApiUpdateAssessment maatApiUpdateAssessment =
                incomeEvidenceMapper.mapUpdateEvidenceToMaatApiUpdateAssessment(workflowRequest, repOrderDTO, apiUpdateIncomeEvidenceResponse);

        assertThat(maatApiUpdateAssessment)
                .usingRecursiveComparison()
                .ignoringFields("laaTransactionId")
                .isEqualTo(expectedAssessment);
    }
}
