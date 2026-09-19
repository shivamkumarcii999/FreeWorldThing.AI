package com.freeworldthing.repository;

import com.freeworldthing.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationKeyOrderByCreatedAtAsc(String conversationKey);

    @Query("select m from Message m where m.sender.id = :uid or m.recipient.id = :uid order by m.createdAt desc")
    List<Message> findThreads(@Param("uid") Long uid);
}
