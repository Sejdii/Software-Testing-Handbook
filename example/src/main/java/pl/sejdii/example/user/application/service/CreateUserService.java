package pl.sejdii.example.user.application.service;

import pl.sejdii.example.user.application.domain.User;
import pl.sejdii.example.user.application.domain.UserIdentifier;
import pl.sejdii.example.user.application.port.in.CreateUserUseCase;
import pl.sejdii.example.user.application.port.out.InsertUserPort;

class CreateUserService implements CreateUserUseCase {

  private final UserFactory userFactory;
  private final InsertUserPort insertUserPort;

  CreateUserService(UserFactory userFactory, InsertUserPort insertUserPort) {
    this.userFactory = userFactory;
    this.insertUserPort = insertUserPort;
  }

  @Override
  public UserIdentifier create(Command command) {
    User user = userFactory.create(command);
    insertUserPort.insert(user);

    return user.identifier();
  }
}
