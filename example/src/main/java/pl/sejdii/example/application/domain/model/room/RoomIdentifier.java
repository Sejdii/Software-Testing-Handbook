package pl.sejdii.example.application.domain.model.room;

import java.util.UUID;

public record RoomIdentifier(String value) {

  public static RoomIdentifier random() {
    return new RoomIdentifier(UUID.randomUUID().toString());
  }
}
