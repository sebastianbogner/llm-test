package com.tallence.llm;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloOllama {

  public static final String MODEL_NAME = "llama3";

  public static void main(String[] args) {

    String baseUrl = "http://localhost:11434";

    ChatLanguageModel model = OllamaChatModel.builder()
            .baseUrl(baseUrl)
            .modelName(MODEL_NAME)
            .build();

    ChatAssistant chatAssistant = AiServices.builder(ChatAssistant.class)
            .chatLanguageModel(model)
            .chatMemory(MessageWindowChatMemory.withMaxMessages(20))
            .build();

    startConversationWith(chatAssistant);
  }

  public static void startConversationWith(ChatAssistant chatAssistant) {
    Logger log = LoggerFactory.getLogger(ChatAssistant.class);

    try(Scanner scanner = new Scanner(System.in)) {
      while (true) {
        log.info("User question: ");
        String question = scanner.nextLine();
        if ("exit".equalsIgnoreCase(question)) {
          break;
        }

        var answer = chatAssistant.answer(question);
        log.info("Answer: {}", answer);
      }
    }
  }
}
