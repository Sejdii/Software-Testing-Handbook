package pl.sejdii.example.adapter.out.postgres.room;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "room")
class RoomEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String identifier;

  private int numberOfSeats;

  @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<ReservationEntity> activeReservations = new ArrayList<>();

  void addReservation(ReservationEntity reservation) {
    activeReservations.add(reservation);
    reservation.setRoom(this);
  }
}
