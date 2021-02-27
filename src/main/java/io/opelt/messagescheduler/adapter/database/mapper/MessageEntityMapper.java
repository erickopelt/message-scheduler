package io.opelt.messagescheduler.adapter.database.mapper;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.domain.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageEntityMapper {

    public MessageEntity from(Message message) {
        return MessageEntity.builder()
                .id(message.getId())
                .body(message.getBody())
                .channel(message.getChannel())
                .recipient(message.getRecipient())
                .schedule(message.getSchedule())
                .status(message.getStatus())
                .build();
    }

    public Message to(MessageEntity entity) {
        return Message.builder()
                .body(entity.getBody())
                .id(entity.getId())
                .channel(entity.getChannel())
                .recipient(entity.getRecipient())
                .schedule(entity.getSchedule())
                .status(entity.getStatus())
                .build();
    }
}
