package pl.sejdii.user.application.service;

import pl.sejdii.user.application.domain.User;
import pl.sejdii.user.application.domain.UserIdentifier;
import pl.sejdii.user.application.port.in.CreateUserUseCase;

class UserFactory {

  private final UserIdentifierGenerator userIdentifierGenerator;

  UserFactory(UserIdentifierGenerator userIdentifierGenerator) {
    this.userIdentifierGenerator = userIdentifierGenerator;
  }

  User create(CreateUserUseCase.Command command) {
    UserIdentifier userIdentifier = userIdentifierGenerator.generate();

    return new User(userIdentifier, command.firstName(), command.secondName());
  }
}
