package pl.sejdii.example.application.domain.model.room;

public class RoomNotFoundException extends RuntimeException {

  public RoomNotFoundException(RoomIdentifier roomIdentifier) {
    super("Room with identifier %s not found".formatted(roomIdentifier.value()));
  }
}
