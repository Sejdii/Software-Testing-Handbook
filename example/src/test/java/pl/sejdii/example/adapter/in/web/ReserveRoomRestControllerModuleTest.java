package pl.sejdii.example.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.sejdii.example.application.domain.model.reservation.ReservationPeriodTestFactory.createPeriodBetween;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.json.JsonAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.sejdii.example.ModuleTest;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;
import pl.sejdii.example.application.port.out.FindRoomPort;

class ReserveRoomRestControllerModuleTest extends ModuleTest {

  private static final String ROOM_IDENTIFIER = "aedd07ed-efd5-4720-99b0-7ae731df43ff";

  @Autowired private MockMvc mockMvc;

  @Autowired private FindRoomPort findRoomPort;

  @Autowired private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired private ConsumerFactory<String, String> consumerFactory;

  @Autowired private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    kafkaTemplate.setConsumerFactory(consumerFactory);
  }

  @Test
  @Sql("insert-room.sql")
  void shouldReserveRoom() throws Exception {
    // given
    String jsonRequestBody = getJsonRequestBody();

    // when
    ResultActions resultActions = reserveRoom(jsonRequestBody);

    // then
    resultActions.andExpect(status().isCreated());
    assertReservationIsCreated();

    String reservationIdentifier = getIdentifierFromResponse(resultActions);
    assertRoomReservedMessageWasSend(reservationIdentifier);
  }

  private ResultActions reserveRoom(String jsonRequestBody) throws Exception {
    return mockMvc.perform(
        post("/rooms/{roomIdentifier}/reservations", ROOM_IDENTIFIER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));
  }

  private static String getJsonRequestBody() {
    @Language("JSON")
    String requestBody =
        """
                {
                  "reservationOwnerIdentifier": "EMP0001",
                  "startTime": "%s",
                  "endTime": "%s",
                  "numberOfParticipants": 4
                }
                """;

    ReservationPeriod reservationPeriod = createPeriodBetween(12, 13);
    return requestBody.formatted(
        reservationPeriod.from().toString(), reservationPeriod.to().toString());
  }

  private String getIdentifierFromResponse(ResultActions resultActions)
      throws UnsupportedEncodingException, JsonProcessingException {
    String responseBody = resultActions.andReturn().getResponse().getContentAsString();
    return objectMapper.readValue(responseBody, ReserveRoomResponse.class).reservationIdentifier();
  }

  private void assertReservationIsCreated() {
    Room room = findRoomPort.find(new RoomIdentifier(ROOM_IDENTIFIER)).orElseThrow();
    assertThat(room.getActiveReservations())
        .hasSize(1)
        .element(0)
        .satisfies(
            reservation -> {
              assertThat(reservation.getReservationOwnerIdentifier().value()).isEqualTo("EMP0001");
              assertThat(reservation.getPeriod()).isEqualTo(createPeriodBetween(12, 13));
              assertThat(reservation.getNumberOfParticipants()).isEqualTo(4);
            });
  }

  private void assertRoomReservedMessageWasSend(String reservationIdentifier) {
    ConsumerRecord<String, String> roomReservedMessage =
        kafkaTemplate.receive("roomReserved", 0, 0);
    ReservationPeriod reservationPeriod = createPeriodBetween(12, 13);
    assertThat(roomReservedMessage).isNotNull();
    JsonAssert.comparator(JSONCompareMode.STRICT)
        .assertIsMatch(
            roomReservedMessage.value(),
            """
                    {
                      "roomIdentifier": "aedd07ed-efd5-4720-99b0-7ae731df43ff",
                      "reservationIdentifier": "%s",
                      "startTime": "%s",
                      "endTime": "%s"
                    }
                """
                .formatted(
                    reservationIdentifier,
                    reservationPeriod.from().format(DateTimeFormatter.ISO_DATE_TIME),
                    reservationPeriod.to().format(DateTimeFormatter.ISO_DATE_TIME)));
  }
}
