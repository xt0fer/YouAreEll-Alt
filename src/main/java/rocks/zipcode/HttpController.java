package rocks.zipcode;


import java.io.IOException;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;


public class HttpController {
    private final String BASE_URL = "http://zipcode.rocks:8085/";
    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private OkHttpClient client;

    public HttpController(YouAreEll youAreEll) {
        this.client = youAreEll.getClient();
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

}
