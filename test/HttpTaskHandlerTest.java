import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpTaskHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson;

    public HttpTaskHandlerTest() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() {
        taskManager.removeAllTasks();
        taskManager.removeAllSubtasks();
        taskManager.removeAllEpics();
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    public void testGetTasks() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "descrip1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 19, 8, 0));
        taskManager.createTask(task1);
        Task task2 = new Task("task2", "descrip2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createTask(task2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromResponse = gson.fromJson(response.body(), new TasksListTypeToken().getType());

        assertNotNull(tasksFromResponse, "Задачи не возвращаются");
        assertEquals(2, tasksFromResponse.size(), "Некорректное количество задач");
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "descrip1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.now());
        String stringTask = gson.toJson(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringTask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getTaskList();
        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals("task1", tasksFromManager.get(0).getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "descrip1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 19, 8, 0));
        taskManager.createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task task = gson.fromJson(response.body(), Task.class);

        assertNotNull(task, "Задача не возвращается");
        assertEquals("task1", task.getName(), "Некорректное имя задачи");
    }

    @Test
    public void testGetTaskByIdNotExisted() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        Task task = gson.fromJson(response.body(), Task.class);

        assertNull(task, "Задача не должна возвращается");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task1 = new Task("task1", "descrip1", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 19, 8, 0));
        taskManager.createTask(task1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task task = taskManager.getTaskById(1);
        assertNull(task, "Задача не удалилась");
    }
}

class TasksListTypeToken extends TypeToken<List<Task>> {
}
