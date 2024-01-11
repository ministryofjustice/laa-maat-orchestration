package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.CrownCourtSummaryDTO;
import uk.gov.justice.laa.crime.orchestration.dto.maat.OutcomeDTO;
import uk.gov.justice.laa.crime.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiRepOrderCrownCourtOutcome;
import uk.gov.justice.laa.crime.util.DateUtil;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

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

    protected List<OutcomeDTO> apiRepOrderCrownCourtOutcomesToOutcomeDtos(
            List<ApiRepOrderCrownCourtOutcome> repOrderCrownCourtOutcomes) {

        return repOrderCrownCourtOutcomes
                .stream()
                .map(x -> {
                    OutcomeDTO outcomeDTO = new OutcomeDTO();
                    outcomeDTO.setOutcome(x.getOutcome().getCode());
                    outcomeDTO.setDescription(x.getOutcome().getDescription());
                    outcomeDTO.setDateSet(DateUtil.toDate(x.getOutcomeDate()));
                    return outcomeDTO;
                }).collect(Collectors.toList());
    }
}
