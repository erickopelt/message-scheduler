package io.opelt.messagescheduler.adapter.database.repository;

import io.opelt.messagescheduler.adapter.database.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageEntityRepository extends JpaRepository<MessageEntity, String> {
}
