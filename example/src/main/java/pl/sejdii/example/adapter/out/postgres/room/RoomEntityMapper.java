package pl.sejdii.example.adapter.out.postgres.room;

import java.util.List;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

@Component
class RoomEntityMapper {

  Room map(RoomEntity roomEntity) {
    Room room =
        new Room(new RoomIdentifier(roomEntity.getIdentifier()), roomEntity.getNumberOfSeats());

    List<Reservation> reservations =
        roomEntity.getActiveReservations().stream().map(this::mapReservation).toList();

    for (Reservation reservation : reservations) {
      room.reserve(reservation);
    }

    return room;
  }

  RoomEntity map(Room room) {
    RoomEntity roomEntity = new RoomEntity();
    roomEntity.setIdentifier(room.getIdentifier().value());
    roomEntity.setNumberOfSeats(room.getNumberOfSeats());

    List<ReservationEntity> reservationEntities =
        room.getActiveReservations().stream().map(this::mapReservation).toList();

    for (ReservationEntity reservationEntity : reservationEntities) {
      roomEntity.addReservation(reservationEntity);
    }

    return roomEntity;
  }

  private Reservation mapReservation(ReservationEntity entity) {
    return new Reservation(
        new ReservationIdentifier(entity.getIdentifier()),
        new ReservationParticipantIdentifier(entity.getReservationOwnerIdentifier()),
        new ReservationPeriod(entity.getStartTime(), entity.getEndTime()),
        new RoomIdentifier(entity.getRoom().getIdentifier()),
        entity.getNumberOfParticipants());
  }

  private ReservationEntity mapReservation(Reservation reservation) {
    ReservationEntity entity = new ReservationEntity();
    entity.setIdentifier(reservation.identifier().value());
    entity.setReservationOwnerIdentifier(reservation.reservationOwnerIdentifier().value());
    entity.setStartTime(reservation.period().from());
    entity.setEndTime(reservation.period().to());
    entity.setNumberOfParticipants(reservation.numberOfParticipants());

    return entity;
  }
}
