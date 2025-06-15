package pl.sejdii.example.adapter.out.postgres;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort;

@Component
@Profile("!in-memory")
@RequiredArgsConstructor
class ParticipantPostgresAdapter implements InsertReservationParticipantPort {

  private final ReservationParticipantRepository repository;

  @Override
  public void insert(ReservationParticipant reservationParticipant) {
    ReservationParticipantEntity entity = convertToEntity(reservationParticipant);

    repository.save(entity);
  }

  private ReservationParticipantEntity convertToEntity(
      ReservationParticipant reservationParticipant) {
    ReservationParticipantEntity entity = new ReservationParticipantEntity();
    entity.setIdentifier(reservationParticipant.identifier().value());
    entity.setFirstName(reservationParticipant.firstName());
    entity.setSurname(reservationParticipant.surname());
    return entity;
  }
}
