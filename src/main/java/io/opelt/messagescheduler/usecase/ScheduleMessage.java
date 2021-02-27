package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageStatus;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleMessage {

    private final MessageRepository repository;

    public Message schedule(CreateMessage createMessage) {
        var message = Message.builder()
                .schedule(createMessage.getSchedule())
                .body(createMessage.getBody())
                .channel(createMessage.getChannel())
                .recipient(createMessage.getRecipient())
                .status(MessageStatus.SCHEDULED)
                .build();
        return repository.save(message);
    }
}
