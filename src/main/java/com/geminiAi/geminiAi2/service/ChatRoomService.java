package com.geminiAi.geminiAi2.service;

import com.geminiAi.geminiAi2.model.ChatRoom;
import com.geminiAi.geminiAi2.repository.ChatRoomRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    public String createChatRoom(String senderId, String recipientId){
        String chatId= String.format("%s_%s",senderId,recipientId);

        ChatRoom viaUser = new ChatRoom();

        viaUser.setChatId(chatId);
        viaUser.setSenderId(senderId);
        viaUser.setRecipientId(recipientId);

        chatRoomRepository.save(viaUser);

        return chatId;
    }

    public Optional<String> findChatRoom(String senderId, String recipientId) {
      return  chatRoomRepository.findBySenderIdAndRecipientId(senderId, recipientId)
                .map(ChatRoom::getChatId).or(()->{
                    var chatId = createChatRoom(senderId, recipientId);

                    return Optional.of(chatId);
                });
    }
}
