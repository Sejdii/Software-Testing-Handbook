package pl.sejdii.example.adapter.out.memory;

import java.util.*;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;
import pl.sejdii.example.application.port.out.FindRoomPort;
import pl.sejdii.example.application.port.out.InsertRoomPort;
import pl.sejdii.example.application.port.out.UpdateRoomPort;

@Component
@Profile("in-memory")
public class RoomInMemoryAdapter implements FindRoomPort, InsertRoomPort, UpdateRoomPort {

  private final Set<Room> rooms = new HashSet<>();

  @Override
  public Optional<Room> find(RoomIdentifier roomIdentifier) {
    return rooms.stream().filter(it -> it.getIdentifier().equals(roomIdentifier)).findAny();
  }

  @Override
  public void insert(Room room) {
    rooms.add(room);
  }

  @Override
  public void update(Room room) {
    // implementation is not needed for in-memory adapter
  }
}
