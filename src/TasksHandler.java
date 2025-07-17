import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class TasksHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    Gson gson;

    TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
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
                    handleGetTaskById(exchange, Integer.parseInt(pathParts[2]));
                } else {
                    handleGetTasks(exchange);
                }
            }
            if (method.equals("POST")) {
                handleCreateTask(exchange);
            }
            if (method.equals("DELETE")) {
                handleDeleteTask(exchange, Integer.parseInt(pathParts[2]));
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка " + exception.getMessage());
        }
    }

    private void handleGetTasks(HttpExchange exchange) throws IOException {
        List<Task> tasks = taskManager.getTaskList();

        try {
            sendText(exchange, gson.toJson(tasks));
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения списка задач " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleGetTaskById(HttpExchange exchange, int taskId) throws IOException {
        Task task = taskManager.getTaskById(taskId);

        try {
            if (task != null) {
                sendText(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange);
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения задачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();

        try {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Task task = gson.fromJson(body, Task.class);

            boolean isTaskIntersected;

            if (task.getId() == 0) {
                isTaskIntersected = taskManager.createTask(task);
            } else {
                isTaskIntersected = taskManager.updateTask(task);
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


    private void handleDeleteTask(HttpExchange exchange, int taskId) throws IOException {
        taskManager.removeTaskById(taskId);

        try {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при удалении задачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }
}
