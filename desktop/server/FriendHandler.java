package server;

import com.google.gson.JsonObject;

import data.FriendDao;
import data.UserDao;
import model.User;
import protocol.Json;
import protocol.Request;
import protocol.Response;

import java.util.ArrayList;

/*
 * Friend requests and the friends list. This is what stops random people from
 * messaging you: you can only DM someone you are actually friends with.
 *
 * When a request is sent or accepted we push a little event to the other person
 * (if they are online) so it can turn into a notification for them right away.
 */
public class FriendHandler {

    private FriendDao friendDao = new FriendDao();
    private UserDao userDao = new UserDao();
    private ChatServer server;

    public FriendHandler(ChatServer server) {
        this.server = server;
    }

    public Response sendRequest(Request req) {
        String from = req.getString("from");
        String to = req.getString("to");

        if (to == null || to.trim().isEmpty()) {
            return Response.fail(req.id, "Type a username.");
        }
        if (to.equals(from)) {
            return Response.fail(req.id, "You can't add yourself :)");
        }
        User target = userDao.findByUsername(to);
        if (target == null) {
            return Response.fail(req.id, "No user called '" + to + "'.");
        }
        String relation = friendDao.relationFor(from, to);
        if (relation.equals("accepted")) {
            return Response.fail(req.id, "You're already friends.");
        }
        if (relation.equals("outgoing")) {
            return Response.fail(req.id, "You already sent them a request.");
        }
        if (relation.equals("incoming")) {
            // they already asked us, so just accept instead of a second request
            friendDao.accept(to, from);
            pushTo(to, "friendAccepted", from);
            return Response.reply(req.id, "{\"accepted\":true}");
        }

        friendDao.request(from, to);
        pushTo(to, "friendRequest", from);
        return Response.reply(req.id, "{\"sent\":true}");
    }

    public Response respond(Request req) {
        String me = req.getString("me");
        String requester = req.getString("requester");
        boolean accept = req.getInt("accept") == 1;

        if (accept) {
            friendDao.accept(requester, me);
            pushTo(requester, "friendAccepted", me);
        } else {
            friendDao.decline(requester, me);
        }
        return Response.reply(req.id, "{\"ok\":true}");
    }

    public Response friends(Request req) {
        return names(req.id, friendDao.getFriends(req.getString("username")));
    }

    public Response requests(Request req) {
        return names(req.id, friendDao.getIncomingRequests(req.getString("username")));
    }

    public Response status(Request req) {
        String relation = friendDao.relationFor(req.getString("me"), req.getString("other"));
        return Response.reply(req.id, "{\"relation\":\"" + relation + "\"}");
    }

    // ---- helpers ----

    private void pushTo(String username, String event, String who) {
        JsonObject data = new JsonObject();
        data.addProperty("from", who);
        server.pushTo(username, Response.push(event, Json.toJson(data)));
    }

    private Response names(int id, ArrayList<String> list) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return Response.reply(id, Json.toJson(array));
    }
}
