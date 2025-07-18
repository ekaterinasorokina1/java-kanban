import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;
import tasks.TaskType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import managers.InMemoryTaskManager;
import managers.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.DurationAdapter;
import utils.LocalDateTimeAdapter;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class HttpEpicHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson;

    public HttpEpicHandlerTest() throws IOException {
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
    public void testGetEpics() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull(epicsFromResponse, "Эпики не возвращаются");
        assertEquals(1, epicsFromResponse.size(), "Некорректное количество эпиков");
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic epic = gson.fromJson(response.body(), Epic.class);

        assertNotNull(epic, "Эпик не возвращается");
        assertEquals("Тестовое задание", epic.getName(), "Некорректное имя эпика");
    }

    @Test
    public void testGetEpicSubtaskList() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 9 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1/subtasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtasksFromResponse = gson.fromJson(response.body(), new EpicListTypeToken().getType());

        assertNotNull(subtasksFromResponse, "Подзадачи эпика не возвращаются");
        assertEquals(1, subtasksFromResponse.size(), "Некорректное количество подзадач");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epicStart = new Epic("Тестовое задание", "Нужно решить тесторовое задание 4 спринта", TaskStatus.NEW, TaskType.EPIC, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 7, 0));
        taskManager.createEpic(epicStart);
        Subtask subtask1 = new Subtask("ТЗ", "Отзнакомиться с ТЗ", TaskStatus.NEW, epicStart.getId(), TaskType.SUBTASK, Duration.ofMinutes(90), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createSubtask(subtask1);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic epic = taskManager.getEpicById(1);
        assertNull(epic, "Эпик не удалился");
    }
}

class EpicListTypeToken extends TypeToken<List<Epic>> {
}