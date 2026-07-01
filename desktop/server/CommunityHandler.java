package server;

import data.CommunityDao;
import data.UserDao;
import model.Community;
import model.User;
import protocol.Json;
import protocol.Request;
import protocol.Response;
import service.CommunityService;
import service.MatchService;

import java.util.ArrayList;

/*
 * All the community actions: listing, searching, the matched home feed, and
 * joining / leaving. These just call the same service and dao classes we
 * already had, the only new thing is packing the result into json.
 */
public class CommunityHandler {

    private CommunityService communities = new CommunityService();
    private CommunityDao communityDao = new CommunityDao();
    private MatchService matcher = new MatchService();
    private UserDao userDao = new UserDao();

    public Response list(Request req) {
        return listResponse(req.id, communities.getAll());
    }

    public Response get(Request req) {
        Community c = communities.findById(req.getInt("id"));
        if (c == null) {
            return Response.fail(req.id, "community not found");
        }
        return Response.reply(req.id, Json.toJson(c));
    }

    public Response byCategory(Request req) {
        return listResponse(req.id, communities.getByCategory(req.getString("category")));
    }

    public Response search(Request req) {
        return listResponse(req.id, communities.search(req.getString("text")));
    }

    public Response joined(Request req) {
        return listResponse(req.id, communities.getJoined(req.getString("username")));
    }

    public Response isMember(Request req) {
        boolean member = communities.isMember(req.getString("username"), req.getInt("communityId"));
        return Response.reply(req.id, "{\"member\":" + member + "}");
    }

    public Response join(Request req) {
        communities.join(req.getString("username"), req.getInt("communityId"));
        return Response.reply(req.id, "{\"ok\":true}");
    }

    public Response leave(Request req) {
        communities.leave(req.getString("username"), req.getInt("communityId"));
        return Response.reply(req.id, "{\"ok\":true}");
    }

    // the home feed: score every community this user hasnt joined and return
    // the best ones, with the match percent already filled in
    public Response homeMatches(Request req) {
        String username = req.getString("username");
        User user = userDao.findByUsername(username);
        if (user == null) {
            return Response.fail(req.id, "user not found");
        }
        ArrayList<Community> all = communities.getAll();
        ArrayList<Community> notJoined = new ArrayList<Community>();
        for (int i = 0; i < all.size(); i++) {
            if (!communities.isMember(username, all.get(i).getId())) {
                notJoined.add(all.get(i));
            }
        }
        ArrayList<Community> top = matcher.topMatches(user, notJoined, 6);
        return listResponse(req.id, top);
    }

    // score a single community for a user (used on the detail page)
    public Response scoreOne(Request req) {
        String username = req.getString("username");
        User user = userDao.findByUsername(username);
        Community c = communities.findById(req.getInt("communityId"));
        if (user == null || c == null) {
            return Response.fail(req.id, "not found");
        }
        matcher.scoreFor(user, c);
        return Response.reply(req.id, Json.toJson(c));
    }

    // helper: send an array of communities
    private Response listResponse(int id, ArrayList<Community> list) {
        Community[] array = new Community[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return Response.reply(id, Json.toJson(array));
    }
}
