package pl.sejdii.user.adapter.out.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import pl.sejdii.user.application.domain.User;
import pl.sejdii.user.application.domain.UserIdentifier;
import pl.sejdii.user.application.port.out.InsertUserPort;

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
