package pl.sejdii.example.adapter.out.postgres.room;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static pl.sejdii.example.application.domain.model.reservation.ReservationPeriodTestFactory.createPeriodBetween;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import pl.sejdii.example.adapter.out.postgres.PostgresTestIT;
import pl.sejdii.example.adapter.out.postgres.StatisticAssertions;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantTestFactory;
import pl.sejdii.example.application.domain.model.reservation.Reservation;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.room.Room;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;

class RoomPostgresAdapterTestIT extends PostgresTestIT {

  @Autowired private RoomRepository repository;

  private RoomPostgresAdapter adapter;

  @BeforeEach
  void setUp() {
    adapter = new RoomPostgresAdapter(repository, new RoomEntityMapper());
  }

  @Test
  @Sql("insert-room-with-reservations.sql")
  void shouldFindRoomByIdentifier() {
    // given
    RoomIdentifier roomIdentifier = new RoomIdentifier("aedd07ed-efd5-4720-99b0-7ae731df43ff");

    // when
    Optional<Room> foundRoom = adapter.find(roomIdentifier);

    // then
    StatisticAssertions.assertThat(statistics).hasQueryCount(1);

    assertThat(foundRoom).isPresent();
    assertRoom(foundRoom.get());
  }

  @Test
  @Sql("insert-room-with-reservations.sql")
  void shouldFindRoomWithoutReservations() {
    // given
    RoomIdentifier roomIdentifier = new RoomIdentifier("d175d0ba-1a96-4e4a-929b-090c62c31eaf");

    // when
    Optional<Room> foundRoom = adapter.find(roomIdentifier);

    // then
    StatisticAssertions.assertThat(statistics).hasQueryCount(1);

    assertThat(foundRoom).isPresent();

    Room room = foundRoom.get();
    assertThat(room.getActiveReservations()).isEmpty();
  }

  @Test
  void shouldFindNoRoom() {
    // given
    RoomIdentifier roomIdentifier = new RoomIdentifier("aedd07ed-efd5-4720-99b0-7ae731df43ff");

    // when
    Optional<Room> foundRoom = adapter.find(roomIdentifier);

    // then
    assertThat(foundRoom).isEmpty();
  }

  @Test
  void shouldInsertRoom() {
    // given
    Room room = new Room(9);

    // when
    adapter.insert(room);

    // then
    StatisticAssertions.assertThat(statistics).hasInsertCount(1);

    Room insertedRoom = adapter.find(room.getIdentifier()).orElseThrow();
    assertThat(insertedRoom.getNumberOfSeats()).isEqualTo(9);
    assertThat(insertedRoom.getActiveReservations()).isEmpty();
  }

  @Test
  void shouldInsertRoomWithReservations() {
    // given
    Room room = new Room(5);

    Reservation firstReservation =
        new Reservation(
            ReservationParticipantTestFactory.IDENTIFIER,
            createPeriodBetween(10, 12),
            room.getIdentifier(),
            3);
    Reservation secondReservation =
        new Reservation(
            ReservationParticipantTestFactory.IDENTIFIER,
            createPeriodBetween(12, 14),
            room.getIdentifier(),
            2);

    room.reserve(firstReservation);
    room.reserve(secondReservation);

    // when
    adapter.insert(room);

    // then
    StatisticAssertions.assertThat(statistics).hasQueryCount(3).hasInsertCount(3);

    Room insertedRoom = adapter.find(room.getIdentifier()).orElseThrow();

    List<Reservation> activeReservations = insertedRoom.getActiveReservations();
    assertThat(activeReservations).hasSize(2);

    assertReservationBetween10and12(
        findReservation(activeReservations, firstReservation.getIdentifier()),
        room.getIdentifier());
    assertReservationBetween12and14(
        findReservation(activeReservations, secondReservation.getIdentifier()),
        room.getIdentifier());
  }

  @Test
  @Sql("insert-room-with-reservations.sql")
  void shouldUpdateRoomWithNewReservation() {
    // given
    Room existingRoom =
        adapter.find(new RoomIdentifier("aedd07ed-efd5-4720-99b0-7ae731df43ff")).orElseThrow();

    Reservation newReservation =
        new Reservation(
            ReservationParticipantTestFactory.IDENTIFIER,
            createPeriodBetween(13, 15),
            existingRoom.getIdentifier(),
            3);
    existingRoom.reserve(newReservation);

    statistics.clear();

    // when
    adapter.update(existingRoom);

    // then
    StatisticAssertions.assertThat(statistics).hasQueryCount(1).hasUpdateCount(0).hasInsertCount(1);

    Room updatedRoom = adapter.find(existingRoom.getIdentifier()).orElseThrow();
    assertThat(updatedRoom.getActiveReservations()).hasSize(2);

    Reservation newInsertedReservation =
        findReservation(updatedRoom.getActiveReservations(), newReservation.getIdentifier());
    assertThat(newInsertedReservation)
        .usingRecursiveComparison()
        .ignoringFields("technicalId")
        .isEqualTo(newReservation);
  }

  @Test
  void shouldThrowExceptionWhenRoomIsNotYetPersisted() {
    // given
    Room room = new Room(5);

    // when then
    assertThatIllegalArgumentException()
        .isThrownBy(() -> adapter.update(room))
        .withMessage("Room is not persisted yet.");
  }

  private static void assertRoom(Room room) {
    assertThat(room.getNumberOfSeats()).isEqualTo(4);
    assertThat(room.getActiveReservations()).hasSize(1);

    Reservation reservation = room.getActiveReservations().getFirst();
    assertReservation(reservation);
  }

  private static void assertReservation(Reservation reservation) {
    assertThat(reservation.getIdentifier().value())
        .isEqualTo("3aeb88fb-5d68-4ec2-85b4-977da4e58b6f");
    assertThat(reservation.getReservationOwnerIdentifier().value()).isEqualTo("EMP0001");
    assertThat(reservation.getPeriod().from()).isEqualTo("2025-06-01T10:00:00");
    assertThat(reservation.getPeriod().to()).isEqualTo("2025-06-01T11:00:00");
    assertThat(reservation.getRoomIdentifier().value())
        .isEqualTo("aedd07ed-efd5-4720-99b0-7ae731df43ff");
    assertThat(reservation.getNumberOfParticipants()).isEqualTo(3);
  }

  private static void assertReservationBetween10and12(
      Reservation reservation, RoomIdentifier expectedRoomIdentifier) {
    assertThat(reservation.getReservationOwnerIdentifier().value()).isEqualTo("EMP0001");
    assertThat(reservation.getPeriod()).isEqualTo(createPeriodBetween(10, 12));
    assertThat(reservation.getRoomIdentifier()).isEqualTo(expectedRoomIdentifier);
    assertThat(reservation.getNumberOfParticipants()).isEqualTo(3);
  }

  private static void assertReservationBetween12and14(
      Reservation reservation, RoomIdentifier expectedRoomIdentifier) {
    assertThat(reservation.getReservationOwnerIdentifier().value()).isEqualTo("EMP0001");
    assertThat(reservation.getPeriod()).isEqualTo(createPeriodBetween(12, 14));
    assertThat(reservation.getRoomIdentifier()).isEqualTo(expectedRoomIdentifier);
    assertThat(reservation.getNumberOfParticipants()).isEqualTo(2);
  }

  private static Reservation findReservation(
      List<Reservation> reservations, ReservationIdentifier identifier) {
    return reservations.stream()
        .filter(it -> it.getIdentifier().equals(identifier))
        .findAny()
        .orElseThrow();
  }
}
