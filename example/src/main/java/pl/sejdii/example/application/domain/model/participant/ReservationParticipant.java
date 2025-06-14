package pl.sejdii.example.application.domain.model.participant;

import lombok.NonNull;

public record ReservationParticipant(
    @NonNull ReservationParticipantIdentifier identifier,
    @NonNull String firstName,
    @NonNull String surname) {}
