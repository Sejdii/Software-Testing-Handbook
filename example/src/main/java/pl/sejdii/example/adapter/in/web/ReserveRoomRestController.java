package pl.sejdii.example.adapter.in.web;

import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import pl.sejdii.example.application.domain.model.participant.ReservationParticipantIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationIdentifier;
import pl.sejdii.example.application.domain.model.reservation.ReservationPeriod;
import pl.sejdii.example.application.domain.model.room.RoomIdentifier;
import pl.sejdii.example.application.port.in.ReserveRoomUseCase;

@Slf4j
@RestController
@RequiredArgsConstructor
class ReserveRoomRestController {

  private final ReserveRoomUseCase useCase;

  @PostMapping("/rooms/{roomIdentifier}/reservations")
  ResponseEntity<ReserveRoomResponse> reserveRoom(
      @PathVariable String roomIdentifier, @RequestBody ReserveRoomRequest request)
      throws URISyntaxException {
    log.info("Incoming room {} reservation request: {}", roomIdentifier, request);

    ReservationIdentifier reservationIdentifier =
        useCase.reserve(mapToCommand(roomIdentifier, request));

    return ResponseEntity.created(
            new URI("/rooms/%s/reservations".formatted(reservationIdentifier.value())))
        .body(new ReserveRoomResponse(reservationIdentifier.value()));
  }

  private static ReserveRoomUseCase.Command mapToCommand(
      String roomIdentifier, ReserveRoomRequest request) {
    return new ReserveRoomUseCase.Command(
        new ReservationParticipantIdentifier(request.reservationOwnerIdentifier()),
        new RoomIdentifier(roomIdentifier),
        new ReservationPeriod(request.startTime(), request.endTime()),
        request.numberOfParticipants());
  }
}
