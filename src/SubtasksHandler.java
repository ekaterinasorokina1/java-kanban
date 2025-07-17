import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    TaskManager taskManager;

    Gson gson;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET")) {
                if (pathParts.length == 3) {
                    handleGetSubtaskById(exchange, Integer.parseInt(pathParts[2]));
                } else {
                    handleGetSubtasks(exchange);
                }
            }
            if (method.equals("POST")) {
                handleCreateSubtask(exchange);
            }
            if (method.equals("DELETE")) {
                handleDeleteSubtask(exchange, Integer.parseInt(pathParts[2]));
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка " + exception.getMessage());
        }
    }

    private void handleGetSubtasks(HttpExchange exchange) throws IOException {
        List<Subtask> subtasks = taskManager.getSubtaskList();

        try {
            sendText(exchange, gson.toJson(subtasks));
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения списка подзадач " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleGetSubtaskById(HttpExchange exchange, int subtaskId) throws IOException {
        Subtask subtask = taskManager.getSubtaskById(subtaskId);

        try {
            if (subtask != null) {
                sendText(exchange, gson.toJson(subtask));
            } else {
                sendNotFound(exchange);
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения подзадачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();

        try {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Subtask subtask = gson.fromJson(body, Subtask.class);

            boolean isTaskIntersected;

            if (subtask.getId() == 0) {
                isTaskIntersected = taskManager.createSubtask(subtask);
            } else {
                isTaskIntersected = taskManager.updateSubtask(subtask);
            }

            if (isTaskIntersected) {
                sendHasInteractions(exchange);
            } else {
                sendSuccess(exchange);
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при создании подзадачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }


    private void handleDeleteSubtask(HttpExchange exchange, int subtaskId) throws IOException {
        taskManager.removeSubtaskById(subtaskId);

        try {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при удалении подзадачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }
}
