package pl.sejdii.example.adapter.out.kafka;

import java.time.LocalDateTime;

record RoomReservedMessage(
    String roomIdentifier,
    String reservationIdentifier,
    LocalDateTime startTime,
    LocalDateTime endTime) {}
