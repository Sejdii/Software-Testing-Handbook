package pl.sejdii.example.adapter.out.postgres.room;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;
import pl.sejdii.example.application.port.out.FindRoomPort;
import pl.sejdii.example.application.port.out.InsertRoomPort;
import pl.sejdii.example.application.port.out.UpdateRoomPort;

@Component
@Profile("!in-memory")
@RequiredArgsConstructor
class RoomPostgresAdapter implements FindRoomPort, InsertRoomPort, UpdateRoomPort {

  private final RoomRepository repository;
  private final RoomEntityMapper mapper;

  @Override
  public Optional<Room> find(RoomIdentifier roomIdentifier) {
    return repository.findByIdentifier(roomIdentifier.value()).map(mapper::map);
  }

  @Override
  public void insert(Room room) {
    repository.save(mapper.map(room));
  }

  @Override
  public void update(Room room) {
    repository.save(mapper.map(room));
  }
}
