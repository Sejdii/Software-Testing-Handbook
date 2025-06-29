package pl.sejdii.example.adapter.in.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pl.sejdii.example.application.domain.model.ValidationException;
import pl.sejdii.example.application.domain.model.room.RoomNotFoundException;

@Slf4j
@ControllerAdvice
class ErrorHandler {

  @ExceptionHandler(RoomNotFoundException.class)
  ResponseEntity<Void> handle(RoomNotFoundException exception) {
    log.error("Handling RoomNotFoundException", exception);

    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(ValidationException.class)
  ResponseEntity<ErrorResponse> handle(ValidationException exception) {
    log.error("Handling ValidationException", exception);

    return ResponseEntity.badRequest().body(new ErrorResponse(exception.getMessage()));
  }
}
