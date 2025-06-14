package pl.sejdii.example.application.domain.service.participant;

import org.springframework.context.annotation.Bean;
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase;
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort;

class ReservationParticipantConfiguration {

  @Bean
  CreateReservationParticipantUseCase createReservationParticipantUseCase(
      InsertReservationParticipantPort insertReservationParticipantPort) {
    return new CreateReservationParticipantService(
        new ReservationParticipantFactory(new ReservationParticipantIdentifierGenerator()),
        insertReservationParticipantPort);
  }
}
