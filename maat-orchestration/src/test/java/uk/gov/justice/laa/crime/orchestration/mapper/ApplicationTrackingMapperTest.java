package uk.gov.justice.laa.crime.orchestration.mapper;

import static uk.gov.justice.laa.crime.orchestration.data.Constants.ASSESSMENT_SUMMARY_ID;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.LEGACY_APPEAL_ID;
import static uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder.USN;

import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.AssessmentType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.CaseType;
import uk.gov.justice.laa.crime.common.model.tracking.ApplicationTrackingOutputResult.RequestSource;
import uk.gov.justice.laa.crime.common.model.tracking.Hardship;
import uk.gov.justice.laa.crime.common.model.tracking.Hardship.HardshipResult;
import uk.gov.justice.laa.crime.common.model.tracking.Hardship.HardshipType;
import uk.gov.justice.laa.crime.common.model.tracking.Ioj;
import uk.gov.justice.laa.crime.common.model.tracking.Ioj.IojAppealResult;
import uk.gov.justice.laa.crime.common.model.tracking.MeansAssessment;
import uk.gov.justice.laa.crime.common.model.tracking.MeansAssessment.MeansAssessmentResult;
import uk.gov.justice.laa.crime.common.model.tracking.Passport;
import uk.gov.justice.laa.crime.common.model.tracking.Passport.PassportResult;
import uk.gov.justice.laa.crime.enums.CourtType;
import uk.gov.justice.laa.crime.enums.DecisionReason;
import uk.gov.justice.laa.crime.orchestration.data.Constants;
import uk.gov.justice.laa.crime.orchestration.data.builder.TestModelDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.AssessmentStatusDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IOJDecisionReasonDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;

import java.util.Date;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SoftAssertionsExtension.class)
class ApplicationTrackingMapperTest {

    public static final String ASSESSOR_NAME = "FIRSTNAME SURNAME";
    ApplicationTrackingMapper mapper = new ApplicationTrackingMapper();

    @InjectSoftAssertions
    private SoftAssertions softly;

    @Test
    void giveAValidWorkflowRequest_whenBuildIsInvoked_thenMappingIsCorrect() {

        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();
        ApplicationTrackingOutputResult request =
                mapper.build(workflowRequest, repOrderDTO, AssessmentType.CCHARDSHIP, RequestSource.HARDSHIP);

        softly.assertThat(request.getUsn()).isEqualTo(USN.longValue());
        softly.assertThat(request.getMaatRef()).isEqualTo(TestModelDataBuilder.REP_ID.intValue());
        softly.assertThat(request.getActionKeyId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(request.getCaseId())
                .isEqualTo(workflowRequest.getApplicationDTO().getCaseId());
        softly.assertThat(request.getCaseType()).isEqualTo(CaseType.EITHER_WAY);
        softly.assertThat(request.getAssessmentId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(request.getAssessmentType()).isEqualTo(AssessmentType.CCHARDSHIP);
        softly.assertThat(request.getDwpResult()).isNull();
        softly.assertThat(request.getRepDecision()).isEqualTo(DecisionReason.GRANTED.getDescription());
        softly.assertThat(request.getCcRepDecision()).isEqualTo(CrownCourtSummaryDTO.REP_ORDER_DECISION_GRANTED);
        softly.assertThat(request.getMagsOutcome())
                .isEqualTo(
                        workflowRequest.getApplicationDTO().getMagsOutcomeDTO().getOutcome());
        softly.assertThat(request.getRequestSource()).isEqualTo(RequestSource.HARDSHIP);
        softly.assertAll();
    }

    @Test
    void giveAEmptyRepOrderDecision_whenBuildIsInvoked_thenMappingIsCorrect() {

        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setUsn(TestModelDataBuilder.REP_ID.longValue());
        workflowRequest.getApplicationDTO().getRepOrderDecision().setDescription(null);
        workflowRequest
                .getApplicationDTO()
                .getCrownCourtOverviewDTO()
                .getCrownCourtSummaryDTO()
                .setRepOrderDecision(null);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        ApplicationTrackingOutputResult request =
                mapper.build(workflowRequest, repOrderDTO, AssessmentType.CCHARDSHIP, RequestSource.HARDSHIP);
        softly.assertThat(request.getUsn()).isEqualTo(TestModelDataBuilder.REP_ID.intValue());
        softly.assertThat(request.getMaatRef()).isEqualTo(TestModelDataBuilder.REP_ID.intValue());
        softly.assertThat(request.getActionKeyId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(request.getCaseId())
                .isEqualTo(workflowRequest.getApplicationDTO().getCaseId());
        softly.assertThat(request.getCaseType()).isEqualTo(CaseType.EITHER_WAY);
        softly.assertThat(request.getAssessmentId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(request.getAssessmentType()).isEqualTo(AssessmentType.CCHARDSHIP);
        softly.assertThat(request.getDwpResult()).isNull();
        softly.assertThat(request.getRepDecision()).isNull();
        softly.assertThat(request.getCcRepDecision()).isNull();
        softly.assertThat(request.getMagsOutcome())
                .isEqualTo(
                        workflowRequest.getApplicationDTO().getMagsOutcomeDTO().getOutcome());
        softly.assertThat(request.getRequestSource()).isEqualTo(RequestSource.HARDSHIP);
        softly.assertAll();
    }

    @Test
    void giveAValidIOJAppeal_whenBuildIOJIsInvoked_thenMappingIsCorrect() {

        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getIojAppeal()
                .setIojId(TestModelDataBuilder.REP_ID.longValue());
        workflowRequest.getApplicationDTO().setIojResultNote(MeansAssessmentResult.PASS.toString());
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getIojAppeal()
                .setAppealReason(IOJDecisionReasonDTO.builder()
                        .code(MeansAssessmentResult.PASS.toString())
                        .build());
        workflowRequest.getApplicationDTO().setDateCreated(TestModelDataBuilder.DATE_COMPLETED);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        Ioj ioj = mapper.buildIOJ(workflowRequest, repOrderDTO);
        softly.assertThat(ioj.getIojId()).isEqualTo(TestModelDataBuilder.REP_ID);
        softly.assertThat(ioj.getIojResult()).isEqualTo(MeansAssessmentResult.PASS.toString());
        softly.assertThat(ioj.getIojReason()).isEqualTo(MeansAssessmentResult.PASS.toString());
        softly.assertThat(ioj.getIojAppealResult()).isEqualTo(IojAppealResult.PASS);
        softly.assertThat(ioj.getIojAssessorName()).isEqualTo(ASSESSOR_NAME);
        softly.assertAll();
    }

    @Test
    void giveAInvalidValidIOJAppeal_whenBuildIOJIsInvoked_thenMappingIsCorrect() {

        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        Ioj ioj = mapper.buildIOJ(workflowRequest, repOrderDTO);

        softly.assertThat(ioj.getIojId()).isEqualTo(LEGACY_APPEAL_ID);
        softly.assertThat(ioj.getIojResult()).isEqualTo(MeansAssessmentResult.PASS.toString());
        softly.assertThat(ioj.getIojReason()).isNull();
        softly.assertThat(ioj.getIojAppealResult()).isEqualTo(IojAppealResult.PASS);
        softly.assertThat(ioj.getIojAssessorName()).isEqualTo(ASSESSOR_NAME);
        softly.assertAll();
    }

    @Test
    void giveAValidPassport_whenBuildPassportIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest.getApplicationDTO().getPassportedDTO().setDate(new Date());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        Passport passport = mapper.buildPassport(workflowRequest, repOrderDTO);
        softly.assertThat(passport.getPassportId()).isEqualTo(TestModelDataBuilder.PASSPORTED_ID);
        softly.assertThat(passport.getPassportResult()).isEqualTo(PassportResult.FAIL);
        softly.assertThat(passport.getPassportCreatedDate()).isNotNull();
        softly.assertThat(passport.getPassportAssessorName()).isEqualTo(ASSESSOR_NAME);
        softly.assertAll();
    }

    @Test
    void giveAInValidPassportId_whenBuildPassportIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest.getApplicationDTO().getPassportedDTO().setDate(new Date());
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();
        repOrderDTO.getPassportAssessments().get(0).setId(ASSESSMENT_SUMMARY_ID);

        Passport passport = mapper.buildPassport(workflowRequest, repOrderDTO);
        softly.assertThat(passport.getPassportId()).isEqualTo(TestModelDataBuilder.PASSPORTED_ID);
        softly.assertThat(passport.getPassportResult()).isEqualTo(PassportResult.FAIL);
        softly.assertThat(passport.getPassportCreatedDate()).isNotNull();
        softly.assertThat(passport.getPassportAssessorName()).isNull();
        softly.assertAll();
    }

    @Test
    void giveAEmptyPassport_whenBuildPassportIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest.getApplicationDTO().getPassportedDTO().setPassportedId(null);
        workflowRequest.getApplicationDTO().getPassportedDTO().setResult(null);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.getTestRepOrderDTO(workflowRequest.getApplicationDTO());

        Passport passport = mapper.buildPassport(workflowRequest, repOrderDTO);
        softly.assertThat(passport.getPassportId()).isNull();
        softly.assertThat(passport.getPassportResult()).isNull();
        softly.assertThat(passport.getPassportCreatedDate()).isNull();
        softly.assertThat(passport.getPassportAssessorName()).isNull();
        softly.assertAll();
    }

    @Test
    void giveAValidFullAssessment_whenBuildMeansAssessmentIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        MeansAssessment meansAssessment = mapper.buildMeansAssessment(workflowRequest, repOrderDTO);
        softly.assertThat(meansAssessment.getMeansAssessmentId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(meansAssessment.getMeansAssessmentStatus()).isEqualTo(AssessmentStatusDTO.COMPLETE);
        softly.assertThat(meansAssessment.getMeansAssessmentResult()).isEqualTo(MeansAssessmentResult.PASS);
        softly.assertThat(meansAssessment.getMeansAssessmentCreatedDate()).isNotNull();
        softly.assertThat(meansAssessment.getMeansAssessorName()).isEqualTo(ASSESSOR_NAME);
        softly.assertAll();
    }

    @Test
    void giveAValidInitialAssessment_whenBuildMeansAssessmentIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setFullAvailable(false);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();

        MeansAssessment meansAssessment = mapper.buildMeansAssessment(workflowRequest, repOrderDTO);
        softly.assertThat(meansAssessment.getMeansAssessmentId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(meansAssessment.getMeansAssessmentStatus()).isEqualTo(AssessmentStatusDTO.COMPLETE);
        softly.assertThat(meansAssessment.getMeansAssessmentResult()).isEqualTo(MeansAssessmentResult.FAIL);
        softly.assertThat(meansAssessment.getMeansAssessmentCreatedDate()).isNull();
        softly.assertThat(meansAssessment.getMeansAssessorName()).isEqualTo(ASSESSOR_NAME);
        softly.assertAll();
    }

    @Test
    void giveAInValidAssessmentId_whenBuildMeansAssessmentIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .setFullAvailable(false);
        RepOrderDTO repOrderDTO = TestModelDataBuilder.buildRepOrderDTOWithAssessorName();
        repOrderDTO.getFinancialAssessments().get(0).setId(ASSESSMENT_SUMMARY_ID);

        MeansAssessment meansAssessment = mapper.buildMeansAssessment(workflowRequest, repOrderDTO);
        softly.assertThat(meansAssessment.getMeansAssessmentId()).isEqualTo(Constants.FINANCIAL_ASSESSMENT_ID);
        softly.assertThat(meansAssessment.getMeansAssessmentStatus()).isEqualTo(AssessmentStatusDTO.COMPLETE);
        softly.assertThat(meansAssessment.getMeansAssessmentResult()).isEqualTo(MeansAssessmentResult.FAIL);
        softly.assertThat(meansAssessment.getMeansAssessmentCreatedDate()).isNull();
        softly.assertThat(meansAssessment.getMeansAssessorName()).isNull();
        softly.assertAll();
    }

    @Test
    void giveAValidHardship_whenBuildHardshipIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);

        Hardship hardship = mapper.buildHardship(workflowRequest);
        softly.assertThat(hardship.getHardshipId()).isEqualTo(Constants.HARDSHIP_REVIEW_ID);
        softly.assertThat(hardship.getHardshipType()).isEqualTo(HardshipType.CCHARDSHIP);
        softly.assertThat(hardship.getHardshipResult()).isEqualTo(HardshipResult.PASS);
        softly.assertAll();
    }

    @Test
    void giveAInvalidHardship_whenBuildHardshipIsInvoked_thenMappingIsCorrect() {
        WorkflowRequest workflowRequest =
                TestModelDataBuilder.buildWorkflowRequestWithCCHardship(CourtType.CROWN_COURT);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setId(null);
        workflowRequest
                .getApplicationDTO()
                .getAssessmentDTO()
                .getFinancialAssessmentDTO()
                .getHardship()
                .getCrownCourtHardship()
                .setReviewResult(null);

        Hardship hardship = mapper.buildHardship(workflowRequest);
        softly.assertThat(hardship.getHardshipId()).isNull();
        softly.assertThat(hardship.getHardshipType()).isEqualTo(HardshipType.CCHARDSHIP);
        softly.assertThat(hardship.getHardshipResult()).isNull();
        softly.assertAll();
    }
}
