package io.opelt.messagescheduler.adapter.web.model;

import io.opelt.messagescheduler.domain.MessageChannel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
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
