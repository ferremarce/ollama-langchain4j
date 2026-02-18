/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jmferreira
 */
public class OllamaSimplePromptTemplate {

    public static void main(String[] args) {

        OllamaChatService ollamaService = new OllamaChatService(
                "http://10.254.3.242:11434",
                "gemma3:4b"
        );

        String template = "Crea una receta para {{plato}} con los siguientes ingredientes: {{ingredientes}}";
        PromptTemplate promptTemplate = PromptTemplate.from(template);

        Map<String, Object> variables = new HashMap<>();
        variables.put("plato", "ensalada");
        variables.put("ingredientes", "aceite de oliva, pepinos, albahaca");

        Prompt prompt = promptTemplate.apply(variables);

        String response = ollamaService.chat(prompt.text());

        System.out.println(response);
    }

}
