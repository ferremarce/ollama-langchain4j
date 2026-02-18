/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;

/**
 *
 * @author jmferreira
 */
public class OllamaChatMemory {

    public interface Assistant {

        String chat(String message);
    }

    public static void main(String[] args) {

        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .maxMessages(10) // últimos 10 mensajes
                .build();

        OllamaChatService ollamaService = new OllamaChatService(
                "http://10.254.3.242:11434",
                "gemma3:4b"
        );
        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(ollamaService.getChatModel())
                .chatMemory(chatMemory)
                .build();
        System.out.println("------------------------------------------------");
        System.out.println(assistant.chat("Hola, mi nombre es Juan y tengo 48 años, ¿me puedes contar algo de vos?"));
        System.out.println("------------------------------------------------");
        System.out.println(assistant.chat("¿Qué sabes del Senado de Paraguay?"));
        System.out.println("------------------------------------------------");
        System.out.println(assistant.chat("¿Quien es el actual presidente de Paraguay"));
        System.out.println("------------------------------------------------");
        System.out.println(assistant.chat("¿Qué sabes del SILpy, el sistema de información legislativa de Paraguay?"));
        System.out.println("------------------------------------------------");
        System.out.println(assistant.chat("¿Cual es mi edad?"));
    }

}
