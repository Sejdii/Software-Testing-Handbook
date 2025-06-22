package pl.sejdii.example.application.port.out;

import pl.sejdii.example.application.domain.model.room.Room;

public interface UpdateRoomPort {

  void update(Room room);
}
