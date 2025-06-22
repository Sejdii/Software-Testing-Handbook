package pl.sejdii.example.adapter.out.kafka;

import java.time.LocalDateTime;

public record RoomReservedMessage(
    String roomIdentifier,
    String reservationIdentifier,
    LocalDateTime startTime,
    LocalDateTime endTime) {}
