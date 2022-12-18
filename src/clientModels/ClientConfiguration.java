package clientModels;

//ClientConfiguration class - sets a configuration for client connection to server request
public class ClientConfiguration {
	private String Host;
	private int port;
	
	public ClientConfiguration(String Host, int port) {
		super();
		this.Host = Host;
		this.port = port;
	}

	//getters and setters
	public String getHost() {
		return Host;
	}
	public void setLocalHost(String Host) {
		this.Host = Host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	

}
