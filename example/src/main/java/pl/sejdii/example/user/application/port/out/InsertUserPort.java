package pl.sejdii.example.user.application.port.out;

import pl.sejdii.example.user.application.domain.User;

public interface InsertUserPort {

  void insert(User user);
}
