package handlers;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.util.List;

import managers.TaskManager;
import tasks.Task;


public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private TaskManager taskManager;
    private Gson gson;

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (exchange.getRequestMethod().equals("GET")) {
            List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

            try {
                sendText(exchange, gson.toJson(prioritizedTasks));
            } catch (IOException exception) {
                System.out.println("Произошла ошибка " + exception.getMessage());
                sendHasErrors(exchange);
            }
        }
    }
}
