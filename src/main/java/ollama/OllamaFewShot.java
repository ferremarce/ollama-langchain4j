/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 *
 * @author jmferreira
 */
public class OllamaFewShot {

    public static void main(String[] args) {

        OllamaStreamingChatService chatService
                = new OllamaStreamingChatService(
                        "http://10.254.3.242:11434",
                        "gemma3:4b"
                );
        List<ChatMessage> fewShotHistory = new ArrayList<>();
        
        // Adding positive feedback example to history
        fewShotHistory.add(UserMessage.from(
                "Me compré unos calzados Nike por 85 dólares en el centro."));
        fewShotHistory.add(AiMessage.from(
                "Producto: Calzado Nike, Precio: 85, Moneda: USD, Sentimiento: NEUTRO"));

        // Adding negative feedback example to history
        fewShotHistory.add(UserMessage
                .from("La suscripción mensual de Netflix subió a 12 euros."));
        fewShotHistory.add(AiMessage.from(
                "Producto: Suscripción Netflix, Precio: 12, Moneda: EUR, Sentimiento: NEGATIVO"));

      
        // Adding real user's message
        UserMessage customerComplaint = UserMessage
                .from("Ayer pagué 4500 guaranies por una hamburguesa doble con papas, me encantó");
        fewShotHistory.add(customerComplaint);

        System.out.println("[User]: " + customerComplaint.singleText());
        System.out.print("[LLM]: ");

        CompletableFuture<ChatResponse> futureChatResponse = new CompletableFuture<>();

        chatService.getChatModel().chat(fewShotHistory, new StreamingChatResponseHandler() {

            @Override
            public void onPartialResponse(String partialResponse) {
                System.out.print(partialResponse);
            }

            @Override
            public void onCompleteResponse(ChatResponse completeResponse) {
                futureChatResponse.complete(completeResponse);
            }

            @Override
            public void onError(Throwable error) {
                futureChatResponse.completeExceptionally(error);
            }
        });

        futureChatResponse.join();

        // Extract reply and send to customer
        // Perform necessary action in back-end
    }
}
