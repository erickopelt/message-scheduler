package io.opelt.messagescheduler.adapter.web.model;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import io.opelt.messagescheduler.domain.MessageChannel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateMessageModel {

    @NotNull
    private final LocalDateTime schedule;
    @NotBlank
    private final String recipient;
    @NotBlank
    private final String body;
    @NotNull
    private final MessageChannel channel;
}
