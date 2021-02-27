package io.opelt.messagescheduler.adapter.web.mapper;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.adapter.web.model.CreateMessageModel;
import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageChannel;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class MessageModelAssemblerTest {

    private MessageModelAssembler assembler;

    @BeforeEach
    void setUp() {
        assembler = new MessageModelAssembler();
    }

    @Test
    void givenAMessageWheMapThenReturnModel() {
        var message = Message.builder()
                .id(UUID.randomUUID().toString())
                .schedule(LocalDateTime.now())
                .body("test")
                .channel(MessageChannel.EMAIL)
                .destiny("erick@opelt.dev")
                .build();

        var model = assembler.toModel(message);

        assertThat(model.getId()).isEqualTo(message.getId());
        assertThat(model.getBody()).isEqualTo(message.getBody());
        assertThat(model.getDestiny()).isEqualTo(message.getDestiny());
        assertThat(model.getSchedule()).isEqualTo(message.getSchedule());
        assertThat(model.getChannel()).isEqualTo(message.getChannel());
    }

    @Test
    void toDomain() {
        var createMessageModel = CreateMessageModel.builder()
                .schedule(LocalDateTime.now())
                .body("test")
                .channel(MessageChannel.EMAIL)
                .destiny("erick@opelt.dev")
                .build();

        var createMessage = assembler.toDomain(createMessageModel);

        assertThat(createMessage.getBody()).isEqualTo(createMessageModel.getBody());
        assertThat(createMessage.getDestiny()).isEqualTo(createMessageModel.getDestiny());
        assertThat(createMessage.getSchedule()).isEqualTo(createMessageModel.getSchedule());
        assertThat(createMessage.getChannel()).isEqualTo(createMessageModel.getChannel());
    }
}