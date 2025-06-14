package pl.sejdii.example.user.application.domain;

public record UserIdentifier(String value) {

  public UserIdentifier {
    if (value == null || value.length() != 7) {
      throw new ValidationException("Invalid value %s".formatted(value));
    }
  }
}
