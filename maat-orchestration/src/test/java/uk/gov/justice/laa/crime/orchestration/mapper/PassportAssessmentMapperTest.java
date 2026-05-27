package uk.gov.justice.laa.crime.orchestration.mapper;

import static org.mockito.Mockito.when;

import uk.gov.justice.laa.crime.common.model.common.ApiUserSession;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiCreatePassportedAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.enums.BenefitType;
import uk.gov.justice.laa.crime.enums.NewWorkReason;
import uk.gov.justice.laa.crime.enums.orchestration.Action;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.EvidenceDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.PassportAssessmentDataBuilder;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.ApplicationDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.dto.validation.UserActionDTO;

import java.util.Optional;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class PassportAssessmentMapperTest {

    @Mock
    UserMapper userMapper = new UserMapper();

    @Mock
    private PassportEvidenceMapper passportEvidenceMapper;

    @InjectMocks
    private PassportAssessmentMapper passportAssessmentMapper;

    @InjectSoftAssertions
    private SoftAssertions softly;

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenValidApiResponse_whenApiGetPassportedAssessmentResponseToPassportedDTOIsInvoked_thenPassportDTOIsReturned(
            boolean withPartner) {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(withPartner);
        ApiGetPassportEvidenceResponse evidence = EvidenceDataBuilder.getApiGetPassportEvidenceResponse(withPartner);
        ApplicantDTO applicant = withPartner ? PassportAssessmentDataBuilder.getApplicantDTO() : null;

        when(passportEvidenceMapper.apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(evidence))
                .thenReturn(EvidenceDataBuilder.getIncomeEvidenceSummaryDTO(withPartner));

        PassportedDTO actual = passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(withPartner), evidence, applicant);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
        softly.assertAll();
    }

    @ParameterizedTest
    @EnumSource(BenefitType.class)
    void givenDifferentBenefitsInResponse_whenAssessmentResponseToPassportedIsInvoked_thenAppropriateBenefitIsReturned(
            BenefitType benefit) {
        PassportedDTO expected = PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER);
        expected.setBenefitIncomeSupport(false);
        ApiGetPassportedAssessmentResponse response =
                PassportAssessmentDataBuilder.getApiGetPassportedAssessmentResponse(Constants.WITHOUT_PARTNER);
        response.getDeclaredBenefit().setBenefitType(benefit);
        ApiGetPassportEvidenceResponse evidence =
                EvidenceDataBuilder.getApiGetPassportEvidenceResponse(Constants.WITHOUT_PARTNER);
        switch (benefit) {
            case BenefitType.INCOME_SUPPORT -> expected.setBenefitIncomeSupport(true);
            case BenefitType.JSA -> {
                expected.setBenefitJobSeeker(PassportAssessmentDataBuilder.getJobSeekerDTO());
                response.setDeclaredBenefit(PassportAssessmentDataBuilder.getDeclaredBenefit(BenefitType.JSA));
            }
            case BenefitType.GSPC -> expected.setBenefitGaurenteedStatePension(true);
            case BenefitType.ESA -> expected.setBenefitEmploymentSupport(true);
            case BenefitType.UC -> expected.setBenefitUniversalCredit(true);
        }

        when(passportEvidenceMapper.apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(evidence))
                .thenReturn(EvidenceDataBuilder.getIncomeEvidenceSummaryDTO(Constants.WITHOUT_PARTNER));

        PassportedDTO actual =
                passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response, evidence, null);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
        softly.assertAll();
    }

    @Test
    void givenValidWorkflowRequest_whenGetUserActionDTOIsInvoked_thenUserActionDTOIsReturned() {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        NewWorkReason newWorkReason = NewWorkReason.getFrom(request.getApplicationDTO()
                .getPassportedDTO()
                .getNewWorkReason()
                .getCode());
        UserActionDTO expected = TestModelDataBuilder.getUserActionDTO();

        when(userMapper.getUserActionDTO(request, Action.CREATE_PASSPORT_ASSESSMENT, newWorkReason))
                .thenReturn(expected);

        UserActionDTO actual = passportAssessmentMapper.getUserActionDTO(request);

        softly.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        softly.assertAll();
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenValidWorkflowRequest_whenWorkflowRequestToPassportedAssessmentRequestIsInvoked_thenRequestIsReturned(
            boolean hasPartner) {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        request.getApplicationDTO().setPassportedDTO(PassportAssessmentDataBuilder.getPassportedDTO(hasPartner));
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        Optional<Integer> partnerId = hasPartner ? Optional.of(Constants.PARTNER_ID) : Optional.empty();
        ApiCreatePassportedAssessmentRequest expected =
                PassportAssessmentDataBuilder.getApiCreatePassportedAssessmentRequest(hasPartner);

        when(userMapper.userDtoToUserSession(request.getUserDTO())).thenReturn(userSession);

        ApiCreatePassportedAssessmentRequest actual =
                passportAssessmentMapper.workflowRequestToApiCreatePassportedAssessmentRequest(request, partnerId);

        softly.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        softly.assertAll();
    }

    @ParameterizedTest
    @EnumSource(BenefitType.class)
    void givenRequestWithDifferentBenefits_whenWorkflowRequestToPassportedRequestIsInvoked_thenRequestIsReturned(
            BenefitType benefit) {
        WorkflowRequest request = TestModelDataBuilder.buildWorkFlowRequest();
        ApplicationDTO applicationDTO = request.getApplicationDTO();
        applicationDTO.setPassportedDTO(PassportAssessmentDataBuilder.getPassportedDTO(Constants.WITHOUT_PARTNER));
        applicationDTO.getPassportedDTO().setBenefitIncomeSupport(false);
        ApiUserSession userSession = TestModelDataBuilder.getApiUserSession();
        ApiCreatePassportedAssessmentRequest expected =
                PassportAssessmentDataBuilder.getApiCreatePassportedAssessmentRequest(Constants.WITHOUT_PARTNER);
        expected.getPassportedAssessment()
                .setDeclaredBenefit(PassportAssessmentDataBuilder.getDeclaredBenefit(benefit));
        switch (benefit) {
            case BenefitType.INCOME_SUPPORT -> applicationDTO.getPassportedDTO().setBenefitIncomeSupport(true);
            case BenefitType.JSA ->
                applicationDTO.getPassportedDTO().setBenefitJobSeeker(PassportAssessmentDataBuilder.getJobSeekerDTO());
            case BenefitType.GSPC -> applicationDTO.getPassportedDTO().setBenefitGaurenteedStatePension(true);
            case BenefitType.ESA -> applicationDTO.getPassportedDTO().setBenefitEmploymentSupport(true);
            case BenefitType.UC -> applicationDTO.getPassportedDTO().setBenefitUniversalCredit(true);
        }

        when(userMapper.userDtoToUserSession(request.getUserDTO())).thenReturn(userSession);

        ApiCreatePassportedAssessmentRequest actual =
                passportAssessmentMapper.workflowRequestToApiCreatePassportedAssessmentRequest(
                        request, Optional.empty());

        softly.assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
        softly.assertAll();
    }
}
