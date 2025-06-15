package pl.sejdii.example.application.domain.model.participant;

public final class ReservationParticipantTestFactory {

  public static final String IDENTIFIER = "EMP0001";
  public static final String FIRST_NAME = "Jan";
  public static final String SURNAME = "Kowalski";

  public static ReservationParticipant create() {
    return new ReservationParticipant(
        new ReservationParticipantIdentifier(IDENTIFIER), FIRST_NAME, SURNAME);
  }
}
