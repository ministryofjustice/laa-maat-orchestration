package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiCreatePassportedAssessmentRequest;
import uk.gov.justice.laa.crime.common.model.passported.ApiCreatePassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.enums.BenefitRecipient;
import uk.gov.justice.laa.crime.orchestration.common.PartnerResolver;
import uk.gov.justice.laa.crime.orchestration.dto.WorkflowRequest;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.EvidenceApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PassportAssessmentService {

    private final AssessmentApiService assessmentApiService;
    private final MaatCourtDataApiService maatCourtDataApiService;
    private final EvidenceApiService evidenceApiService;
    private final PassportAssessmentMapper passportAssessmentMapper;

    private boolean hasPartnerBenefit(DeclaredBenefit declaredBenefit) {
        return declaredBenefit != null && BenefitRecipient.PARTNER.equals(declaredBenefit.getBenefitRecipient());
    }

    public PassportedDTO find(int id) {
        ApiGetPassportedAssessmentResponse assessment = assessmentApiService.findPassportAssessment(id);

        ApiGetPassportEvidenceResponse evidence = evidenceApiService.getPassportEvidence(id);

        DeclaredBenefit declaredBenefit = assessment.getDeclaredBenefit();
        ApplicantDTO partner = hasPartnerBenefit(declaredBenefit)
                ? maatCourtDataApiService.getApplicant(declaredBenefit.getLegacyPartnerId())
                : null;

        return passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(
                assessment, evidence, partner);
    }

    public Integer create(WorkflowRequest workflowRequest) {
        Optional<Integer> partnerId = Boolean.TRUE.equals(
                        workflowRequest.getApplicationDTO().getPassportedDTO().getBenefitClaimedByPartner())
                ? PartnerResolver.getPartnerId(workflowRequest.getApplicationDTO())
                : Optional.empty();

        ApiCreatePassportedAssessmentRequest createPassportRequest =
                passportAssessmentMapper.workflowRequestToApiCreatePassportedAssessmentRequest(
                        workflowRequest, partnerId);

        ApiCreatePassportedAssessmentResponse createPassportResponse =
                assessmentApiService.createPassportAssessment(createPassportRequest);

        return createPassportResponse.getLegacyAssessmentId();
    }
}
