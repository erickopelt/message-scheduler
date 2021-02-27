package io.opelt.messagescheduler.adapter.database.gateway;

import io.opelt.messagescheduler.adapter.database.mapper.MessageEntityMapper;
import io.opelt.messagescheduler.adapter.database.repository.MessageEntityRepository;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MessageRepositoryGateway implements MessageRepository {

    private final MessageEntityRepository repository;
    private final MessageEntityMapper mapper;

    @Override
    public Message save(Message message) {
        var entity = mapper.from(message);
        var savedEntity = repository.save(entity);
        return mapper.to(savedEntity);
    }

    @Override
    public Optional<Message> findById(String id) {
        return repository.findById(id).map(mapper::to);
    }
}
