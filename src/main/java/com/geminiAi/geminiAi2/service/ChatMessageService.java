package com.geminiAi.geminiAi2.service;

import com.geminiAi.geminiAi2.model.ChatMessage;
import com.geminiAi.geminiAi2.repository.ChatMessageRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;

    public ChatMessageService(ChatMessageRepository chatMessageRepository, ChatRoomService chatRoomService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatRoomService = chatRoomService;
    }

    public void saveChatMessage(ChatMessage chatMessage) {
//        check if chatRoomExist
        String chatRoomId = chatRoomService.findChatRoom(chatMessage.getSenderId(), chatMessage.getRecipientId()).orElseThrow(()-> new RuntimeException("UNABLE TO INITIALIZE CHATROOM"));
        chatMessage.setChatId(chatRoomId);
        chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> findChatMessage(String senderId, String recipientId){

        var chatRoomId = chatRoomService.findChatRoom(senderId, recipientId);
        return chatRoomId.map(chatMessageRepository::findByChatId).orElse(new ArrayList<>());
    }
}
