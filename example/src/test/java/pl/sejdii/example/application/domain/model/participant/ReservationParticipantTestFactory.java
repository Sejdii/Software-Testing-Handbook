package pl.sejdii.example.application.domain.model.participant;

public final class ReservationParticipantTestFactory {

  public static final String IDENTIFIER_AS_STRING = "EMP0001";
  public static final ReservationParticipantIdentifier IDENTIFIER =
      new ReservationParticipantIdentifier(IDENTIFIER_AS_STRING);
  public static final String FIRST_NAME = "Jan";
  public static final String SURNAME = "Kowalski";

  public static ReservationParticipant create() {
    return new ReservationParticipant(IDENTIFIER, FIRST_NAME, SURNAME);
  }
}
