import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static HttpServer httpServer;
    static TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;

        init();
    }

    public static void main(String[] args) throws IOException {
        taskManager = Managers.getDefault();
        init();
        start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public static void init() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
        httpServer.createContext("/epics", new EpicsHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void start() {
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
    }

}
