package io.opelt.messagescheduler.domain;

import org.springframework.data.domain.Pageable;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MessageFilter {

    private final Pageable pageable;
    private final MessageStatus status;
    private final MessageChannel channel;
}
