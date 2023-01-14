package client;

import models.Request;
import models.Response;

public interface IClient {
	public void setRequestForServer(Request request);
	public Response getResFromServer();
}
