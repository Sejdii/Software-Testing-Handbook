package pl.sejdii.example.user.application.domain;

public record User(UserIdentifier identifier, String firstName, String surname) {

  public User {
    if (identifier == null) {
      throw new ValidationException("Identifier cannot be null");
    }

    if (firstName == null) {
      throw new ValidationException("First name cannot be null");
    }

    if (surname == null) {
      throw new ValidationException("Surname cannot be null");
    }
  }
}
