package client;

import gui.SubscribersController;
import models.Response;
import ocsf.client.AbstractClient;

import java.io.IOException;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
 */
public class Client extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	public static boolean awaitResponse = false;
	public static Response resFromServer;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 */

	public Client(String host, int port) throws IOException {
		super(host, port); // Call the superclass constructor
		openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server. Added the static
	 * response object.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {

		resFromServer = (Response) msg;
		awaitResponse = false;
		System.out.println("Got response from server");
	}

	/**
	 * This method handles all data coming from the UI
	 *
	 * @param message The message from the UI.
	 */

	public void handleMessageFromClientUI(Object message) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;

			sendToServer(message);
			System.out.println("Sent Request To Server");
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * Hook method called each time an exception is thrown by the
	 * client's thread that is waiting for messages from the server.
	 * The method may be overridden by subclasses. (Called when Server is disconnected)
	 *
	 * @param exception the exception raised.
	 */
	protected void connectionException(Exception exception) {
		System.out.println("connectionException, server could be disconnected");
		SubscribersController.moveToConnectToServer();
	}

	/**
	 * This method terminates the client.
	 */
	public void quit() {
		try {
			closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}