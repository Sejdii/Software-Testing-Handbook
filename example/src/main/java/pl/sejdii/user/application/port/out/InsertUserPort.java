package pl.sejdii.user.application.port.out;

import pl.sejdii.user.application.domain.User;

public interface InsertUserPort {

  void insert(User user);
}
