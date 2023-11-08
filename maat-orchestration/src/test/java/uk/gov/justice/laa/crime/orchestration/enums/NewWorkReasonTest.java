package uk.gov.justice.laa.crime.orchestration.enums;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class NewWorkReasonTest {

    @Test
    void givenABlankString_whenGetFromIsInvoked_thenNullIsReturned() {
        assertThat(NewWorkReason.getFrom(null)).isNull();
    }

    @Test
    void givenValidResultString_whenGetFromIsInvoked_thenCorrectEnumIsReturned() {
        assertThat(NewWorkReason.getFrom("NEW")).isEqualTo(NewWorkReason.NEW);
    }

    @Test
    void givenInvalidResultString_whenGetFromIsInvoked_thenExceptionIsThrown() {
        assertThatThrownBy(
                () -> NewWorkReason.getFrom("MOCK_RESULT_STRING")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenValidInput_ValidateEnumValues() {
        assertThat("NEW").isEqualTo(NewWorkReason.NEW.getCode());
        assertThat("New").isEqualTo(NewWorkReason.NEW.getDescription());
        assertThat("HARDIOJ").isEqualTo(NewWorkReason.NEW.getType());
    }

}
