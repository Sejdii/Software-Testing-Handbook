package pl.sejdii.example.application.port.out;

import pl.sejdii.example.application.domain.model.room.Room;

public interface InsertRoomPort {

  void insert(Room room);
}
