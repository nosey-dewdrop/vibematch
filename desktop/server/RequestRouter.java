package server;

import com.google.gson.JsonObject;

import protocol.Json;
import protocol.Request;
import protocol.Response;

/*
 * Looks at a request's action and sends it to the right handler. This is the
 * one place that maps action names to actual work, so its a good map of
 * everything the server can do.
 *
 * The handlers are grouped: auth, profile (interests + mbti), communities,
 * forum, messages.
 */
public class RequestRouter {

    private ChatServer server;
    private AuthHandler authHandler = new AuthHandler();
    private ProfileHandler profileHandler = new ProfileHandler();
    private CommunityHandler communityHandler = new CommunityHandler();
    private ForumHandler forumHandler;
    private MessageHandler messageHandler;
    private FriendHandler friendHandler;
    private NotificationHandler notificationHandler = new NotificationHandler();

    public RequestRouter(ChatServer server) {
        this.server = server;
        this.forumHandler = new ForumHandler(server);
        this.messageHandler = new MessageHandler(server);
        this.friendHandler = new FriendHandler(server);
    }

    public Response handle(Request request, ClientHandler client) {
        String action = request.action;
        if (action == null) {
            return Response.fail(request.id, "no action given");
        }

        // connection check
        if (action.equals("ping")) {
            JsonObject data = new JsonObject();
            data.addProperty("message", "pong");
            return Response.reply(request.id, Json.toJson(data));
        }

        // auth
        if (action.equals("register")) {
            return authHandler.register(request);
        }
        if (action.equals("verify")) {
            return authHandler.verify(request, client, server);
        }
        if (action.equals("resend")) {
            return authHandler.resend(request);
        }
        if (action.equals("login")) {
            return authHandler.login(request, client, server);
        }

        // profile / onboarding
        if (action.equals("setInterests")) {
            return profileHandler.setInterests(request);
        }
        if (action.equals("submitMbti")) {
            return profileHandler.submitMbti(request);
        }
        if (action.equals("getUser")) {
            return profileHandler.getUser(request);
        }

        // communities
        if (action.equals("communities.list")) {
            return communityHandler.list(request);
        }
        if (action.equals("communities.get")) {
            return communityHandler.get(request);
        }
        if (action.equals("communities.byCategory")) {
            return communityHandler.byCategory(request);
        }
        if (action.equals("communities.search")) {
            return communityHandler.search(request);
        }
        if (action.equals("communities.joined")) {
            return communityHandler.joined(request);
        }
        if (action.equals("communities.isMember")) {
            return communityHandler.isMember(request);
        }
        if (action.equals("communities.join")) {
            return communityHandler.join(request);
        }
        if (action.equals("communities.leave")) {
            return communityHandler.leave(request);
        }
        if (action.equals("communities.homeMatches")) {
            return communityHandler.homeMatches(request);
        }
        if (action.equals("communities.scoreOne")) {
            return communityHandler.scoreOne(request);
        }

        // forum
        if (action.equals("forum.posts")) {
            return forumHandler.posts(request);
        }
        if (action.equals("forum.createPost")) {
            return forumHandler.createPost(request);
        }
        if (action.equals("forum.comments")) {
            return forumHandler.comments(request);
        }
        if (action.equals("forum.addComment")) {
            return forumHandler.addComment(request);
        }

        // friends
        if (action.equals("friends.request")) {
            return friendHandler.sendRequest(request);
        }
        if (action.equals("friends.respond")) {
            return friendHandler.respond(request);
        }
        if (action.equals("friends.list")) {
            return friendHandler.friends(request);
        }
        if (action.equals("friends.requests")) {
            return friendHandler.requests(request);
        }
        if (action.equals("friends.status")) {
            return friendHandler.status(request);
        }

        // notifications
        if (action.equals("notifications.list")) {
            return notificationHandler.list(request);
        }
        if (action.equals("notifications.unread")) {
            return notificationHandler.unread(request);
        }
        if (action.equals("notifications.markRead")) {
            return notificationHandler.markRead(request);
        }

        // messages
        if (action.equals("messages.partners")) {
            return messageHandler.partners(request);
        }
        if (action.equals("messages.conversation")) {
            return messageHandler.conversation(request);
        }
        if (action.equals("messages.send")) {
            return messageHandler.send(request);
        }
        if (action.equals("messages.findUser")) {
            return messageHandler.findUser(request);
        }

        return Response.fail(request.id, "unknown action: " + action);
    }
}
