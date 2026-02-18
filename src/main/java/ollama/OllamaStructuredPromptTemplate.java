/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.structured.StructuredPrompt;
import dev.langchain4j.model.input.structured.StructuredPromptProcessor;
import static java.util.Arrays.asList;
import java.util.List;

/**
 *
 * @author jmferreira
 */
public class OllamaStructuredPromptTemplate {

    @StructuredPrompt({
        "Crea una receta para {{plato}} con los siguientes ingredientes: {{ingredientes}}.",
        "Escribe la respuesta de la siguiente manera:",
        "Nombre de la receta: ...",
        "Descripcion: ...",
        "Tiempo de preparación: ...",
        "Ingredientes requeridos:",
        "- ...",
        "- ...",
        "Instrucciones:",
        "- ...",
        "- ..."
    })
    static class CreateRecipePrompt {

        String plato;
        List<String> ingredientes;

        public CreateRecipePrompt(String plato, List<String> ingredientes) {
            this.plato = plato;
            this.ingredientes = ingredientes;
        }

    }

    public static void main(String[] args) {

        OllamaChatService ollamaService = new OllamaChatService(
                "http://10.254.3.242:11434",
                "gemma3:4b"
        );

        CreateRecipePrompt createRecipePrompt = new CreateRecipePrompt(
                "ensalada",
                asList("pepino", "tomate", "queso", "cebolla", "aceituna")
        );

        Prompt prompt = StructuredPromptProcessor.toPrompt(createRecipePrompt);

        String recipe = ollamaService.chat(prompt.text());

        System.out.println(recipe);
    }
}
