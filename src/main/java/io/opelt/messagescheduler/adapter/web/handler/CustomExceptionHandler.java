package io.opelt.messagescheduler.adapter.web.handler;

import io.opelt.messagescheduler.usecase.exception.MessageAlreadySentException;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.exception.MessageScheduleMustHaveAFutureValueException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<Void> handleMessageNotFound(MessageNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MessageAlreadySentException.class)
    public ResponseEntity<ErrorMessage> handleMessageAlreadySent(MessageAlreadySentException exception) {
        return ResponseEntity.badRequest()
                .body(ErrorMessage.builder().message(exception.getMessage()).build());
    }

    @ExceptionHandler(MessageScheduleMustHaveAFutureValueException.class)
    public ResponseEntity<ErrorMessage> handleMessageScheduleMustHaveAFutureValueException(MessageScheduleMustHaveAFutureValueException exception) {
        return ResponseEntity.badRequest()
                .body(ErrorMessage.builder().message(exception.getMessage()).build());

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorMessage> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        return ResponseEntity.badRequest()
                .body(ErrorMessage.builder()
                        .message(String.format("Field '%s' error: %s", exception.getFieldError().getField(), exception.getFieldError().getDefaultMessage()))
                        .build());
    }

    @Builder
    @Getter
    static class ErrorMessage {
        private final String message;
    }
}
