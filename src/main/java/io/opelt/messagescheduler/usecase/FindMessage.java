package io.opelt.messagescheduler.usecase;

import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindMessage {

    private final MessageRepository repository;

    public Message findById(String id) throws MessageNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }
}
