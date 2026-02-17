/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author jmferreira
 */
public class OllamaStreamingChatService {

    private final StreamingChatModel model;

    public StreamingChatModel getModel() {
        return model;
    }

    public OllamaStreamingChatService(String baseUrl, String modelName) {
        this.model = OllamaStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(modelName)
                .temperature(0.3)
                .logRequests(true)
                .logResponses(true)
                .timeout(Duration.ofSeconds(60))
                .build();
    }

    /**
     * Envía un mensaje y devuelve la respuesta completa, recibiendo tokens en
     * streaming.
     */
    public CompletableFuture<String> chatStream(String userMessage) {
        CompletableFuture<String> futureResponse = new CompletableFuture<>();
        StringBuilder responseBuilder = new StringBuilder();

        model.chat(userMessage, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                //System.out.println(partialResponse);
                responseBuilder.append(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureResponse.complete(responseBuilder.toString());
            }

            @Override
            public void onError(Throwable error) {
                futureResponse.completeExceptionally(error);
            }
        });
        return futureResponse;
    }

    public static void main(String[] args) {

        OllamaStreamingChatService chatService
                = new OllamaStreamingChatService(
                        "http://10.254.3.242:11434",
                        "gemma3:4b"
                );
//        String userMessage = "Explicá qué es Jakarta EE en 5 parrafos. Cada parrafo debe tener hasta 20 palabras";
//
//        CompletableFuture<ChatResponse> futureResponse = new CompletableFuture<>();
//
//        chatService.model.chat(userMessage, new StreamingChatResponseHandler() {
//
//            @Override
//            public void onPartialResponse(String partialResponse) {
//                System.out.print(partialResponse);
//            }
//
//            @Override
//            public void onCompleteResponse(ChatResponse completeResponse) {
//                futureResponse.complete(completeResponse);
//            }
//
//            @Override
//            public void onError(Throwable error) {
//                futureResponse.completeExceptionally(error);
//            }
//        });
//
//        futureResponse.join();
        System.out.println("Respuesta del modelo:\n");

        String respuestaFinal = chatService
                .chatStream("Explicá qué es Jakarta EE en 5 parrafos. Cada parrafo debe tener hasta 20 palabras").join();

        System.out.println("\n\n--- Respuesta completa ---");
        System.out.println(respuestaFinal);

    }
}
