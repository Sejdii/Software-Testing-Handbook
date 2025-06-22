package pl.sejdii.example.application.domain.model.reservation;

import java.util.UUID;

public record ReservationIdentifier(String value) {

  public static ReservationIdentifier random() {
    return new ReservationIdentifier(UUID.randomUUID().toString());
  }
}
