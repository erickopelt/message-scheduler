package io.opelt.messagescheduler.usecase.exception;

public class MessageAlreadySentException extends Exception {

    public MessageAlreadySentException(String id) {
        super(String.format("Message id=%s already sent", id));
    }
}
