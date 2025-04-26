package pl.sejdii.user.application.port.in;

import pl.sejdii.user.application.domain.UserIdentifier;
import pl.sejdii.user.application.domain.ValidationException;

public interface CreateUserUseCase {

    UserIdentifier create(Command command);

    record Command(String firstName, String secondName) {

        public Command {
            if(firstName == null) {
                throw new ValidationException("First name cannot be null");
            }

            if(secondName == null) {
                throw new ValidationException("Second name cannot be null");
            }
        }
    }
}
