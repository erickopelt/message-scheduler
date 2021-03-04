package io.opelt.messagescheduler.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageFilter;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;

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

    @Test
    void givenAMessageFilterWhenFindByFilterThenCallRepository() {
        var filter = MessageFilter.builder().build();
        var page = Page.<Message>empty();

        when(repository.findByFilter(filter)).thenReturn(page);

        assertThat(findMessage.findByFilter(filter)).isEqualTo(page);

        verify(repository).findByFilter(filter);
        verifyNoMoreInteractions(repository);
    }
}