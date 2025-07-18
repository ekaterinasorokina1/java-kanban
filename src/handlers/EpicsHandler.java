package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

import managers.TaskManager;
import tasks.Epic;
import tasks.Subtask;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    private TaskManager taskManager;
    private Gson gson;

    public EpicsHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String[] pathParts = path.split("/");

        try {
            if (method.equals("GET")) {
                if (pathParts.length == 4) {
                    handleGetEpicSubtasks(exchange, Integer.parseInt(pathParts[2]));
                }
                if (pathParts.length == 3) {
                    handleGetEpicById(exchange, Integer.parseInt(pathParts[2]));
                } else {
                    handleGetEpics(exchange);
                }
            }
            if (method.equals("POST")) {
                handleCreateEpic(exchange);
            }
            if (method.equals("DELETE")) {
                handleDeleteEpic(exchange, Integer.parseInt(pathParts[2]));
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка " + exception.getMessage());
        }
    }

    private void handleGetEpics(HttpExchange exchange) throws IOException {
        List<Epic> epics = taskManager.getEpicList();
        String epicJson = gson.toJson(epics);

        try {
            sendText(exchange, epicJson);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения списка эпиков " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleGetEpicById(HttpExchange exchange, int epicId) throws IOException {
        Epic epic = taskManager.getEpicById(epicId);

        try {
            if (epic != null) {
                sendText(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange);
            }
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения эпика " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleGetEpicSubtasks(HttpExchange exchange, int epicId) throws IOException {
        List<Subtask> subtasks = taskManager.getEpicSubtaskList(epicId);

        try {
            sendText(exchange, gson.toJson(subtasks));
        } catch (IOException exception) {
            System.out.println("Произошла ошибка получения списка подзадач эпика " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();

        try {
            String body = new String(inputStream.readAllBytes(), DEFAULT_CHARSET);
            Epic epic = gson.fromJson(body, Epic.class);
            taskManager.createEpic(epic);
            sendSuccess(exchange);
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при создании подзадачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }

    private void handleDeleteEpic(HttpExchange exchange, int epicId) throws IOException {
        taskManager.removeEpicById(epicId);

        try {
            exchange.sendResponseHeaders(200, 0);
            exchange.close();
        } catch (IOException exception) {
            System.out.println("Произошла ошибка при удалении подзадачи " + exception.getMessage());
            sendHasErrors(exchange);
        }
    }
}
