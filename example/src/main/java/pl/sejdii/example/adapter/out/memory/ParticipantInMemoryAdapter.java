package pl.sejdii.example.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort;

@Component
@Profile("in-memory")
public class ParticipantInMemoryAdapter implements InsertReservationParticipantPort {

  private final List<ReservationParticipant> reservationParticipants = new ArrayList<>();

  @Override
  public void insert(ReservationParticipant reservationParticipant) {
    reservationParticipants.add(reservationParticipant);
  }

  public Optional<ReservationParticipant> findBy(ReservationParticipantIdentifier identifier) {
    return reservationParticipants.stream()
        .filter(it -> it.identifier().equals(identifier))
        .findAny();
  }
}
