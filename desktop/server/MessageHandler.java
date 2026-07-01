package server;

import data.MessageDao;
import data.UserDao;
import model.Message;
import model.User;
import protocol.Json;
import protocol.Request;
import protocol.Response;

import java.util.ArrayList;

/*
 * Direct messages, 1 on 1. The other real time spot: when someone sends a
 * message we save it and, if the person they sent it to is online, push it
 * straight to them so it pops up right away.
 */
public class MessageHandler {

    private MessageDao messageDao = new MessageDao();
    private UserDao userDao = new UserDao();
    private ChatServer server;

    public MessageHandler(ChatServer server) {
        this.server = server;
    }

    public Response partners(Request req) {
        ArrayList<String> partners = messageDao.getPartners(req.getString("username"));
        String[] array = new String[partners.size()];
        for (int i = 0; i < partners.size(); i++) {
            array[i] = partners.get(i);
        }
        return Response.reply(req.id, Json.toJson(array));
    }

    public Response conversation(Request req) {
        ArrayList<Message> list = messageDao.getConversation(req.getString("me"), req.getString("other"));
        Message[] array = new Message[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return Response.reply(req.id, Json.toJson(array));
    }

    public Response send(Request req) {
        String sender = req.getString("sender");
        String receiver = req.getString("receiver");
        String body = req.getString("body");

        Message m = new Message(sender, receiver, body);
        messageDao.send(m);

        // if the receiver is online, push it to them now
        Response push = Response.push("newMessage", Json.toJson(m));
        server.pushTo(receiver, push);

        return Response.reply(req.id, Json.toJson(m));
    }

    // used when starting a new chat, to check the username exists
    public Response findUser(Request req) {
        User u = userDao.findByUsername(req.getString("username"));
        if (u == null) {
            return Response.fail(req.id, "No user with that username.");
        }
        return Response.reply(req.id, Json.toJson(Dto.safeUser(u)));
    }
}
