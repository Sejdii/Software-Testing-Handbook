package pl.sejdii.example.application.domain.model.room;

import jakarta.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;

@RequiredArgsConstructor
public class Room {

  @Getter private final RoomIdentifier identifier;
  @Getter private final int numberOfSeats;

  private final List<Reservation> activeReservations = new ArrayList<>();

  @Nullable @Getter @Setter private Integer technicalId;

  public Room(int numberOfSeats) {
    this.identifier = RoomIdentifier.random();
    this.numberOfSeats = numberOfSeats;
  }

  public void reserve(Reservation reservation) {
    if (!reservation.getRoomIdentifier().equals(identifier)) {
      throw new IllegalArgumentException("Room identifier does not match");
    }

    if (isTaken(reservation.getPeriod())) {
      throw new ValidationException("Room is already taken");
    }

    if (numberOfSeats < reservation.getNumberOfParticipants()) {
      throw new ValidationException("Room is not large enough");
    }

    activeReservations.add(reservation);
  }

  public boolean isTaken(ReservationPeriod period) {
    return activeReservations.stream().anyMatch(it -> it.getPeriod().overlaps(period));
  }

  public List<Reservation> getActiveReservations() {
    return List.copyOf(activeReservations);
  }
}
