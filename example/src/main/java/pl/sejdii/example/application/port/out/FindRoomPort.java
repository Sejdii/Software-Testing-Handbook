package pl.sejdii.example.application.port.out;

import java.util.Optional;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

public interface FindRoomPort {

  Optional<Room> find(RoomIdentifier roomIdentifier);
}
