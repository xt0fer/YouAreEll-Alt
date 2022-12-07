package rocks.zipcode;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MesgController {
    HttpController httpController;
    ObjectMapper mapper = new ObjectMapper();

    public MesgController(HttpController httpController) {
        this.httpController = httpController;
    }

    public String getMessages() throws IOException {
        return httpController.get("messages").string();
    }

    public String getMessagesMine(String github) throws IOException {
        return httpController.get("ids/"+github+"/messages").string();
    }

    public String getMessagesFromFriend(String from, String to) throws IOException {
        return httpController.get("ids/"+from+"/from/"+to).string();
    }

    public String postMessages(Message payload) throws IOException {
        return httpController.post("ids/"+payload.getFromid()+"/messages", mapper.writeValueAsString(payload)).string();
    }
}
