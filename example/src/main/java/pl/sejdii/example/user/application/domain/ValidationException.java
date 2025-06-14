package pl.sejdii.example.user.application.domain;

public class ValidationException extends RuntimeException {

  public ValidationException(String message) {
    super(message);
  }
}
