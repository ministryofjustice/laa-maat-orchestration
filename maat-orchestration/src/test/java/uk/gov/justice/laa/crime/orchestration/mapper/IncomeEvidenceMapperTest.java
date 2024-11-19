package uk.gov.justice.laa.crime.orchestration.mapper;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.crime.common.model.evidence.ApiCreateIncomeEvidenceRequest;
import uk.gov.justice.laa.crime.common.model.meansassessment.ApiIncomeEvidence;
import uk.gov.justice.laa.crime.common.model.meansassessment.maatapi.MaatApiAssessmentResponse;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.orchestration.data.builder.MeansAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.UserDTO;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
public class IncomeEvidenceMapperTest {

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
    }

    @Test
    void givenInitialAssessment_whenMapToMaatApiUpdateAssessmentIsInvoked_thenMaatApiUpdateAssessmentIsReturned() {

    }

    @Test
    void givenFullAssessment_whenMapToMaatApiUpdateAssessmentIsInvoked_thenMaatApiUpdateAssessmentIsReturned() {

    }

    @Test
    void givenValidParams_whenMaatApiAssessmentResponseToApplicationDTOIsInvoked_thenApplicationDTOIsUpdated() {
        List<ApiIncomeEvidence> incomeEvidences = MeansAssessmentDataBuilder.getIncomeEvidence(false);
        MaatApiAssessmentResponse maatApiAssessmentResponse =
                new MaatApiAssessmentResponse().withIncomeEvidence(incomeEvidences);
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
    }
}
