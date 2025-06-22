package pl.sejdii.example.application.domain.service.room;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomNotFoundException;
import pl.sejdii.example.application.port.in.ReserveRoomUseCase;
import pl.sejdii.example.application.port.out.FindRoomPort;
import pl.sejdii.example.application.port.out.SendRoomReservedMessagePort;
import pl.sejdii.example.application.port.out.UpdateRoomPort;

@RequiredArgsConstructor
class ReserveRoomService implements ReserveRoomUseCase {

  private final FindRoomPort findRoomPort;
  private final UpdateRoomPort updateRoomPort;
  private final SendRoomReservedMessagePort sendRoomReservedMessagePort;

  @Override
  @Transactional
  public ReservationIdentifier reserve(Command command) {
    Room room =
        findRoomPort
            .find(command.roomIdentifier())
            .orElseThrow(() -> new RoomNotFoundException(command.roomIdentifier()));

    Reservation reservation =
        new Reservation(
            ReservationIdentifier.random(),
            command.reservationOwnerIdentifier(),
            command.period(),
            command.roomIdentifier(),
            command.numberOfParticipants());

    room.reserve(reservation);
    updateRoomPort.update(room);

    sendRoomReservedMessagePort.send(room, reservation);

    return reservation.getIdentifier();
  }
}
