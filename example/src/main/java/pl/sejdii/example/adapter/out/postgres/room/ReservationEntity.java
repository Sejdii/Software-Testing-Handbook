package pl.sejdii.example.adapter.out.postgres.room;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "reservation")
class ReservationEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String identifier;
  private String reservationOwnerIdentifier;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private int numberOfParticipants;

  @ManyToOne(fetch = FetchType.LAZY)
  private RoomEntity room;

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ReservationEntity reservation)) return false;
    return identifier != null && identifier.equals(reservation.getIdentifier());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
