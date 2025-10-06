package com.vtb.kafkaconsumer.repository;

import com.vtb.kafkaconsumer.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long>{
    public boolean existsByMsgUuid(String msgUuid);
}
