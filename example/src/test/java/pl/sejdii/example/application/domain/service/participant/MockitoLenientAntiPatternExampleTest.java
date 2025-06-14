package pl.sejdii.example.application.domain.service.participant;

import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipant;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase;
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort;

@ExtendWith(MockitoExtension.class)
class MockitoLenientAntiPatternExampleTest {

  private static final String GENERATED_IDENTIFIER = "EMP0001";
  private static final String FIRST_NAME = "Joe";
  private static final String LAST_NAME = "Doe";

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
        new CreateReservationParticipantUseCase.Command(FIRST_NAME, LAST_NAME);

    when(reservationParticipantFactory.create(command))
        .thenReturn(
            new ReservationParticipant(
                new ReservationParticipantIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME));

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
