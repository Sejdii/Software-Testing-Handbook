package pl.sejdii.example.adapter.out.postgres;

import static org.assertj.core.api.Assertions.assertThat;
import static pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory;

class ParticipantPostgresAdapterTestIT extends PostgresTestIT {

  @Autowired private ReservationParticipantRepository repository;

  private ParticipantPostgresAdapter adapter;

  @BeforeEach
  void setup() {
    adapter = new ParticipantPostgresAdapter(repository);
  }

  @Test
  void shouldInsertParticipant() {
    // given
    ReservationParticipant participant = ReservationParticipantTestFactory.create();

    // when
    adapter.insert(participant);

    // then
    ReservationParticipantEntity savedParticipant =
        repository.findByIdentifier(IDENTIFIER).orElseThrow();
    assertThat(savedParticipant.getFirstName()).isEqualTo(FIRST_NAME);
    assertThat(savedParticipant.getSurname()).isEqualTo(SURNAME);

    StatisticAssertions.assertThat(statistics).hasInsertCount(1);
  }
}
