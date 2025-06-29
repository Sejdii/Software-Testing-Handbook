package pl.sejdii.example.adapter.out.kafka;

import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.test.json.JsonAssert;
import org.springframework.test.json.JsonCompareMode;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

class SendMessageKafkaAdapterTestIT extends KafkaTestIT {

  private SendMessageKafkaAdapter adapter;

  @BeforeEach
  void setup() {
    adapter = new SendMessageKafkaAdapter(template, objectMapper);
  }

  @Test
  void shouldSendMessage(EmbeddedKafkaBroker embeddedKafkaBroker) {
    // given
    Room room = new Room(new RoomIdentifier("178caccd-d9cd-4e35-886d-2fc6e19f8ed5"), 5);
    Reservation reservation = createReservation(room);
    room.reserve(reservation);

    // when
    adapter.send(room, reservation);

    // then
    String messageContent = getMessageFromTopic(embeddedKafkaBroker, "roomReserved");
    JsonAssert.comparator(JsonCompareMode.STRICT)
        .assertIsMatch(
            """
                                {
                                    "roomIdentifier":"178caccd-d9cd-4e35-886d-2fc6e19f8ed5",
                                    "reservationIdentifier":"14c9caff-cdae-46b9-9448-86fa2ee7bfd1",
                                    "startTime":[2025,6,23,10,0],
                                    "endTime":[2025,6,23,12,0]
                                }
                                """,
            messageContent);
  }

  private static Reservation createReservation(Room room) {
    return new Reservation(
        new ReservationIdentifier("14c9caff-cdae-46b9-9448-86fa2ee7bfd1"),
        ReservationParticipantTestFactory.IDENTIFIER,
        new ReservationPeriod(
            LocalDateTime.parse("2025-06-23T10:00"), LocalDateTime.parse("2025-06-23T12:00")),
        room.getIdentifier(),
        3);
  }
}
