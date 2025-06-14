package pl.sejdii.example.user.application.service;

import org.springframework.context.annotation.Bean;
import pl.sejdii.example.user.application.port.in.CreateUserUseCase;
import pl.sejdii.example.user.application.port.out.InsertUserPort;

class UserConfiguration {

  @Bean
  CreateUserUseCase createUserUseCase(InsertUserPort insertUserPort) {
    return new CreateUserService(new UserFactory(new UserIdentifierGenerator()), insertUserPort);
  }
}
