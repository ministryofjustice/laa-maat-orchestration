package uk.gov.justice.laa.crime.orchestration.exception;

/**
 * <class>MAATServerException</class>
 */
public class StoredProcedureValidationException extends RuntimeException {

  /**
   * Constructs an instance of <code>MAATServerException</code>.
   */
  public StoredProcedureValidationException() {
    super();
  }

  /**
   * Constructs an instance of <code>MAATServerException</code> with
   * the specified detail message.
   * @param message The detail message.
   */
  public StoredProcedureValidationException(String message) {
    super(message);
  }

}
