package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageStatus;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

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
                .recipient("erick@opelt.dev")
                .build();

        when(repository.save(any())).thenAnswer(answer -> answer.getArgument(0));

        var scheduledMessage = scheduleMessage.schedule(createMessage);

        assertThat(scheduledMessage.getId()).isNull();
        assertThat(scheduledMessage.getSchedule()).isEqualTo(createMessage.getSchedule());
        assertThat(scheduledMessage.getBody()).isEqualTo(createMessage.getBody());
        assertThat(scheduledMessage.getRecipient()).isEqualTo(createMessage.getRecipient());
        assertThat(scheduledMessage.getChannel()).isEqualTo(createMessage.getChannel());
        assertThat(scheduledMessage.getStatus()).isEqualTo(MessageStatus.SCHEDULED);

        verify(repository).save(scheduledMessage);
        verifyNoMoreInteractions(repository);
    }
}