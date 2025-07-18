package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

import managers.TaskManager;
import tasks.Task;

public class HistoryHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public HistoryHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            List<Task> history = taskManager.getHistory();
            String historyJson = gson.toJson(history);

            try {
                sendText(exchange, historyJson);
            } catch (IOException exception) {
                System.out.println("Произошла ошибка " + exception.getMessage());
                sendHasErrors(exchange);
            }
        }
    }
}
