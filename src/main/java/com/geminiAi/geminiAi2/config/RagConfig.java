package com.geminiAi.geminiAi2.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RagConfig implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);
    private final VectorStore vectorStore;
    private final MongoTemplate mongoTemplate;

    @Value("classpath:/shoply-Privacy-Policy.pdf")
    private Resource privacyPolicy;

    public RagConfig(VectorStore vectorStore, MongoTemplate mongoTemplate) {
        this.vectorStore = vectorStore;
        this.mongoTemplate = mongoTemplate;
    }


    @Override
    public void run(String... args) throws Exception {

        long availableDocument = mongoTemplate.getCollection("vector_store").countDocuments();

        if (availableDocument > 0) {
            log.info("VECTOR STORE ALREADY LOADED !!!");
       } else {
            var pdfReader = new PagePdfDocumentReader(privacyPolicy);
            TextSplitter textSplitter = new TokenTextSplitter();
            vectorStore.accept(textSplitter.apply(pdfReader.get()));

            log.info("VECTOR STORE LOADED WITH DATA!!!");
       }
    }




}
