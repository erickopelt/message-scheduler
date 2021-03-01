package io.opelt.messagescheduler.domain;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMessage {

    private final LocalDateTime schedule;
    private final String recipient;
    private final String body;
    private final MessageChannel channel;
}
