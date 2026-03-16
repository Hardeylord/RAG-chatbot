package com.geminiAi.geminiAi2.repository;

import com.geminiAi.geminiAi2.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
   List<ChatMessage> findAllBySenderIdAndRecipientId(String senderId, String recipientId);

    List<ChatMessage> findByChatId(String chatRoomId);
}
