package uk.gov.justice.laa.crime.orchestration.mapper;

import org.springframework.stereotype.Component;
import uk.gov.justice.laa.crime.orchestration.dto.maat.OutcomeDTO;
import uk.gov.justice.laa.crime.orchestration.enums.CrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.model.common.ApiCrownCourtOutcome;
import uk.gov.justice.laa.crime.orchestration.util.DateUtil;

@Component
public class CrownCourtMapper {

    protected ApiCrownCourtOutcome outcomeDtoToCrownCourtOutcome(OutcomeDTO outcomeDTO) {
        return new ApiCrownCourtOutcome()
                .withOutcome(CrownCourtOutcome.getFrom(outcomeDTO.getOutcome()))
                .withOutComeType(outcomeDTO.getOutComeType())
                .withDateSet(DateUtil.toLocalDateTime(outcomeDTO.getDateSet()))
                .withDescription(outcomeDTO.getDescription());
    }
}
