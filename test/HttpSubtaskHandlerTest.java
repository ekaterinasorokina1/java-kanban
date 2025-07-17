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

public class HttpSubtaskHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson;

    public HttpSubtaskHandlerTest() throws IOException {
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
    public void testGetSubtasks() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("ТЗ2", "Проверить дату эпика", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 12, 0));
        taskManager.createSubtask(subtask2);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new SubtasksListTypeToken().getType());

        assertNotNull(subtasksFromResponse, "Подзадачи не возвращаются");
        assertEquals(2, subtasksFromResponse.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        String stringSubtask = gson.toJson(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(stringSubtask))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> tasksFromManager = taskManager.getSubtaskList();
        assertNotNull(tasksFromManager, "Подзадачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество подзадач");
        assertEquals("ТЗ", tasksFromManager.get(0).getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtaskById() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(subtask, "Подзадача не возвращается");
        assertEquals("ТЗ", subtask.getName(), "Некорректное имя подзадачи");
    }

    @Test
    public void testGetSubtaskByIdNotExisted() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());

        Subtask subtask = gson.fromJson(response.body(), Subtask.class);

        assertNull(subtask, "Подзадача не должна возвращается");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Subtask subtask = taskManager.getSubtaskById(2);
        assertNull(subtask, "Задача не удалилась");
    }
}

class SubtasksListTypeToken extends TypeToken<List<Subtask>> {
}
