package pl.sejdii.example.adapter.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.assertArg;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static pl.sejdii.example.application.domain.model.reservation.ReservationPeriodTestFactory.createPeriodBetween;

import java.time.LocalDateTime;
import org.intellij.lang.annotations.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;
import pl.sejdii.example.application.domain.model.room.RoomNotFoundException;
import pl.sejdii.example.application.port.in.ReserveRoomUseCase;

@WebMvcTest
class ReserveRoomRestControllerTest {

  private static final ReservationIdentifier RESERVATION_IDENTIFIER =
      new ReservationIdentifier("a674fc0c-0d74-469d-beaa-266dd98b921c");
  private static final String ROOM_IDENTIFIER = "0fd6c5e5-ff2c-4b7a-aa06-2a6581d0fe9b";

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ReserveRoomUseCase useCase;

  @Test
  void shouldReserveRoom() throws Exception {
    // given
    given(
            useCase.reserve(
                assertArg(
                    command -> {
                      assertThat(command.reservationOwnerIdentifier().value())
                          .isEqualTo(ReservationParticipantTestFactory.IDENTIFIER_AS_STRING);
                      assertThat(command.roomIdentifier().value()).isEqualTo(ROOM_IDENTIFIER);
                      assertThat(command.period()).isEqualTo(createPeriodBetween(11, 15));
                      assertThat(command.numberOfParticipants()).isEqualTo(7);
                    })))
        .willReturn(RESERVATION_IDENTIFIER);

    // when
    ResultActions resultActions = performRequest(getJsonRequestBody());

    // then
    resultActions
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.reservationIdentifier").value(RESERVATION_IDENTIFIER.value()));
  }

  @Test
  void shouldReturnBadRequestWhenPeriodIsInThePast() throws Exception {
    // given
    String jsonRequestBodyWithInvalidPeriod =
        getJsonRequestBody(
            new ReservationPeriod(
                LocalDateTime.of(2025, 6, 23, 10, 0), LocalDateTime.of(2025, 6, 23, 12, 0)));

    // when
    ResultActions resultActions = performRequest(jsonRequestBodyWithInvalidPeriod);

    // then
    resultActions
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Reservation cannot start in the past"));
  }

  @Test
  void shouldReturnNotFoundWhenRoomNotExists() throws Exception {
    // given
    given(useCase.reserve(any()))
        .willThrow(new RoomNotFoundException(new RoomIdentifier(ROOM_IDENTIFIER)));

    // when
    ResultActions resultActions = performRequest(getJsonRequestBody());

    // then
    resultActions.andExpect(status().isNotFound());
  }

  private ResultActions performRequest(String jsonRequestBody) throws Exception {
    return mockMvc.perform(
        post("/rooms/{roomIdentifier}/reservations", ROOM_IDENTIFIER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(jsonRequestBody));
  }

  private static String getJsonRequestBody() {
    ReservationPeriod period = createPeriodBetween(11, 15);

    return getJsonRequestBody(period);
  }

  private static String getJsonRequestBody(ReservationPeriod period) {

    @Language("JSON")
    String jsonRequestBody =
        """
                {
                    "reservationOwnerIdentifier": "EMP0001",
                    "startTime": "%s",
                    "endTime": "%s",
                    "numberOfParticipants": 7
                }
                """;
    return jsonRequestBody.formatted(period.from(), period.to());
  }
}
