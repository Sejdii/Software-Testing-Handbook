package pl.sejdii.example.application.domain.service.participant


import pl.sejdii.example.adapter.out.memory.ParticipantInMemoryAdapter
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase
import spock.lang.Specification

class SociableExampleSpecification extends Specification {

	private static final String GENERATED_IDENTIFIER = "EMP0001"
	private static final String FIRST_NAME = "Joe"
	private static final String LAST_NAME = "Doe"

	private final ParticipantInMemoryAdapter participantInMemoryAdapter = new ParticipantInMemoryAdapter()
	private final CreateReservationParticipantUseCase createParticipantUseCase = new ReservationParticipantConfiguration().createReservationParticipantUseCase(participantInMemoryAdapter)

	def "Should create participant"() {
		given: "Command of participant creation"
		def command = new CreateReservationParticipantUseCase.Command(FIRST_NAME, LAST_NAME)

		when: "Participant is created"
		def identifier = createParticipantUseCase.create(command)

		then: "Identifier is generated"
		identifier.value() == GENERATED_IDENTIFIER

		and: "Participant is inserted into repository"
		def participantByIdentifier = participantInMemoryAdapter.findBy(identifier)
		participantByIdentifier.isPresent()

		def participant = participantByIdentifier.get()
		participant.firstName() == FIRST_NAME
		participant.surname() == LAST_NAME
	}
}
