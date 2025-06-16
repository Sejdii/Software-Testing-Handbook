package pl.sejdii.example.application.domain.model.reservation;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import pl.sejdii.example.application.domain.model.ValidationException;

public record ReservationPeriod(LocalDateTime from, LocalDateTime to) {

  public ReservationPeriod {
    if (from.isAfter(to)) {
      throw new ValidationException("From date cannot be after to date");
    }

    if (from.isBefore(LocalDateTime.now())) {
      throw new ValidationException("From date cannot be before current date");
    }

    if (ChronoUnit.DAYS.between(from, to) > 3) {
      throw new ValidationException("Reservation period cannot be longer than 3 days");
    }
  }

  public boolean overlaps(ReservationPeriod other) {
    if (other.to.isEqual(from) || other.from.isEqual(to)) {
      return false;
    }

    return !(other.to.isBefore(from) || other.from.isAfter(to));
  }
}
