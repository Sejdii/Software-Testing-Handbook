package pl.sejdii.example.application.domain.service.room;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.sejdii.example.application.port.in.ReserveRoomUseCase;
import pl.sejdii.example.application.port.out.FindRoomPort;
import pl.sejdii.example.application.port.out.UpdateRoomPort;

@Configuration
class RoomConfiguration {

  @Bean
  ReserveRoomUseCase reserveRoomService(FindRoomPort findRoomPort, UpdateRoomPort updateRoomPort) {
    return new ReserveRoomService(findRoomPort, updateRoomPort);
  }
}
