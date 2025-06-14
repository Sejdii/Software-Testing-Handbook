package pl.sejdii.example.user.application.service

import pl.sejdii.example.user.adapter.out.memory.UserInMemoryAdapter
import pl.sejdii.example.user.application.port.in.CreateUserUseCase
import spock.lang.Specification

class SociableExampleSpecification extends Specification {

	private static final String GENERATED_IDENTIFIER = "EMP0001"
	private static final String FIRST_NAME = "Joe"
	private static final String LAST_NAME = "Doe"

	private final UserInMemoryAdapter userInMemoryAdapter = new UserInMemoryAdapter()
	private final CreateUserUseCase createUserUseCase = new UserConfiguration().createUserUseCase(userInMemoryAdapter)

	def "Should create user"() {
		given: "Command of user creation"
		def command = new CreateUserUseCase.Command(FIRST_NAME, LAST_NAME)

		when: "User is created"
		def identifier = createUserUseCase.create(command)

		then: "Identifier is generated"
		identifier.value() == GENERATED_IDENTIFIER

		and: "User is inserted into repository"
		def userByIdentifier = userInMemoryAdapter.findBy(identifier)
		userByIdentifier.isPresent()

		def user = userByIdentifier.get()
		user.firstName() == FIRST_NAME
		user.surname() == LAST_NAME
	}
}
