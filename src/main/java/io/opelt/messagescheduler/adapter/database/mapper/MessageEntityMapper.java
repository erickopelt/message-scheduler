package io.opelt.messagescheduler.adapter.database.mapper;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageEntityMapper {

    public MessageEntity from(CreateMessage createMessage) {
        return MessageEntity.builder()
                .body(createMessage.getBody())
                .channel(createMessage.getChannel())
                .recipient(createMessage.getRecipient())
                .schedule(createMessage.getSchedule())
                .build();
    }

    public Message to(MessageEntity entity) {
        return Message.builder()
                .body(entity.getBody())
                .id(entity.getId())
                .channel(entity.getChannel())
                .recipient(entity.getRecipient())
                .schedule(entity.getSchedule())
                .build();
    }
}
