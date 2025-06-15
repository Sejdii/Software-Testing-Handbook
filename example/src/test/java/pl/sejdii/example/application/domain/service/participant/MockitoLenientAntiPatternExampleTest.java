package pl.sejdii.example.application.domain.service.participant;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory;
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase;
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort;

@ExtendWith(MockitoExtension.class)
class MockitoLenientAntiPatternExampleTest {

  @Mock private ReservationParticipantFactory reservationParticipantFactory;

  @Mock private InsertReservationParticipantPort insertReservationParticipantPort;

  @Mock private DependentClassThatThisServiceIsNotLongerUse dependentClass;

  @InjectMocks private CreateReservationParticipantService createReservationParticipantService;

  @BeforeEach
  void setUp() {
    lenient().when(dependentClass.doSomething()).thenReturn(1);
  }

  @Test
  void shouldCreateParticipant() {
    // Given
    CreateReservationParticipantUseCase.Command command =
        new CreateReservationParticipantUseCase.Command(FIRST_NAME, SURNAME);

    when(reservationParticipantFactory.create(command))
        .thenReturn(ReservationParticipantTestFactory.create());

    // When
    createReservationParticipantService.create(command);

    // Then
    var captor = ArgumentCaptor.forClass(ReservationParticipant.class);

    verify(insertReservationParticipantPort).insert(captor.capture());
    // assertions for created Participant omitted
  }

  private interface DependentClassThatThisServiceIsNotLongerUse {

    int doSomething();
  }
}
