package uk.gov.justice.laa.crime.orchestration.mapper;

public interface RequestMapper<T, D> {

    T fromDto(final D dto);
}
