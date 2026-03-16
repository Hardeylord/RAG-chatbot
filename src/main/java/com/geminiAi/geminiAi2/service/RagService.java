package com.geminiAi.geminiAi2.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    private final ChatClient chatClient;
    private final VectorStore vectorStore;

    public RagService(ChatClient.Builder chatClient, VectorStore vectorStore){
        this.chatClient = chatClient.build();
        this.vectorStore = vectorStore;
    }

    public String promptWithContext (String userPrompt){

        List<Document> relevantChunks = vectorStore.similaritySearch(
                SearchRequest.builder()
//                Returns the top 4 most similar document chunks
                        .topK(4)
                        .query(userPrompt)
                        .build()
        );
        String context = relevantChunks.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n---\n"));

        return chatClient.prompt()
                .system("""
        You are a precise Q&A assistant. Your sole knowledge source is the context block below.
        
        Rules:
        - Answer only from the provided context
        - If the answer is not in the context, respond: "I don't have that information."
        - Be concise and direct — one to three sentences max
        - Never infer, assume, or supplement with outside knowledge
        - list on a new line and number
        
        Context:
        """ + context)
                .user(userPrompt)
                .call()
                .content();
    }


}
