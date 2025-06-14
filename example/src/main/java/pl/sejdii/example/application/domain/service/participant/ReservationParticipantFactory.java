package pl.sejdii.example.application.domain.service.participant;

import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase;

class ReservationParticipantFactory {

  private final ReservationParticipantIdentifierGenerator reservationParticipantIdentifierGenerator;

  ReservationParticipantFactory(
      ReservationParticipantIdentifierGenerator reservationParticipantIdentifierGenerator) {
    this.reservationParticipantIdentifierGenerator = reservationParticipantIdentifierGenerator;
  }

  ReservationParticipant create(CreateReservationParticipantUseCase.Command command) {
    ReservationParticipantIdentifier reservationParticipantIdentifier =
        reservationParticipantIdentifierGenerator.generate();

    return new ReservationParticipant(
        reservationParticipantIdentifier, command.firstName(), command.secondName());
  }
}
