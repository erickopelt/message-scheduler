package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageStatus;
import io.opelt.messagescheduler.usecase.exception.MessageAlreadySentException;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CancelScheduleMessageTest {

    @InjectMocks
    private CancelScheduleMessage cancelScheduleMessage;

    @Mock
    private FindMessage findMessage;

    @Mock
    private MessageRepository repository;

    @Test
    void givenAScheduledMessageWhenCancelThenCallRepository() throws MessageNotFoundException, MessageAlreadySentException {
        var id = "id";
        var message = Message.builder()
                .id(id)
                .status(MessageStatus.SCHEDULED)
                .build();

        when(findMessage.findById(id)).thenReturn(message);

        cancelScheduleMessage.cancel(id);

        verify(findMessage).findById(id);
        verify(repository).delete(message);
        verifyNoMoreInteractions(findMessage, repository);
    }

    @Test
    void givenASentMessageWhenCancelThenThrowMessageAlreadySent() throws MessageNotFoundException {
        var id = "id";
        var message = Message.builder()
                .id(id)
                .status(MessageStatus.SENT)
                .build();

        when(findMessage.findById(id)).thenReturn(message);

        assertThatThrownBy(() -> cancelScheduleMessage.cancel(id))
                .isInstanceOf(MessageAlreadySentException.class)
                .hasMessage("Message id=id already sent");

        verify(findMessage).findById(id);
        verifyNoMoreInteractions(findMessage);
        verifyNoInteractions(repository);
    }
}