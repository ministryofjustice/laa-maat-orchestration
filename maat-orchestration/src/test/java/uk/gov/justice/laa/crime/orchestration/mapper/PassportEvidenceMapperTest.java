package uk.gov.justice.laa.crime.orchestration.mapper;

import uk.gov.justice.laa.crime.common.model.evidence.ApiGetPassportEvidenceResponse;
import uk.gov.justice.laa.crime.orchestration.data.builder.EvidenceDataBuilder;
import uk.gov.justice.laa.crime.orchestration.dto.maat.IncomeEvidenceSummaryDTO;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

@ExtendWith(SoftAssertionsExtension.class)
class PassportEvidenceMapperTest {

    @InjectSoftAssertions
    private SoftAssertions softly;

    private final PassportEvidenceMapper passportEvidenceMapper = new PassportEvidenceMapper();

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void givenValidEvidenceResponse_whenEvidenceResponseToEvidenceSummaryIsInvoked_thenMappingIsSuccessful(
            boolean withPartner) {
        IncomeEvidenceSummaryDTO expected = EvidenceDataBuilder.getIncomeEvidenceSummaryDTO(withPartner);
        ApiGetPassportEvidenceResponse response = EvidenceDataBuilder.getApiGetPassportEvidenceResponse(withPartner);
        IncomeEvidenceSummaryDTO actual =
                passportEvidenceMapper.apiGetPassportEvidenceResponseToIncomeEvidenceSummaryDTO(response);

        softly.assertThat(actual)
                .usingRecursiveComparison()
                .ignoringCollectionOrder()
                .isEqualTo(expected);
        softly.assertAll();
    }
}
