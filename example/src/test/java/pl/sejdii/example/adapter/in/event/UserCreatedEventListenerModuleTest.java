package pl.sejdii.example.adapter.in.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory.FIRST_NAME;
import static pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory.SURNAME;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.kafka.core.KafkaTemplate;
import pl.sejdii.example.ModuleTest;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;

class UserCreatedEventListenerModuleTest extends ModuleTest {

  @Autowired private KafkaTemplate<String, UserCreatedEvent> template;

  @Autowired private JdbcClient jdbcClient;

  @Test
  void shouldCreateNewReservationParticipant() {
    // given
    UserCreatedEvent event = new UserCreatedEvent(FIRST_NAME, SURNAME);

    // when
    sendMessage(event);

    // then
    await().until(() -> !getAllReservationParticipantsFromDb().isEmpty());

    assertThat(getAllReservationParticipantsFromDb())
        .hasSize(1)
        .element(0)
        .satisfies(UserCreatedEventListenerModuleTest::assertCreatedReservationParticipant);
  }

  private void sendMessage(UserCreatedEvent event) {
    template.send("userCreated", event);
  }

  private List<ReservationParticipant> getAllReservationParticipantsFromDb() {
    return jdbcClient
        .sql("select * from reservation_participant")
        .query(ReservationParticipant.class)
        .list();
  }

  private static void assertCreatedReservationParticipant(
      ReservationParticipant reservationParticipant) {
    assertThat(reservationParticipant.firstName()).isEqualTo(FIRST_NAME);
    assertThat(reservationParticipant.surname()).isEqualTo(SURNAME);
  }
}
