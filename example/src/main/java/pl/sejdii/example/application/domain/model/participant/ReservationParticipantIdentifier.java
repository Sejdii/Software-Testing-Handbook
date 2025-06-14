package pl.sejdii.example.application.domain.model.participant;

import pl.sejdii.example.application.domain.model.ValidationException;

public record ReservationParticipantIdentifier(String value) {

  public ReservationParticipantIdentifier {
    if (value == null || value.length() != 7) {
      throw new ValidationException("Invalid value %s".formatted(value));
    }
  }
}
