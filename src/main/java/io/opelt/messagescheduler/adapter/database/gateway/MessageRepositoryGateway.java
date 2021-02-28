package io.opelt.messagescheduler.adapter.database.gateway;

import static io.opelt.messagescheduler.adapter.database.repository.MessageEntityRepository.channelEqualTo;
import static io.opelt.messagescheduler.adapter.database.repository.MessageEntityRepository.statusEqualsTo;

import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.adapter.database.mapper.MessageEntityMapper;
import io.opelt.messagescheduler.adapter.database.repository.MessageEntityRepository;
import io.opelt.messagescheduler.domain.Message;
import io.opelt.messagescheduler.domain.MessageFilter;
import io.opelt.messagescheduler.usecase.port.MessageRepository;
import lombok.RequiredArgsConstructor;

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

    @Override
    public Page<Message> findByFilter(MessageFilter filter) {
        var spec = Specification.<MessageEntity>where(null);
        if (Objects.nonNull(filter.getStatus())) {
            spec = spec.and(statusEqualsTo(filter.getStatus()));
        }
        if (Objects.nonNull(filter.getChannel())) {
            spec = spec.and(channelEqualTo(filter.getChannel()));
        }
        return repository.findAll(spec, filter.getPageable()).map(mapper::to);
    }

    @Override
    public void delete(Message message) {
        repository.delete(mapper.from(message));
    }
}
