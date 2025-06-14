package pl.sejdii.example.user.application.service

import pl.sejdii.example.user.application.domain.User
import pl.sejdii.example.user.application.domain.UserIdentifier
import pl.sejdii.example.user.application.port.in.CreateUserUseCase
import pl.sejdii.example.user.application.port.out.InsertUserPort
import spock.lang.Specification

class SolitaryExampleSpecification extends Specification {

	private static final String GENERATED_IDENTIFIER = "EMP0001"
	private static final String FIRST_NAME = "Joe"
	private static final String LAST_NAME = "Doe"

	private final UserFactory userFactory = Mock(UserFactory)
	private final InsertUserPort insertUserPort = Mock(InsertUserPort)

	private final CreateUserUseCase createUserUseCase = new CreateUserService(this.userFactory, insertUserPort)

	def "Should create user"() {
		given: "Command of user creation"
		def command = new CreateUserUseCase.Command(FIRST_NAME, LAST_NAME)

		this.userFactory.create(command) >> new User(new UserIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME)

		when: "User is created"
		def identifier = createUserUseCase.create(command)

		then: "Identifier is generated"
		identifier.value() == GENERATED_IDENTIFIER

		and: "User is inserted into repository"
		1 * insertUserPort.insert(_ as User) >> { User user ->
			assert user.identifier().value() == GENERATED_IDENTIFIER
			assert user.firstName() == FIRST_NAME
			assert user.surname() == LAST_NAME
			return user.identifier()
		}
	}
}
