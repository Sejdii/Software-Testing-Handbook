package pl.sejdii.example.adapter.out.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.port.out.SendRoomReservedMessagePort;

@Component
@RequiredArgsConstructor
@Slf4j
class SendMessageKafkaAdapter implements SendRoomReservedMessagePort {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final ObjectMapper objectMapper;

  @Override
  public void send(Room room, Reservation newReservation) {
    RoomReservedMessage message =
        new RoomReservedMessage(
            room.getIdentifier().value(),
            newReservation.getIdentifier().value(),
            newReservation.getPeriod().from(),
            newReservation.getPeriod().to());

    log.info("Sending RoomReservedMessage message: {}", message);

    String messageJson = convertMessageToJson(message);
    kafkaTemplate.send("roomReserved", messageJson);
  }

  private String convertMessageToJson(RoomReservedMessage message) {
    try {
      return objectMapper.writeValueAsString(message);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }
}
