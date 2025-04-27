package pl.sejdii.user.application.service;

import pl.sejdii.user.application.port.in.CreateUserUseCase;
import pl.sejdii.user.application.port.out.InsertUserPort;

class UserConfiguration {

  CreateUserUseCase createUserUseCase(InsertUserPort insertUserPort) {
    return new CreateUserService(new UserFactory(new UserIdentifierGenerator()), insertUserPort);
  }
}
