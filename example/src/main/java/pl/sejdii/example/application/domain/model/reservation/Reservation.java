package pl.sejdii.example.application.domain.model.reservation;

import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

public record Reservation(
    ReservationParticipantIdentifier reservationOwnerIdentifier,
    ReservationPeriod period,
    RoomIdentifier roomIdentifier,
    int numberOfParticipants) {}
