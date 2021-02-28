package io.opelt.messagescheduler.usecase.exception;

import java.time.LocalDateTime;

public class MessageScheduleMustHaveAFutureValueException extends Exception {

    public MessageScheduleMustHaveAFutureValueException(LocalDateTime schedule) {
        super(String.format("Schedule field schedule=%s must have a future date-time", schedule.toString()));
    }
}
