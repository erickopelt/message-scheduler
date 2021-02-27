package io.opelt.messagescheduler.adapter.web.model;

import com.sun.istack.NotNull;
import io.opelt.messagescheduler.domain.MessageChannel;
import lombok.*;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class CreateMessageModel {

    @NotNull
    private final LocalDateTime schedule;
    @NotBlank
    private final String destiny;
    @NotBlank
    private final String body;
    @NotNull
    private final MessageChannel channel;
}
