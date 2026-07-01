package server;

import com.google.gson.JsonObject;

import data.UserDao;
import model.User;
import protocol.Json;
import protocol.Request;
import protocol.Response;
import service.AuthService;
import util.EmailSender;

import java.util.HashMap;

/*
 * Handles the account actions coming over the socket: register, verify, login,
 * resend code. It leans on AuthService for the real work and just deals with
 * the request / response side of things here.
 *
 * The pending verification codes are kept in memory (username -> code) because
 * the server is one long running process. If email is set up we mail the code;
 * if not, we send it back in the reply so the client can show it (same fallback
 * idea as before, just decided on the server now).
 */
public class AuthHandler {

    private AuthService auth = new AuthService();
    private UserDao userDao = new UserDao();

    // codes we are waiting to be confirmed
    private HashMap<String, String> pendingCodes = new HashMap<String, String>();

    public Response register(Request req) {
        String name = req.getString("displayName");
        String username = req.getString("username");
        String email = req.getString("email");
        String password = req.getString("password");

        // AuthService throws IllegalArgumentException with a nice message if
        // something is wrong, and the client handler turns that into a fail
        User user = auth.register(name, username, email, password);

        String code = auth.generateVerificationCode();
        pendingCodes.put(user.getUsername(), code);

        boolean emailed = EmailSender.sendVerificationCode(user.getEmail(), code);

        JsonObject data = new JsonObject();
        data.addProperty("username", user.getUsername());
        data.addProperty("email", user.getEmail());
        data.addProperty("emailed", emailed);
        if (!emailed) {
            // no smtp set up, let the client show it so testing still works
            data.addProperty("code", code);
        }
        return Response.reply(req.id, Json.toJson(data));
    }

    public Response verify(Request req) {
        String username = req.getString("username");
        String typed = req.getString("code");

        String expected = pendingCodes.get(username);
        if (expected == null || typed == null || !typed.trim().equals(expected)) {
            return Response.fail(req.id, "That code isn't right, check again.");
        }
        auth.markVerified(username);
        pendingCodes.remove(username);

        User user = userDao.findByUsername(username);
        return Response.reply(req.id, Json.toJson(Dto.safeUser(user)));
    }

    public Response resend(Request req) {
        String username = req.getString("username");
        String email = req.getString("email");
        String code = auth.generateVerificationCode();
        pendingCodes.put(username, code);
        boolean emailed = EmailSender.sendVerificationCode(email, code);

        JsonObject data = new JsonObject();
        data.addProperty("emailed", emailed);
        if (!emailed) {
            data.addProperty("code", code);
        }
        return Response.reply(req.id, Json.toJson(data));
    }

    // login also marks this socket as belonging to that user, so the server can
    // push things to them later
    public Response login(Request req, ClientHandler client, ChatServer server) {
        String usernameOrEmail = req.getString("usernameOrEmail");
        String password = req.getString("password");

        User user = auth.login(usernameOrEmail, password);

        client.setUsername(user.getUsername());
        server.register(user.getUsername(), client);

        return Response.reply(req.id, Json.toJson(Dto.safeUser(user)));
    }
}
