package io.opelt.messagescheduler.usecase;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageFilter;
import io.opelt.messagescheduler.usecase.exception.MessageNotFoundException;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FindMessage {

    private final MessageRepository repository;

    public Message findById(String id) throws MessageNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new MessageNotFoundException(id));
    }

    public Page<Message> findByFilter(MessageFilter filter) {
        return repository.findByFilter(filter);
    }
}
