import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class HttpHistoryHandlerTest {
    TaskManager taskManager = new InMemoryTaskManager();
    HttpTaskServer taskServer = new HttpTaskServer(taskManager);
    Gson gson;

    public HttpHistoryHandlerTest() throws IOException {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gson = gsonBuilder
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @BeforeEach
    public void setUp() {
        HttpTaskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }


    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task2 = new Task("Тест", "Проверить, что последняя задача, которую посмотрели, идет первой в списке", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(20), LocalDateTime.of(2025, 6, 19, 9, 0));
        taskManager.createTask(task2);
        taskManager.getTaskById(task2.getId());
        Task task1 = new Task("task2", "descrip2", TaskStatus.NEW, TaskType.TASK, Duration.ofMinutes(30), LocalDateTime.of(2025, 6, 19, 10, 0));
        taskManager.createTask(task1);
        taskManager.getTaskById(task1.getId());

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/history");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksHistory = gson.fromJson(response.body(), new EpicListTypeToken().getType());
        assertNotNull(tasksHistory, "История не возвращаются");
        assertEquals(2, tasksHistory.size(), "Некорректное количество задач в списке");
        assertEquals(2, tasksHistory.get(0).getId(), "Некорректный id задачи в первом списке");
    }

}
