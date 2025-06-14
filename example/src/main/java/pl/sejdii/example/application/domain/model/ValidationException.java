package pl.sejdii.example.application.domain.model;

public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}
