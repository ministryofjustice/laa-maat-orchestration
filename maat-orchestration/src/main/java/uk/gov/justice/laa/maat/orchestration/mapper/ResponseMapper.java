package uk.gov.justice.laa.maat.orchestration.mapper;

public interface ResponseMapper<T, D> {

    void toDto(final T model, final D dto);
}
