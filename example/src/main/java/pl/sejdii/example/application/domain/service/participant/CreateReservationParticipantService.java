package pl.sejdii.example.application.domain.service.participant;

import org.springframework.transaction.annotation.Transactional;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase;
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort;

class CreateReservationParticipantService implements CreateReservationParticipantUseCase {

  private final ReservationParticipantFactory reservationParticipantFactory;
  private final InsertReservationParticipantPort insertReservationParticipantPort;

  CreateReservationParticipantService(
      ReservationParticipantFactory reservationParticipantFactory,
      InsertReservationParticipantPort insertReservationParticipantPort) {
    this.reservationParticipantFactory = reservationParticipantFactory;
    this.insertReservationParticipantPort = insertReservationParticipantPort;
  }

  @Override
  @Transactional
  public ReservationParticipantIdentifier create(Command command) {
    ReservationParticipant reservationParticipant = reservationParticipantFactory.create(command);
    insertReservationParticipantPort.insert(reservationParticipant);

    return reservationParticipant.identifier();
  }
}
