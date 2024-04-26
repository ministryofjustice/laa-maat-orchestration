package uk.gov.justice.laa.crime.orchestration.mapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.enums.CaseType;
import uk.gov.justice.laa.crime.enums.PassportAssessmentResult;
import uk.gov.justice.laa.crime.enums.ReviewResult;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.*;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.PassportAssessmentDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.RepOrderDTO;
import uk.gov.justice.laa.crime.orchestration.enums.cat.AssessmentType;
import uk.gov.justice.laa.crime.orchestration.enums.cat.HardshipType;
import uk.gov.justice.laa.crime.orchestration.enums.cat.MeanAssessmentResult;
import uk.gov.justice.laa.crime.orchestration.enums.cat.RequestSource;
import uk.gov.justice.laa.crime.orchestration.model.application_tracking.*;

import java.util.Arrays;
import java.util.stream.Collectors;

import static uk.gov.justice.laa.crime.util.DateUtil.toLocalDateTime;

@Component
public class ApplicationTrackingMapper {

    public ApiCrimeApplicationTrackingRequest build(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {

        ApiCrimeApplicationTrackingRequest request = new ApiCrimeApplicationTrackingRequest();
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        PassportedDTO passportedDTO = application.getPassportedDTO();
        RepOrderDecisionDTO repOrderDecisionDTO = application.getRepOrderDecision();
        CrownCourtOverviewDTO crownCourtOverviewDTO = application.getCrownCourtOverviewDTO();
        CrownCourtSummaryDTO crownCourtSummaryDTO = crownCourtOverviewDTO.getCrownCourtSummaryDTO();

        request.setUsn(financialAssessmentDTO.getUsn() != null ? financialAssessmentDTO.getUsn().intValue() : null);
        request.setMaatRef(application.getRepId().intValue());
        request.setActionKeyId(financialAssessmentDTO.getId().intValue());
        request.setCaseId(application.getCaseId());
        request.setCaseType(CaseType.getFrom(application.getCaseDetailsDTO().getCaseType()));
        request.setAssessmentId(financialAssessmentDTO.getId().intValue());
        request.setAssessmentType(AssessmentType.CCHARDSHIP);
        request.setDwpResult(passportedDTO.getDwpResult());
        request.setRepDecision(repOrderDecisionDTO.getDescription() != null ? repOrderDecisionDTO.getDescription().getValue() : null);
        request.setCcRepDecision(crownCourtSummaryDTO.getRepOrderDecision() != null ? crownCourtSummaryDTO.getRepOrderDecision().getValue() : null);
        request.setMagsOutcome(application.getMagsOutcomeDTO().getOutcome());
        request.setRequestSource(RequestSource.HARDSHIP);
        request.setUserCreated(workflowRequest.getUserDTO().getUserName());
        request.setIoj(buildIOJ(workflowRequest, repOrderDTO));
        request.setPassport(buildPassport(workflowRequest, repOrderDTO));
        request.setMeansAssessment(buildMeansAssessment(workflowRequest, repOrderDTO));
        request.setHardship(buildHardship(workflowRequest));

        return request;
    }

    protected Ioj buildIOJ(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        IOJAppealDTO iojAppealDTO = application.getAssessmentDTO().getIojAppeal();
        return new Ioj()
                .withIojId(null != iojAppealDTO.getIojId() ? iojAppealDTO.getIojId().intValue() : null)
                .withIojResult(application.getIojResult())
                .withIojReason(application.getIojResultNote())
                .withIojAppealResult(ReviewResult.getFrom(iojAppealDTO.getAppealDecisionResult()))
                .withIojAssessorName(getCapitalisedFullName(repOrderDTO.getUserCreatedEntity().getFirstName(), repOrderDTO.getUserCreatedEntity().getSurname()))
                .withAppCreatedDate(toLocalDateTime(application.getDateCreated()));

    }

    protected Passport buildPassport(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        PassportedDTO passportedDTO = application.getPassportedDTO();
        PassportAssessmentDTO passportAssessmentDTO = null;
        if(passportedDTO.getPassportedId() != null) {
            passportAssessmentDTO = repOrderDTO.getPassportAssessments()
                    .stream()
                    .filter(pa -> pa.getId() == passportedDTO.getPassportedId().intValue())
                    .findFirst()
                    .orElse(null);
        }

        return new Passport()
                .withPassportId(passportedDTO.getPassportedId() != null ? passportedDTO.getPassportedId().intValue() : null)
                .withPassportResult(PassportAssessmentResult.getFrom(passportedDTO.getResult()))
                .withPassportAssessorName( (null != passportAssessmentDTO) ?
                        getCapitalisedFullName(passportAssessmentDTO.getUserCreatedEntity().getFirstName(),
                                passportAssessmentDTO.getUserCreatedEntity().getSurname()) : null)
                .withPassportCreatedDate(passportedDTO.getDate() != null ? toLocalDateTime(passportedDTO.getDate()) : null);
    }

    protected MeansAssessment buildMeansAssessment(WorkflowRequest workflowRequest, RepOrderDTO repOrderDTO) {
        ApplicationDTO application = workflowRequest.getApplicationDTO();
        FinancialAssessmentDTO financialAssessmentDTO = application.getAssessmentDTO().getFinancialAssessmentDTO();
        MeansAssessment assessment = new MeansAssessment();
        assessment.setMeansAssessmentId(financialAssessmentDTO.getId().intValue());
        uk.gov.justice.laa.crime.orchestration.dto.maat_api.FinancialAssessmentDTO fa = repOrderDTO.getFinancialAssessments()
                .stream()
                .filter(pa -> pa.getId() == financialAssessmentDTO.getId().intValue())
                .findFirst()
                .orElse(null);
        assessment.setMeansAssessorName( (null !=  fa) ? getCapitalisedFullName(fa.getUserCreatedEntity().getFirstName(),
                fa.getUserCreatedEntity().getSurname()) : null);

        if (financialAssessmentDTO.getFullAvailable() !=null && financialAssessmentDTO.getFullAvailable()) {
            assessment.setMeansAssessmentStatus(financialAssessmentDTO.getFull().getAssessmnentStatusDTO().getStatus());
            assessment.setMeansAssessmentResult(MeanAssessmentResult.getFrom(financialAssessmentDTO.getFull().getResult()));
            assessment.setMeansAssessmentCreatedDate(toLocalDateTime(financialAssessmentDTO.getFull().getAssessmentDate()));
        } else {
            assessment.setMeansAssessmentStatus(financialAssessmentDTO.getInitial().getAssessmnentStatusDTO().getStatus());
            assessment.setMeansAssessmentResult(MeanAssessmentResult.getFrom(financialAssessmentDTO.getInitial().getResult()));
            assessment.setMeansAssessmentCreatedDate(toLocalDateTime(financialAssessmentDTO.getInitial().getAssessmentDate()));
        }

        return assessment;
    }

    protected Hardship buildHardship(WorkflowRequest workflowRequest) {

        ApplicationDTO application = workflowRequest.getApplicationDTO();
        HardshipOverviewDTO hardshipOverviewDTO = application.getAssessmentDTO().getFinancialAssessmentDTO().getHardship();
        HardshipReviewDTO hardshipReviewDTO = hardshipOverviewDTO.getCrownCourtHardship();

        return new Hardship()
                .withHardshipId(hardshipReviewDTO.getId() != null ? hardshipReviewDTO.getId().intValue() : null)
                .withHardshipResult(ReviewResult.getFrom(hardshipReviewDTO.getReviewResult()))
                .withHardshipType(HardshipType.CCHARDSHIP);
    }

    public String getCapitalisedFullName(String... names) {
        return Arrays.stream(names)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .map(WordUtils::capitalize)
                .collect(Collectors.joining(StringUtils.SPACE));
    }
}
