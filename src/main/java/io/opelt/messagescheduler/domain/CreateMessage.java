package io.opelt.messagescheduler.domain;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Builder(toBuilder = true)
@Getter
@EqualsAndHashCode
@ToString
public class CreateMessage {

    private final LocalDateTime schedule;
    private final String recipient;
    private final String body;
    private final MessageChannel channel;
}
