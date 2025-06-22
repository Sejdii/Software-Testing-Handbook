package pl.sejdii.example.application.domain.service.room

import pl.sejdii.example.adapter.out.memory.RoomInMemoryAdapter
import pl.sejdii.example.application.domain.model.ValidationException
import pl.sejdii.example.application.domain.model.reservation.Reservation
import pl.sejdii.example.application.domain.model.room.Room
import pl.sejdii.example.application.domain.model.room.RoomIdentifier
import pl.sejdii.example.application.domain.model.room.RoomNotFoundException
import pl.sejdii.example.application.port.in.ReserveRoomUseCase
import spock.lang.Specification

import static pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory.IDENTIFIER as PARTICIPANT_IDENTIFIER
import static pl.sejdii.example.application.domain.model.reservation.ReservationPeriodTestFactory.createPeriodBetween

class ReserveRoomServiceSpecification extends Specification {

	private RoomInMemoryAdapter roomInMemoryAdapter = new RoomInMemoryAdapter()

	private ReserveRoomUseCase useCase = new RoomConfiguration().reserveRoomService(roomInMemoryAdapter, roomInMemoryAdapter)

	def "Should reserve a room"() {
		given: "Existing room"
		def room = new Room(10)
		roomInMemoryAdapter.insert(room)

		and: "Reservation command"
		def command = new ReserveRoomUseCase.Command(PARTICIPANT_IDENTIFIER, room.getIdentifier(),
				createPeriodBetween(11, 13), 9)

		when: "Reserve room"
		def reservationIdentifier = useCase.reserve(command)

		then: "Room has reservation"
		def updatedRoom = roomInMemoryAdapter.find(room.getIdentifier()).orElseThrow()

		def createdReservation = updatedRoom.getActiveReservations()
				.find { it.identifier() == reservationIdentifier }

		createdReservation.reservationOwnerIdentifier() == PARTICIPANT_IDENTIFIER
		createdReservation.period() == createPeriodBetween(11, 13)
		createdReservation.roomIdentifier() == room.getIdentifier()
		createdReservation.numberOfParticipants() == 9
	}

	def "Should reserve a room which has reservation but is not overlapping requested period"() {
		given: "Existing room with reservation between 11 and 13"
		def room = new Room(10)
		room.reserve(new Reservation(
				PARTICIPANT_IDENTIFIER, createPeriodBetween(11, 13), room.getIdentifier(), 9))

		roomInMemoryAdapter.insert(room)

		and: "Command to reserve the same room for the period between 14 and 16"
		def command = new ReserveRoomUseCase.Command(PARTICIPANT_IDENTIFIER, room.getIdentifier(),
				createPeriodBetween(14, 16), 9)

		when: "Reserve room"
		def reservationIdentifier = useCase.reserve(command)

		then: "Room has reservation"
		def updatedRoom = roomInMemoryAdapter.find(room.getIdentifier()).orElseThrow()

		def createdReservation = updatedRoom.getActiveReservations()
				.find { it.identifier() == reservationIdentifier }

		createdReservation.reservationOwnerIdentifier() == PARTICIPANT_IDENTIFIER
		createdReservation.period() == createPeriodBetween(14, 16)
		createdReservation.roomIdentifier() == room.getIdentifier()
		createdReservation.numberOfParticipants() == 9
	}

	def "Should throw exception when room doesn't exits"() {
		given: "Reservation command for non-existing room"

		def notExistingRoomIdentifier = RoomIdentifier.random()
		def command = new ReserveRoomUseCase.Command(PARTICIPANT_IDENTIFIER, notExistingRoomIdentifier,
				createPeriodBetween(11, 13), 9)

		when: "Reserve room"
		useCase.reserve(command)

		then: "Exception is thrown"
		def exception = thrown(RoomNotFoundException)
		exception.message == "Room with identifier %s not found".formatted(notExistingRoomIdentifier.value())
	}

	def "Should throw exception when room is already reserved"() {
		given: "Existing room with reservation between 11 and 13"
		def room = new Room(10)
		room.reserve(new Reservation(
				PARTICIPANT_IDENTIFIER, createPeriodBetween(11, 13), room.getIdentifier(), 9))

		roomInMemoryAdapter.insert(room)

		and: "Command to reserve the same room for the same period"
		def command = new ReserveRoomUseCase.Command(PARTICIPANT_IDENTIFIER, room.getIdentifier(),
				createPeriodBetween(11, 13), 9)

		when: "Reserve room second time with the same period"
		useCase.reserve(command)

		then: "Exception is thrown"
		def exception = thrown(ValidationException)
		exception.message == "Room is already taken"
	}

	def "Should throw exception when room is reserved for more participants than it can hold"() {
		given: "Existing room with capacity of 10"
		def room = new Room(10)
		roomInMemoryAdapter.insert(room)

		and: "Command to reserve the room for 11 participants"
		def command = new ReserveRoomUseCase.Command(PARTICIPANT_IDENTIFIER, room.getIdentifier(),
				createPeriodBetween(11, 13), 11)

		when: "Reserve room with too many participants"
		useCase.reserve(command)

		then: "Exception is thrown"
		def exception = thrown(ValidationException)
		exception.message == "Room is not large enough"
	}

	def "Should throw exception when room is reserved for less participants than 1"() {
		given: "Existing room with capacity of 10"
		def room = new Room(10)
		roomInMemoryAdapter.insert(room)

		and: "Command to reserve the room for 0 participants"
		def command = new ReserveRoomUseCase.Command(PARTICIPANT_IDENTIFIER, room.getIdentifier(),
				createPeriodBetween(11, 13), 0)

		when: "Reserve room with too few participants"
		useCase.reserve(command)

		then: "Exception is thrown"
		def exception = thrown(ValidationException)
		exception.message == "Number of participants must be at least 1"
	}
}
