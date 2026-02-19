/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.SystemMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import java.util.concurrent.ExecutionException;
import static dev.langchain4j.data.message.UserMessage.userMessage;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import java.util.concurrent.CompletableFuture;
/**
 *
 * @author jmferreira
 */
public class OllamaStreamingChatMemory {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        OllamaStreamingChatService chatService
                = new OllamaStreamingChatService(
                        "http://10.254.3.242:11434",
                        "deepseek-r1:7b"
                );

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10) // últimos 10 mensajes
                .build();

        SystemMessage systemMessage = SystemMessage.from(
                "You are a senior developer explaining to another senior developer, "
                + "the project you are working on is an e-commerce platform with Java back-end, "
                + "Oracle database, and Spring Data JPA");
        chatMemory.add(systemMessage);

        UserMessage userMessage1 = userMessage(
                "How do I optimize database queries for a large-scale e-commerce platform? "
                + "Answer short in two paragrahs maximum.");
        chatMemory.add(userMessage1);

        System.out.println("[User]: " + userMessage1.singleText());
        System.out.print("[LLM]: ");

        AiMessage aiMessage1 = streamChat(chatService.getChatModel(), chatMemory);
        chatMemory.add(aiMessage1);

        UserMessage userMessage2 = userMessage(
                "Give a concrete example implementation of the first point? "
                + "Be short, 10 lines of code maximum.");
        chatMemory.add(userMessage2);

        System.out.println("\n\n[User]: " + userMessage2.singleText());
        System.out.print("[LLM]: ");

        AiMessage aiMessage2 = streamChat(chatService.getChatModel(), chatMemory);
        chatMemory.add(aiMessage2);
    }

    private static AiMessage streamChat(StreamingChatModel model, ChatMemory chatMemory)
            throws ExecutionException, InterruptedException {

        CompletableFuture<AiMessage> futureAiMessage = new CompletableFuture<>();

        StreamingChatResponseHandler handler = new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureAiMessage.complete(completeResponse.aiMessage());
            }

            @Override
            public void onError(Throwable throwable) {
            }
        };

        model.chat(chatMemory.messages(), handler);
        return futureAiMessage.get();
    }
}
