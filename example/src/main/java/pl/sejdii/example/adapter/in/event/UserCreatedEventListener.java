package pl.sejdii.example.adapter.in.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase;

@Slf4j
@Component
@RequiredArgsConstructor
class UserCreatedEventListener {

  private final CreateReservationParticipantUseCase useCase;

  @KafkaListener(id = "userCreatedEvent", topics = "userCreated")
  void listenOn(UserCreatedEvent event) {
    log.info("Incoming user created event: {}", event);
    useCase.create(
        new CreateReservationParticipantUseCase.Command(event.firstName(), event.surname()));
  }
}
