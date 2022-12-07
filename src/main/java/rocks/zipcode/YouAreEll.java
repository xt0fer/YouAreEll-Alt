package rocks.zipcode;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;


public class YouAreEll {
    private OkHttpClient client = new OkHttpClient();
    private final String BASE_URL = "http://zipcode.rocks:8085/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private ObjectMapper objectMapper = new ObjectMapper();

    YouAreEll() {
    }

    public ResponseBody get(String slug) throws IOException {
        Response res = client.newCall(
                new Request
                        .Builder()
                        .url(BASE_URL + slug)
                        .build()
        ).execute();
        return res.body();
    }

    public ResponseBody post(String slug, String payload) throws IOException {
        Response res = client.newCall(
                new Request
                        .Builder()
                        .url(BASE_URL + slug)
                        .post(RequestBody.create(JSON, payload))
                        .build()
        ).execute();
        return res.body();
    }

    public ResponseBody put(String slug, String payload) throws IOException {
        Response res = client.newCall(
                new Request
                        .Builder()
                        .url(BASE_URL + slug)
                        .put(RequestBody.create(JSON, payload))
                        .build()
        ).execute();
        return res.body();
    }

    public String getIds() throws IOException {
        return get("/ids").string();
    }

    public String postIds(User payload) throws IOException {
        ResponseBody res = post("ids", objectMapper.writeValueAsString(payload));
        return res.string();
    }

    public String putIds(User payload) throws IOException {
        ResponseBody b = get("ids/" + payload.getGithub());
        String bod = b.string();
        payload.setUserid(bod);
        ResponseBody res = put("ids", objectMapper.writeValueAsString(payload));
        return res.string();
    }

    public Boolean doesUserExist(String github) throws IOException {
        ResponseBody b = get("ids/" + github);
        return b.string().length() > 2;
    }

    public String postOrPutIds(User payload) throws IOException {
        if (doesUserExist(payload.getGithub())) return putIds(payload);
        return postIds(payload);
    }

    public String getMessages() throws IOException {
        return get("messages").string();
    }

    public String getMessagesMine(String github) throws IOException {
        return get("ids/"+github+"/messages").string();
    }

    public String getMessagesFromFriend(String from, String to) throws IOException {
        return get("ids/"+from+"/from/"+to).string();
    }

    public String postMessages(Message payload) throws IOException {
        return post("ids/"+payload.getFromid()+"/messages", objectMapper.writeValueAsString(payload)).string();
    }
}
