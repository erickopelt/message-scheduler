package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.MessageStatus;
import io.opelt.messagescheduler.usecase.exception.MessageAlreadySentException;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CancelScheduleMessage {

    private final FindMessage findMessage;
    private final MessageRepository repository;

    public void cancel(String id) throws MessageNotFoundException, MessageAlreadySentException {
        var message = findMessage.findById(id);
        if (MessageStatus.SENT.equals(message.getStatus())) {
            throw new MessageAlreadySentException(id);
        }
        repository.delete(message);
    }
}
