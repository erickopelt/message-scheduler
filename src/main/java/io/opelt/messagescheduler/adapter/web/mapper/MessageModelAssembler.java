package io.opelt.messagescheduler.adapter.web.mapper;

import io.opelt.messagescheduler.adapter.web.controller.MessageController;
import io.opelt.messagescheduler.adapter.web.model.CreateMessageModel;
import io.opelt.messagescheduler.adapter.web.model.MessageModel;
import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import org.springframework.hateoas.server.mvc.RepresentationModelAssemblerSupport;
import org.springframework.stereotype.Component;

@Component
public class MessageModelAssembler extends RepresentationModelAssemblerSupport<Message, MessageModel> {

    public MessageModelAssembler() {
        super(MessageController.class, MessageModel.class);
    }

    @Override
    public MessageModel toModel(Message message) {
        return createModelWithId(message.getId(), message);
    }

    @Override
    protected MessageModel instantiateModel(Message message) {
        return MessageModel.builder()
                .id(message.getId())
                .body(message.getBody())
                .recipient(message.getRecipient())
                .channel(message.getChannel())
                .schedule(message.getSchedule())
                .build();
    }

    public CreateMessage toDomain(CreateMessageModel createMessageModel) {
        return CreateMessage.builder()
                .channel(createMessageModel.getChannel())
                .body(createMessageModel.getBody())
                .schedule(createMessageModel.getSchedule())
                .recipient(createMessageModel.getRecipient())
                .build();
    }

}
