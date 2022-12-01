package client;

public class ClientConfiguration {
	private String localHost;
	private int port;
	
	public ClientConfiguration(String localHost, int port) {
		super();
		this.localHost = localHost;
		this.port = port;
	}

	public String getLocalHost() {
		return localHost;
	}
	public void setLocalHost(String localHost) {
		this.localHost = localHost;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	

}
