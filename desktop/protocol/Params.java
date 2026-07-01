package protocol;

import com.google.gson.JsonObject;

/*
 * A tiny builder so we can put together the data part of a request without
 * writing out a JsonObject by hand every time.
 *
 *   Params.of().put("username", name).put("password", pass).json()
 */
public class Params {

    private JsonObject obj = new JsonObject();

    public static Params of() {
        return new Params();
    }

    public Params put(String key, String value) {
        obj.addProperty(key, value);
        return this;
    }

    public Params put(String key, int value) {
        obj.addProperty(key, value);
        return this;
    }

    public JsonObject json() {
        return obj;
    }
}
