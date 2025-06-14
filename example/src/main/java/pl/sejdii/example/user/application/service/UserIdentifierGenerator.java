package pl.sejdii.example.user.application.service;

import java.util.concurrent.atomic.AtomicInteger;
import pl.sejdii.example.user.application.domain.UserIdentifier;

class UserIdentifierGenerator {

  private final AtomicInteger sequence = new AtomicInteger(0);

  UserIdentifier generate() {
    return new UserIdentifier("EMP000" + sequence.incrementAndGet());
  }
}
