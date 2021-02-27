package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindMessageTest {

    @InjectMocks
    private FindMessage findMessage;

    @Mock
    private MessageRepository repository;

    @Test
    void givenAnIdWhenFindByIdThenReturnMessage() throws Exception {
        var id = "message-id";
        var message = Message.builder().build();

        when(repository.findById(id)).thenReturn(Optional.of(message));

        assertThat(findMessage.findById(id)).isEqualTo(message);

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }

    @Test
    void givenAnIdWhenFindByIdReturnsEmptyThenThrowException() throws Exception {
        var id = "message-id";

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> findMessage.findById(id))
                .isInstanceOf(MessageNotFoundException.class)
                .hasMessage("Message with id=message-id not found");

        verify(repository).findById(id);
        verifyNoMoreInteractions(repository);
    }
}