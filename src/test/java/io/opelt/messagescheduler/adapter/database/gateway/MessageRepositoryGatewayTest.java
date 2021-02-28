package io.opelt.messagescheduler.adapter.database.gateway;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.adapter.database.mapper.MessageEntityMapper;
import io.opelt.messagescheduler.adapter.database.repository.MessageEntityRepository;
import io.opelt.messagescheduler.domain.Message;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

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

    @Test
    void givenAnIdWhenFindByIdThenReturnMessage() {
        var id = UUID.randomUUID().toString();
        var entity = MessageEntity.builder().build();
        var message = Message.builder().build();

        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.to(entity)).thenReturn(message);

        var foundMessage = gateway.findById(id);

        assertThat(foundMessage).isPresent().get().isEqualTo(message);

        verify(repository).findById(id);
        verify(mapper).to(entity);
        verifyNoMoreInteractions(repository, mapper);
    }

    @Test
    void givenAMessageWhenDeleteThenMapAndCallRepository() {
        var message = Message.builder().build();
        var entity = MessageEntity.builder().build();

        when(mapper.from(message)).thenReturn(entity);

        gateway.delete(message);

        verify(mapper).from(message);
        verify(repository).delete(entity);
        verifyNoMoreInteractions(mapper, repository);
    }

}