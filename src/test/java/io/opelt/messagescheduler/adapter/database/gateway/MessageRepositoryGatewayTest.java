package io.opelt.messagescheduler.adapter.database.gateway;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.adapter.database.mapper.MessageEntityMapper;
import io.opelt.messagescheduler.adapter.database.repository.MessageEntityRepository;
import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MessageRepositoryGatewayTest {

    @InjectMocks
    private MessageRepositoryGateway gateway;

    @Mock
    private MessageEntityRepository repository;

    @Mock
    private MessageEntityMapper mapper;

    @Test
    void givenACreateMessageWhenSaveThenMapAndCallRepository() {
        var createMessage = Message.builder().build();
        var mappedEntity = MessageEntity.builder().build();
        var savedEntity = MessageEntity.builder().build();
        var savedMessage = Message.builder().build();

        when(mapper.from(createMessage)).thenReturn(mappedEntity);
        when(mapper.to(savedEntity)).thenReturn(savedMessage);
        when(repository.save(mappedEntity)).thenReturn(savedEntity);

        assertThat(gateway.save(createMessage)).isEqualTo(savedMessage);

        verify(mapper).from(createMessage);
        verify(mapper).to(savedEntity);
        verify(repository).save(mappedEntity);
        verifyNoMoreInteractions(repository, mapper);
    }
}