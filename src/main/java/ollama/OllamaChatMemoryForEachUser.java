package ollama;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.mapdb.DB;
import org.mapdb.DBMaker;
import static org.mapdb.Serializer.INTEGER;
import static org.mapdb.Serializer.STRING;
import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;

public class OllamaChatMemoryForEachUser {
    interface Assistant {

        String chat(@MemoryId int memoryId, @UserMessage String userMessage);
    }

    public static void main(String[] args) {

        PersistentChatMemoryStore2 store = new PersistentChatMemoryStore2();

        ChatMemoryProvider chatMemoryProvider = memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(store)
                .build();
        OllamaChatService ollamaService = new OllamaChatService(
                "http://10.254.3.242:11434",
                "gemma3:4b");

        Assistant assistant = AiServices.builder(Assistant.class)
                .chatModel(ollamaService.getChatModel())
                .chatMemoryProvider(chatMemoryProvider)
                .build();

        System.out.println(assistant.chat(1, "Hola, mi nombre es José"));
        System.out.println(assistant.chat(2, "Hola, me llamo Jannet y tengo 25 años"));

        // Now, comment out the two lines above, uncomment the two lines below, and run
        // again.

        System.out.println(assistant.chat(1, "Cúal es mi nombre?"));
        System.out.println(assistant.chat(2, "Dime mi nombre y mi edad"));
    }

    // You can create your own implementation of ChatMemoryStore and store chat
    // memory whenever you'd like
    static class PersistentChatMemoryStore implements ChatMemoryStore {

        private final DB db = DBMaker.fileDB("multi-user-chat-memory.db").transactionEnable().make();
        private final Map<Integer, String> map = db.hashMap("messages", INTEGER, STRING).createOrOpen();

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            String json = map.get((int) memoryId);
            return messagesFromJson(json);
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            map.put((int) memoryId, json);
            db.commit();
        }

        @Override
        public void deleteMessages(Object memoryId) {
            map.remove((int) memoryId);
            db.commit();
        }
    }

    static class PersistentChatMemoryStore2 implements ChatMemoryStore {

        private final String url = "jdbc:mysql://localhost:3306/chat_memory_db?createDatabaseIfNotExist=true";
        private final String user = "root";
        private final String password = "mysql"; // Configure with your MySQL password

        public PersistentChatMemoryStore2() {
            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", user, password);
                    Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS chat_memory_db");
            } catch (SQLException e) {
                // If the user doesn't have privilege to create DB or it already exists, ignore
                // or handle.
            }

            try (Connection conn = DriverManager.getConnection(url, user, password);
                    Statement stmt = conn.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS messages (" +
                        "memory_id INT PRIMARY KEY, " +
                        "json_messages TEXT" +
                        ")");
            } catch (SQLException e) {
                throw new RuntimeException("Failed to initialize database table", e);
            }
        }

        @Override
        public List<ChatMessage> getMessages(Object memoryId) {
            try (Connection conn = DriverManager.getConnection(url, user, password);
                    PreparedStatement stmt = conn
                            .prepareStatement("SELECT json_messages FROM messages WHERE memory_id = ?")) {
                stmt.setInt(1, (int) memoryId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    String json = rs.getString("json_messages");
                    if (json != null && !json.trim().isEmpty()) {
                        return messagesFromJson(json);
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException("Failed to load messages", e);
            }
            return new ArrayList<>();
        }

        @Override
        public void updateMessages(Object memoryId, List<ChatMessage> messages) {
            String json = messagesToJson(messages);
            try (Connection conn = DriverManager.getConnection(url, user, password);
                    PreparedStatement stmt = conn.prepareStatement(
                            "INSERT INTO messages (memory_id, json_messages) VALUES (?, ?) " +
                                    "ON DUPLICATE KEY UPDATE json_messages = ?")) {
                stmt.setInt(1, (int) memoryId);
                stmt.setString(2, json);
                stmt.setString(3, json);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to update messages", e);
            }
        }

        @Override
        public void deleteMessages(Object memoryId) {
            try (Connection conn = DriverManager.getConnection(url, user, password);
                    PreparedStatement stmt = conn.prepareStatement("DELETE FROM messages WHERE memory_id = ?")) {
                stmt.setInt(1, (int) memoryId);
                stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to delete messages", e);
            }
        }
    }
}
