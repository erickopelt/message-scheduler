package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.CreateMessage;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleMessage {

    private final MessageRepository repository;

    public Message schedule(CreateMessage message) {
        return repository.save(message);
    }
}
