package pl.sejdii.example.application.port.out;

import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.room.Room;

public interface SendRoomReservedMessagePort {

  void send(Room room, Reservation newReservation);
}
