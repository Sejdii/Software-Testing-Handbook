package pl.sejdii.example.application.port.out;

import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;

public interface InsertReservationParticipantPort {

  void insert(ReservationParticipant reservationParticipant);
}
