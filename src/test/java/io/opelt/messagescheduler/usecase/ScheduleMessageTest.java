package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScheduleMessageTest {

    @InjectMocks
    private ScheduleMessage scheduleMessage;

    @Mock
    private MessageRepository repository;

    @Test
    void givenAMessageWhenScheduleThenReturnScheduledMessage() {
        var createMessage = CreateMessage.builder()
                .schedule(LocalDateTime.now())
                .body("test")
                .channel(MessageChannel.EMAIL)
                .destiny("erick@opelt.dev")
                .build();

        var message = Message.builder()
                .id(UUID.randomUUID().toString())
                .schedule(LocalDateTime.now())
                .body("test")
                .channel(MessageChannel.EMAIL)
                .destiny("erick@opelt.dev")
                .build();

        when(repository.save(createMessage)).thenReturn(message);

        var scheduledMessage = scheduleMessage.schedule(createMessage);

        assertThat(scheduledMessage).isEqualTo(message);

        verify(repository).save(createMessage);
        verifyNoMoreInteractions(repository);
    }
}