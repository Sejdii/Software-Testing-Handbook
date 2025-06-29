package pl.sejdii.example.application.port.in;

import java.time.LocalDateTime;
import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

public interface ReserveRoomUseCase {

  ReservationIdentifier reserve(Command command);

  record Command(
      ReservationParticipantIdentifier reservationOwnerIdentifier,
      RoomIdentifier roomIdentifier,
      ReservationPeriod period,
      int numberOfParticipants) {

    public Command {
      if (period.from().isBefore(LocalDateTime.now())) {
        throw new ValidationException("Reservation cannot start in the past");
      }
    }
  }
}
