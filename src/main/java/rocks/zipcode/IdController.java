package rocks.zipcode;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import rocks.zipcode.HttpController.*;

public class IdController {
    private HttpController httpController;
    ObjectMapper mapper = new ObjectMapper();

    public IdController(HttpController httpController) {
        this.httpController = httpController;
    }

    public String getIds() throws IOException {
        return httpController.get("/ids").string();
    }

    public String postIds(User payload) throws IOException {
        ResponseBody res = httpController.post("ids", mapper.writeValueAsString(payload));
        return res.string();
    }

    public String putIds(User payload) throws IOException {
        ResponseBody b = httpController.get("ids/" + payload.getGithub());
        String bod = b.string();
        payload.setUserid(bod);
        ResponseBody res = put("ids", mapper.writeValueAsString(payload));
        return res.string();
    }

    public Boolean doesUserExist(String github) throws IOException {
        ResponseBody b = httpController.get("ids/" + github);
        return b.string().length() > 2;
    }

    public String postOrPutIds(User payload) throws IOException {
        if (doesUserExist(payload.getGithub())) return putIds(payload);
        return postIds(payload);
    }


}
