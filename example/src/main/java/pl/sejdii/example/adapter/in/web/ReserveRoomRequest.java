package pl.sejdii.example.adapter.in.web;

import java.time.LocalDateTime;

record ReserveRoomRequest(
    String reservationOwnerIdentifier,
    LocalDateTime startTime,
    LocalDateTime endTime,
    int numberOfParticipants) {}
