package io.opelt.messagescheduler.usecase.exception;

public class MessageNotFoundException extends Exception {

    public MessageNotFoundException(String id) {
        super(String.format("Message with id=%s not found", id));
    }
}
