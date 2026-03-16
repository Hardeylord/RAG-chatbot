package com.geminiAi.geminiAi2.controller;

import com.geminiAi.geminiAi2.model.ChatMessage;
import com.geminiAi.geminiAi2.service.ChatMessageService;
import com.geminiAi.geminiAi2.service.RagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class ChatController {

    @Autowired
    RagService ragService;
    @Autowired
    ChatMessageService chatMessageService;

    @PostMapping("/chatbot")
    public String chat(@RequestBody ChatMessage chatMessage){
        String chatbot_response = ragService.promptWithContext(chatMessage.getPrompt());
        chatMessage.setResponse(chatbot_response);
        chatMessageService.saveChatMessage(chatMessage);
        return chatbot_response;
    }

    @GetMapping("/chat-history/{senderId}/{recipientId}")
    public List<ChatMessage> chatMessages(@PathVariable String senderId,
                                          @PathVariable String recipientId) {
        return chatMessageService.findChatMessage(senderId, recipientId);
    }
}
