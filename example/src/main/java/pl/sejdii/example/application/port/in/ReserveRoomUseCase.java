package pl.sejdii.example.application.port.in;

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
      int numberOfParticipants) {}
}
