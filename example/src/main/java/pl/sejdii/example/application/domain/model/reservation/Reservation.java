package pl.sejdii.example.application.domain.model.reservation;

import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

@Getter
@EqualsAndHashCode
public final class Reservation {

  private final ReservationIdentifier identifier;
  private final ReservationParticipantIdentifier reservationOwnerIdentifier;
  private final ReservationPeriod period;
  private final RoomIdentifier roomIdentifier;
  private final int numberOfParticipants;

  @Nullable @Getter @Setter private Integer technicalId;

  public Reservation(
      ReservationIdentifier identifier,
      ReservationParticipantIdentifier reservationOwnerIdentifier,
      ReservationPeriod period,
      RoomIdentifier roomIdentifier,
      int numberOfParticipants) {
    if (numberOfParticipants < 1) {
      throw new ValidationException("Number of participants must be at least 1");
    }
    this.identifier = identifier;
    this.reservationOwnerIdentifier = reservationOwnerIdentifier;
    this.period = period;
    this.roomIdentifier = roomIdentifier;
    this.numberOfParticipants = numberOfParticipants;
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
