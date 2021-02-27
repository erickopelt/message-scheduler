package io.opelt.messagescheduler.adapter.web.handler;

import io.opelt.messagescheduler.adapter.web.model.MessageModel;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(MessageNotFoundException.class)
    public ResponseEntity<MessageModel> handleMessageNotFound(MessageNotFoundException exception) {
        return ResponseEntity.notFound().build();
    }
}
