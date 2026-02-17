/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import java.time.Duration;

/**
 *
 * @author jmferreira
 */
public class OllamaChatService {

    private final ChatModel chatModel;

    public ChatModel getChatModel() {
        return chatModel;
    }

    public OllamaChatService(String baseUrl, String modelName) {
        this.chatModel = OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .timeout(Duration.ofMinutes(2))
                .logRequests(Boolean.TRUE)
                .logResponses(Boolean.TRUE)
                .build();
    }

    public String chat(String userMessage) {
        return chatModel.chat(userMessage);
    }

    public static void main(String[] args) {
        OllamaChatService ollama = new OllamaChatService(
                "http://10.254.3.242:11434",
                "gemma3:4b"
        );

        String response = ollama.chat("Primero dime qué modelo eres y luego explícame qué es LangChain4j");
        System.out.println(response);
    }
}
