package pl.sejdii.example.application.domain.service.participant;

import java.util.concurrent.atomic.AtomicInteger;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;

class ReservationParticipantIdentifierGenerator {

  private final AtomicInteger sequence = new AtomicInteger(0);

  ReservationParticipantIdentifier generate() {
    return new ReservationParticipantIdentifier("EMP000" + sequence.incrementAndGet());
  }
}
