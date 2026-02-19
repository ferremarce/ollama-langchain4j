/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ollama;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import ollama.OllamaAiService.ClasificacionOdsMultipleResponse;

/**
 *
 * @author jmferreira
 */
public class OllamaAiService {

    static OllamaChatService chatService
            = new OllamaChatService(
                    "http://10.254.3.242:11434",
                    //"gpt-oss"
                    "gemma3:12b-cloud"
            );

    interface NumberExtractor {

        @UserMessage("Extract number from {{it}}")
        int extractInt(String text);

        @UserMessage("Extract number from {{it}}")
        long extractLong(String text);

        @UserMessage("Extract number from {{it}}")
        BigInteger extractBigInteger(String text);

        @UserMessage("Extract number from {{it}}")
        float extractFloat(String text);

        @UserMessage("Extract number from {{it}}")
        double extractDouble(String text);

        @UserMessage("Extract number from {{it}}")
        BigDecimal extractBigDecimal(String text);
    }

    public static void main1(String[] args) {

        NumberExtractor extractor = AiServices.create(NumberExtractor.class, chatService.getChatModel());

        String text = "After countless millennia of computation, the supercomputer Deep Thought finally announced "
                + "that the answer to the ultimate question of life, the universe, and everything was forty two.";

        int intNumber = extractor.extractInt(text);
        System.out.println(intNumber); // 42

        long longNumber = extractor.extractLong(text);
        System.out.println(longNumber); // 42

        BigInteger bigIntegerNumber = extractor.extractBigInteger(text);
        System.out.println(bigIntegerNumber); // 42

        float floatNumber = extractor.extractFloat(text);
        System.out.println(floatNumber); // 42.0

        double doubleNumber = extractor.extractDouble(text);
        System.out.println(doubleNumber); // 42.0

        BigDecimal bigDecimalNumber = extractor.extractBigDecimal(text);
        System.out.println(bigDecimalNumber); // 42.0
    }

    public enum IssueCategory {
        MAINTENANCE_ISSUE,
        SERVICE_ISSUE,
        COMFORT_ISSUE,
        FACILITY_ISSUE,
        CLEANLINESS_ISSUE,
        CONNECTIVITY_ISSUE,
        CHECK_IN_ISSUE,
        OVERALL_EXPERIENCE_ISSUE
    }

    interface HotelReviewIssueAnalyzer {

        @UserMessage("Please analyse the following review: |||{{it}}|||")
        List<IssueCategory> analyzeReview(String review);
    }

    public static void main2(String[] args) {

        HotelReviewIssueAnalyzer hotelReviewIssueAnalyzer = AiServices.create(HotelReviewIssueAnalyzer.class, chatService.getChatModel());

        String review = "Our stay at hotel was a mixed experience. The location was perfect, just a stone's throw away "
                + "from the beach, which made our daily outings very convenient. The rooms were spacious and well-decorated, "
                + "providing a comfortable and pleasant environment. However, we encountered several issues during our "
                + "stay. The air conditioning in our room was not functioning properly, making the nights quite uncomfortable. "
                + "Additionally, the room service was slow, and we had to call multiple times to get extra towels. Despite the "
                + "friendly staff and enjoyable breakfast buffet, these issues significantly impacted our stay.";

        List<IssueCategory> issueCategories = hotelReviewIssueAnalyzer.analyzeReview(review);

        // Should output [MAINTENANCE_ISSUE, SERVICE_ISSUE, COMFORT_ISSUE, OVERALL_EXPERIENCE_ISSUE]
        System.out.println(issueCategories);
    }

    public enum OdsObjetivo {

        ODS_1_FIN_DE_LA_POBREZA,
        ODS_2_HAMBRE_CERO,
        ODS_3_SALUD_Y_BIENESTAR,
        ODS_4_EDUCACION_DE_CALIDAD,
        ODS_5_IGUALDAD_DE_GENERO,
        ODS_6_AGUA_LIMPIA_Y_SANEAMIENTO,
        ODS_7_ENERGIA_ASEQUIBLE_Y_NO_CONTAMINANTE,
        ODS_8_TRABAJO_DECENTE_Y_CRECMIENTO_ECONOMICO,
        ODS_9_INDUSTRIA_INNOVACION_E_INFRAESTRUCTURA,
        ODS_10_REDUCCION_DE_LAS_DESIGUALDADES,
        ODS_11_CIUDADES_Y_COMUNIDADES_SOSTENIBLES,
        ODS_12_PRODUCCION_Y_CONSUMO_RESPONSABLE,
        ODS_13_ACCION_POR_EL_CLIMA,
        ODS_14_VIDA_SUBMARINA,
        ODS_15_VIDA_DE_ECOSISTEMAS_TERRESTRES,
        ODS_16_PAZ_JUSTICIA_E_INSTITUCIONES_SOLIDAS,
        ODS_17_ALIANZAS_PARA_LOGRAR_LOS_OBJETIVOS
    }

    public interface OdsClassifierService {

        @SystemMessage("""
        Eres un experto en los 17 Objetivos de Desarrollo Sostenible (ODS).
        
        TAREA:
        - Analizar el texto.
        - Identificar hasta un máximo de 3 ODS relevantes.
        - Ordenarlos por relevancia (el más importante primero).
        - Para cada ODS proporcionar:
            - objetivo (usar solo valores del enum)
            - justificación breve en no mas de 50 palabras
            - confianza entre 0 y 100
        
        REGLAS:
        - Nunca devolver más de 3 ODS.
        - Si solo corresponde 1 o 2, devolver solo esos.
        - No inventar ODS.
        - Tu respuesta debe ser exclusivamente un objeto JSON válido.
    """)
        ClasificacionOdsMultipleResponse clasificar(
                @UserMessage String texto
        );
    }

    public record ClasificacionOdsMultipleResponse(List<OdsResultado> lista) {

        @Override
        public String toString() {
            String cadena = "";
            for (OdsResultado odsr : lista) {
                cadena += odsr;
            }
            return cadena;
        }

    }

    public record OdsResultado(OdsObjetivo objetivo, String justificacion, double confianza) {

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("OdsResultado{");
            sb.append("objetivo=").append(objetivo);
            sb.append(", justificacion=").append(justificacion);
            sb.append(", confianza=").append(confianza);
            sb.append('}');
            return sb.toString();
        }

    }

    public static void main(String[] args) throws JsonProcessingException {

        OdsClassifierService clasificador = AiServices.create(OdsClassifierService.class, chatService.getChatModel());

        String review = "El Paraguay enfrenta una crisis estructural del sistema previsional que no se resuelve\n"
                + "con ajustes parciales ni con reformas aisladas de caj as específicas. Actualmente, menos del 30o¿\n"
                + "de la población económicamente activa accede a unajubilación, mientras que la gran mayoría\n"
                + "de los paraguayos, trabajadores informales, independientes, rurales y cuentapropistas, aportan\n"
                + "al Estado toda su vida a través de impuestos indirectos, especialmente el IVA, sin derecho\n"
                + "alguno a una vejez protegida.\n"
                + "Este proyecto parte de una premisa simple y justa, como el hecho que no puede existir\n"
                + "un país donde todos pagan impuestos, pero solo algunos tienen derecho ajubilarse. La refbrma\n"
                + "de la Caja Fiscal, del IPS o de cualquier caja previsional no puede ser el fin del debate, sino el\n"
                + "inicio de una transformación profunda hacia un sistema previsional universal, sostenible y\n"
                + "solidario, acorde a la realidad económica y laboral del Paraguay.\n"
                + "La presente ley crea el Sistema Nacional de Jubilación Universal y Contributiva, que\n"
                + "entre otras cosas: Amplía derechos sin eliminar los existentes, Incorpora progresivamente a los\n"
                + "excluidos, Establece un aporte explícito del Estado, Reduce la inlormalidad mediante\n"
                + "incentivos, no castigos y Utiliza fuentes no salariales, como renta energética y recursos tiscales\n"
                + "generales.\n"
                + "Este modelo se alinea con los principios constitucionales de igualdad, justicia social,\n"
                + "dignidad humana y protección a la vejez, y con estándares intemacionales de seguridad social.\n"
                + "Desde el punto de vista social, debemos reconocer que hoy el sistema castiga al\n"
                + "trabajador honesto que nunca pudo formalizarse y esta ley corrige una injusticia histórica.\n"
                + "Así también, la informalidad no se combate con exclusión, sino con incentivos\n"
                + "previsionales reales, y en este sentido, lógicamente con más aportantes existirá más\n"
                + "sostenibilidad.\n"
                + "El Estado ya recauda de todos a través del lmpuesto al Valor Agregado (lVA) y este\n"
                + "proyecto transparenta y ordena ese aporte en favor de la vejez.\n"
                + "Para terminar, el argumento político válido es que no es una ley de izquierda ni de\n"
                + "derecha, sino una ley de sentido común yjust¡cia soc¡al.\n"
                + "No estamos discutiendo números, estamos discutiendo qué país queremos ser cuando\n"
                + "nuestros ciudadanos envejezcan, un país donde solo algunos se jubilan, o un país donde todos\n"
                + "tienen derecho a una vejez digna.";

        ClasificacionOdsMultipleResponse respuesta = clasificador.clasificar(review);
        // En tu main, limpia manualmente:
//        String response = clasificador.clasificar(review);
//        String cleanJson = response.replaceAll("(?s)```json\\s*|```|\\n", "").trim();
//        ObjectMapper mapper = new ObjectMapper();
//        OdsResultado[] usersArray = mapper.readValue(cleanJson, OdsResultado[].class);

        System.out.println(respuesta);
    }
}
