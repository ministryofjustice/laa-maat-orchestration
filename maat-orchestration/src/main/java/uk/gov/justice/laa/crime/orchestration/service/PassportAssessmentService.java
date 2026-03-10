package uk.gov.justice.laa.crime.orchestration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.crime.common.model.passported.ApiGetPassportedAssessmentResponse;
import uk.gov.justice.laa.crime.common.model.passported.DeclaredBenefit;
import uk.gov.justice.laa.crime.enums.BenefitRecipient;
import uk.gov.justice.laa.crime.orchestration.dto.maat.PassportedDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat_api.ApplicantDTO;
import uk.gov.justice.laa.crime.orchestration.mapper.PassportAssessmentMapper;
import uk.gov.justice.laa.crime.orchestration.service.api.AssessmentApiService;
import uk.gov.justice.laa.crime.orchestration.service.api.MaatCourtDataApiService;

@Service
@RequiredArgsConstructor
public class PassportAssessmentService {

    private final AssessmentApiService assessmentApiService;
    private final MaatCourtDataApiService maatCourtDataApiService;
    private final PassportAssessmentMapper passportAssessmentMapper;

    private boolean hasPartnerBenefit(DeclaredBenefit declaredBenefit) {
        return declaredBenefit != null && BenefitRecipient.PARTNER.equals(
            declaredBenefit.getBenefitRecipient());
    }

    public PassportedDTO find(int legacyId) {
        ApiGetPassportedAssessmentResponse response = assessmentApiService.findPassportAssessment(
            legacyId);

        ApplicantDTO applicantDTO =
            hasPartnerBenefit(response.getDeclaredBenefit()) ? maatCourtDataApiService.getApplicant(
                response.getDeclaredBenefit().getLegacyPartnerId()) : null;

        return passportAssessmentMapper.apiGetPassportedAssessmentResponseToPassportedDTO(response,
            applicantDTO);
    }
}