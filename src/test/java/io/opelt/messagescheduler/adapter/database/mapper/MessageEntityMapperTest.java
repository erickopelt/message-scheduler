package io.opelt.messagescheduler.adapter.database.mapper;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageEntityMapperTest {

    private MessageEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new MessageEntityMapper();
    }

    @Test
    void givenACreateMessageWhenMapThenReturnEntity() {
        var createMessage = Message.builder()
                .id(UUID.randomUUID().toString())
                .schedule(LocalDateTime.now())
                .body("test")
                .channel(MessageChannel.EMAIL)
                .recipient("erick@opelt.dev")
                .build();

        var entity = mapper.from(createMessage);

        assertThat(entity.getId()).isEqualTo(createMessage.getId());
        assertThat(entity.getBody()).isEqualTo(createMessage.getBody());
        assertThat(entity.getRecipient()).isEqualTo(createMessage.getRecipient());
        assertThat(entity.getSchedule()).isEqualTo(createMessage.getSchedule());
        assertThat(entity.getChannel()).isEqualTo(createMessage.getChannel());
        assertThat(entity.getStatus()).isEqualTo(createMessage.getStatus());
    }

    @Test
    void givenAnEntityWhenMapThenReturnMessage() {
        var entity = MessageEntity.builder()
                .id(UUID.randomUUID().toString())
                .schedule(LocalDateTime.now())
                .body("test")
                .channel(MessageChannel.EMAIL)
                .recipient("erick@opelt.dev")
                .build();

        var message = mapper.to(entity);

        assertThat(message.getId()).isEqualTo(entity.getId());
        assertThat(message.getBody()).isEqualTo(entity.getBody());
        assertThat(message.getRecipient()).isEqualTo(entity.getRecipient());
        assertThat(message.getSchedule()).isEqualTo(entity.getSchedule());
        assertThat(message.getChannel()).isEqualTo(entity.getChannel());
        assertThat(message.getStatus()).isEqualTo(entity.getStatus());
    }
}