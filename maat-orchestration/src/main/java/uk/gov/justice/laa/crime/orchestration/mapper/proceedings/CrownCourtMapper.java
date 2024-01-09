package uk.gov.justice.laa.crime.orchestration.mapper.proceedings;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.OutcomeDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;

import java.util.Collection;
import java.util.List;

@Component
public class CrownCourtMapper {

    protected List<ApiCrownCourtOutcome> crownCourtSummaryDtoToCrownCourtOutcomes(
            CrownCourtSummaryDTO crownCourtSummary) {

        Collection<OutcomeDTO> outcomeDTOs = crownCourtSummary.getOutcomeDTOs();

        return outcomeDTOs.stream()
                .map(outcomeDTO ->
                        new ApiCrownCourtOutcome()
                                .withOutcome(CrownCourtOutcome.getFrom(outcomeDTO.getOutcome()))
                                .withOutComeType(outcomeDTO.getOutComeType())
                                .withDateSet(DateUtil.toLocalDateTime(outcomeDTO.getDateSet()))
                                .withDescription(outcomeDTO.getDescription())
                ).toList();
    }
}
