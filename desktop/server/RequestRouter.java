package server;

import com.google.gson.JsonObject;

import protocol.Json;
import protocol.Request;
import protocol.Response;

/*
 * Looks at a request's action and decides what to do with it. This is the one
 * place that maps action names ("login", "sendMessage", ...) to actual work.
 *
 * Right now it only knows "ping", which we use to check the connection is alive.
 * The real actions get added on top of this as we wire each feature to the
 * server.
 */
public class RequestRouter {

    private ChatServer server;

    public RequestRouter(ChatServer server) {
        this.server = server;
    }

    public Response handle(Request request, ClientHandler client) {
        String action = request.action;

        if (action == null) {
            return Response.fail(request.id, "no action given");
        }

        if (action.equals("ping")) {
            // just bounce back a little hello so we know the pipe works
            JsonObject data = new JsonObject();
            data.addProperty("message", "pong");
            return Response.reply(request.id, Json.toJson(data));
        }

        return Response.fail(request.id, "unknown action: " + action);
    }
}
