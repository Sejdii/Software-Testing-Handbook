package pl.sejdii.example.user.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import pl.sejdii.example.user.application.domain.User;
import pl.sejdii.example.user.application.domain.UserIdentifier;
import pl.sejdii.example.user.application.port.out.InsertUserPort;

@Component
@Profile("in-memory")
public class UserInMemoryAdapter implements InsertUserPort {

  private final List<User> users = new ArrayList<>();

  @Override
  public void insert(User user) {
    users.add(user);
  }

  public Optional<User> findBy(UserIdentifier identifier) {
    return users.stream().filter(it -> it.identifier().equals(identifier)).findAny();
  }
}
