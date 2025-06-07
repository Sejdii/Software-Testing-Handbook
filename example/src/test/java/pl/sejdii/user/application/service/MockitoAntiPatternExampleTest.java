package pl.sejdii.user.application.service;

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
import pl.sejdii.user.application.domain.User;
import pl.sejdii.user.application.domain.UserIdentifier;
import pl.sejdii.user.application.port.in.CreateUserUseCase;
import pl.sejdii.user.application.port.out.InsertUserPort;

@ExtendWith(MockitoExtension.class)
class MockitoAntiPatternExampleTest {

  private static final String GENERATED_IDENTIFIER = "EMP0001";
  private static final String FIRST_NAME = "Joe";
  private static final String LAST_NAME = "Doe";

  @Mock private UserFactory userFactory;

  @Mock private InsertUserPort insertUserPort;

  @Mock private DependentClassThatThisServiceIsNotLongerUse dependentClass;

  @InjectMocks private CreateUserService createUserService;

  @BeforeEach
  void setUp() {
    lenient().when(dependentClass.doSomething()).thenReturn(1);
  }

  @Test
  void shouldCreateUser() {
    // Given
    CreateUserUseCase.Command command = new CreateUserUseCase.Command(FIRST_NAME, LAST_NAME);

    when(userFactory.create(command))
        .thenReturn(new User(new UserIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME));

    // When
    createUserService.create(command);

    // Then
    var captor = ArgumentCaptor.forClass(User.class);

    verify(insertUserPort).insert(captor.capture());
    // assertions for created user omitted
  }

  private interface DependentClassThatThisServiceIsNotLongerUse {

    int doSomething();
  }
}
