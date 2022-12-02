package client;

public class ClientConfiguration {
<<<<<<< HEAD
	private String Host;
	private int port;
	
	public ClientConfiguration(String Host, int port) {
		super();
		this.Host = Host;
		this.port = port;
	}

	public String getHost() {
		return Host;
	}
	public void setLocalHost(String Host) {
		this.Host = Host;
=======
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
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}

	

}
