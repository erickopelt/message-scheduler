package io.opelt.messagescheduler.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageStatus;
import io.opelt.messagescheduler.usecase.exception.MessageScheduleMustHaveAFutureValueException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;

@ExtendWith(MockitoExtension.class)
class ScheduleMessageTest {

    @InjectMocks
    private ScheduleMessage scheduleMessage;

    @Mock
    private MessageRepository repository;

    @Test
    void givenAMessageWhenScheduleThenReturnScheduledMessage() throws MessageScheduleMustHaveAFutureValueException {
        var createMessage = CreateMessage.builder()
                .schedule(LocalDateTime.now().plusMinutes(5))
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

    @Test
    void givenAMessageWithPastScheduleWhenScheduleThenThrowException() {
        var schedule = LocalDateTime.now().minusMinutes(1);
        var createMessage = CreateMessage.builder()
                .schedule(schedule)
                .body("test")
                .channel(MessageChannel.EMAIL)
                .recipient("erick@opelt.dev")
                .build();


        assertThatThrownBy(() -> scheduleMessage.schedule(createMessage))
            .isInstanceOf(MessageScheduleMustHaveAFutureValueException.class)
            .hasMessage(String.format("Schedule field schedule=%s must have a future date-time", schedule.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS"))));

        verifyNoInteractions(repository);
    }
}