package pl.sejdii.example.application.domain.model.room;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;

public class Room {

  @Getter private final RoomIdentifier identifier;
  @Getter private final int numberOfSeats;

  private final List<Reservation> activeReservations = new ArrayList<>();

  public Room(int numberOfSeats) {
    this.identifier = RoomIdentifier.random();
    this.numberOfSeats = numberOfSeats;
  }

  public void reserve(Reservation reservation) {
    if (reservation.roomIdentifier() != identifier) {
      throw new IllegalArgumentException("Room identifier does not match");
    }

    if (isTaken(reservation.period())) {
      throw new ValidationException("Room is already taken");
    }

    if (numberOfSeats < reservation.numberOfParticipants()) {
      throw new ValidationException("Room is not large enough");
    }

    activeReservations.add(reservation);
  }

  public boolean isTaken(ReservationPeriod period) {
    return activeReservations.stream().anyMatch(it -> it.period().overlaps(period));
  }

  public List<Reservation> getActiveReservations() {
    return List.copyOf(activeReservations);
  }
}
