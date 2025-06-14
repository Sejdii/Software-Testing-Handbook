package pl.sejdii.example.application.domain.service.participant

import pl.sejdii.example.application.domain.model.participant.ReservationParticipant
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier
import pl.sejdii.example.application.port.in.CreateReservationParticipantUseCase
import pl.sejdii.example.application.port.out.InsertReservationParticipantPort
import spock.lang.Specification

class SolitaryExampleSpecification extends Specification {

	private static final String GENERATED_IDENTIFIER = "EMP0001"
	private static final String FIRST_NAME = "Joe"
	private static final String LAST_NAME = "Doe"

	private final ReservationParticipantFactory participantFactory = Mock(ReservationParticipantFactory)
	private final InsertReservationParticipantPort insertParticipantPort = Mock(InsertReservationParticipantPort)

	private final CreateReservationParticipantUseCase createParticipantUseCase = new CreateReservationParticipantService(this.participantFactory, insertParticipantPort)

	def "Should create participant"() {
		given: "Command of participant creation"
		def command = new CreateReservationParticipantUseCase.Command(FIRST_NAME, LAST_NAME)

		this.participantFactory.create(command) >> new ReservationParticipant(new ReservationParticipantIdentifier(GENERATED_IDENTIFIER), FIRST_NAME, LAST_NAME)

		when: "Participant is created"
		def identifier = createParticipantUseCase.create(command)

		then: "Identifier is generated"
		identifier.value() == GENERATED_IDENTIFIER

		and: "Participant is inserted into repository"
		1 * insertParticipantPort.insert(_ as ReservationParticipant) >> { ReservationParticipant participant ->
			assert participant.identifier().value() == GENERATED_IDENTIFIER
			assert participant.firstName() == FIRST_NAME
			assert participant.surname() == LAST_NAME
			return participant.identifier()
		}
	}
}
