package handlers;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    protected void sendText(HttpExchange h, String text) throws IOException {
        byte[] resp = text.getBytes(DEFAULT_CHARSET);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(200, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        makeResponse(h, 404);
    }

    protected void sendHasInteractions(HttpExchange h) throws IOException {
        makeResponse(h, 406);
    }

    protected void sendHasErrors(HttpExchange h) throws IOException {
        makeResponse(h, 500);
    }

    protected void sendSuccess(HttpExchange h) throws IOException {
        makeResponse(h, 201);
    }

    private void makeResponse(HttpExchange h, int code) throws IOException {
        h.sendResponseHeaders(code, 0);
        h.close();
    }
}
