package io.opelt.messagescheduler.usecase.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MessageScheduleMustHaveAFutureValueException extends Exception {

    public MessageScheduleMustHaveAFutureValueException(LocalDateTime schedule) {
        super(String.format("Schedule field schedule=%s must have a future date-time",
                schedule.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"))));
    }
}
