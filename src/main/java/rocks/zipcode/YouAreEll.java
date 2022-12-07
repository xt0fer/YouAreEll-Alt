package rocks.zipcode;

import com.squareup.okhttp.OkHttpClient;

public class YouAreEll {
    private OkHttpClient client = new OkHttpClient();

    private HttpController httpController;
    private IdController idController;
    private MesgController mesgController;

    YouAreEll() {
        httpController = new HttpController(this);
        idController = new IdController(httpController);
        mesgController = new MesgController(httpController);
    }

    public OkHttpClient getClient() {
        return client;
    }
    public IdController idController() {
        return this.idController;
    }
    public MesgController mesgController() {
        return this.mesgController;
    }
}
