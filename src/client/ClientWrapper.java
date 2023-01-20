package client;

import models.Request;
import models.Response;

public class ClientWrapper implements IClient {

    @Override
    public void setRequestForServer(Request request) {
        ClientUI.chat.accept(request);
    }

    @Override
    public Response getResFromServer() {
        return Client.resFromServer;
    }

}
