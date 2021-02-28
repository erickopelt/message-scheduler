package io.opelt.messagescheduler.adapter.database.repository;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import io.opelt.messagescheduler.adapter.database.entity.MessageEntity_;
import io.opelt.messagescheduler.domain.MessageChannel;
import io.opelt.messagescheduler.domain.MessageStatus;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, String>, JpaSpecificationExecutor<MessageEntity> {

    static Specification<MessageEntity> statusEqualsTo(MessageStatus status) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MessageEntity_.status), status);
    }

    static Specification<MessageEntity> channelEqualTo(MessageChannel channel) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get(MessageEntity_.channel), channel);
    }
}
