package pl.sejdii.example.application.domain.model.reservation;

import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReservationPeriodTestFactory {

  public static ReservationPeriod createPeriodBetween(int startHour, int endHour) {
    return new ReservationPeriod(nextDayAt(startHour), nextDayAt(endHour));
  }

  public static ReservationPeriod createPeriodBetween(
      int startHour, int startMinutes, int endHour, int endMinutes) {
    return new ReservationPeriod(
        nextDayAt(startHour, startMinutes), nextDayAt(endHour, endMinutes));
  }

  private static LocalDateTime nextDayAt(int hour) {
    return LocalDateTime.now().plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
  }

  private static LocalDateTime nextDayAt(int hour, int minutes) {
    return nextDayAt(hour).withMinute(minutes);
  }
}
