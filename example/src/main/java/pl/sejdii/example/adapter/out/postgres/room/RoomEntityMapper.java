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
    room.setTechnicalId(roomEntity.getId());

    List<Reservation> reservations =
        roomEntity.getActiveReservations().stream().map(this::mapReservation).toList();

    for (Reservation reservation : reservations) {
      room.reserve(reservation);
    }

    return room;
  }

  RoomEntity map(Room room) {
    RoomEntity roomEntity = new RoomEntity();
    roomEntity.setId(room.getTechnicalId());
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
    Reservation reservation =
        new Reservation(
            new ReservationIdentifier(entity.getIdentifier()),
            new ReservationParticipantIdentifier(entity.getReservationOwnerIdentifier()),
            new ReservationPeriod(entity.getStartTime(), entity.getEndTime()),
            new RoomIdentifier(entity.getRoom().getIdentifier()),
            entity.getNumberOfParticipants());

    reservation.setTechnicalId(entity.getId());

    return reservation;
  }

  private ReservationEntity mapReservation(Reservation reservation) {
    ReservationEntity entity = new ReservationEntity();
    entity.setId(reservation.getTechnicalId());
    entity.setIdentifier(reservation.getIdentifier().value());
    entity.setReservationOwnerIdentifier(reservation.getReservationOwnerIdentifier().value());
    entity.setStartTime(reservation.getPeriod().from());
    entity.setEndTime(reservation.getPeriod().to());
    entity.setNumberOfParticipants(reservation.getNumberOfParticipants());

    return entity;
  }
}
