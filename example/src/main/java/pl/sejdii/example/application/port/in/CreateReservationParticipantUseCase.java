package pl.sejdii.example.application.port.in;

import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;

public interface CreateReservationParticipantUseCase {

  ReservationParticipantIdentifier create(Command command);

  record Command(String firstName, String secondName) {

    public Command {
      if (firstName == null) {
        throw new ValidationException("First name cannot be null");
      }

      if (secondName == null) {
        throw new ValidationException("Second name cannot be null");
      }
    }
  }
}
