package pl.sejdii.example.application.domain.model.room

import pl.sejdii.example.application.domain.model.ValidationException
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier
import pl.sejdii.example.application.domain.model.reservation.Reservation
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod
import spock.lang.Specification

import java.time.LocalDateTime

import static pl.sejdii.example.application.domain.model.reservation.ReservationPeriodTestFactory.createPeriodBetween

class RoomSpecification extends Specification {

	private static final ReservationParticipantIdentifier RESERVATION_OWNER_IDENTIFIER = new ReservationParticipantIdentifier("EMP0001")

	def "Should reserve room when room has no reservations"() {
		given: "Room without reservations"
		def room = new Room(12)

		and: "Reservation from 12:00 to 14:00 with 12 participants"
		def reservation = createReservation(room, createPeriodBetween(12, 14), 12)

		when: "Reserve room"
		room.reserve(reservation)

		then: "Reservation is active"
		room.getActiveReservations().size() == 1
		room.getActiveReservations().contains(reservation)
	}

	def "Should reserve room when room is free at requested reservation period"() {
		given: "Room which is reserved between 8:00 and 10:00"
		def room = new Room(12)
		room.reserve(createReservation(room, createPeriodBetween(8, 10), 5))

		and: "Reservation from 10:00 to 11:00"
		def reservation = createReservation(room, createPeriodBetween(10, 11), 9)

		when: "Reserve room"
		room.reserve(reservation)

		then: "Reservation in active"
		room.getActiveReservations().size() == 2
		room.getActiveReservations().contains(reservation)
	}

	def "Should throw exception when room is already reserved at given period"() {
		given: "Room which is reserved between 12:00 and 15:45"
		def room = new Room(5)
		room.reserve(createReservation(room, createPeriodBetween(12, 0, 15, 45), 3))

		and: "Reservation from 15:30 to 16:00"
		def reservation = createReservation(room, createPeriodBetween(15, 30, 16, 0), 5)

		when: "Reserve room"
		room.reserve(reservation)

		then: "Exception has been thrown"
		def exception = thrown(ValidationException)
		exception.getMessage() == "Room is already taken"
	}

	def "Should throw exception when room has less seats than requested participants number"() {
		given: "Room for 6 persons"
		def room = new Room(6)

		and: "Reservation for 7 participants"
		def reservation = createReservation(room, createPeriodBetween(12, 14), 7)

		when: "Reserve room"
		room.reserve(reservation)

		then: "Exception has been thrown"
		def exception = thrown(ValidationException)
		exception.getMessage() == "Room is not large enough"
	}

	def "Should throw exception when reservation is for different room"() {
		given: "Room"
		def room = new Room(9)

		and: "Reservation for different room"
		def reservation = createReservation(new Room(2), createPeriodBetween(8, 9), 2)

		when: "Reserve room"
		room.reserve(reservation)

		then: "Exception has been thrown"
		def exception = thrown(IllegalArgumentException)
		exception.getMessage() == "Room identifier does not match"
	}

	private static Reservation createReservation(Room room, ReservationPeriod reservationPeriod, Integer numberOfParticipants) {
		new Reservation(ReservationIdentifier.random(), RESERVATION_OWNER_IDENTIFIER, reservationPeriod, room.getIdentifier(),
				numberOfParticipants)
	}
}
