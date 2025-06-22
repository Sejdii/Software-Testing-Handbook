package pl.sejdii.example.application.domain.model.reservation;

import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

public record Reservation(
    ReservationIdentifier identifier,
    ReservationParticipantIdentifier reservationOwnerIdentifier,
    ReservationPeriod period,
    RoomIdentifier roomIdentifier,
    int numberOfParticipants) {

  public Reservation {
    if (numberOfParticipants < 1) {
      throw new ValidationException("Number of participants must be at least 1");
    }
  }

  public Reservation(
      ReservationParticipantIdentifier reservationOwnerIdentifier,
      ReservationPeriod period,
      RoomIdentifier roomIdentifier,
      int numberOfParticipants) {
    this(
        ReservationIdentifier.random(),
        reservationOwnerIdentifier,
        period,
        roomIdentifier,
        numberOfParticipants);
  }
}
