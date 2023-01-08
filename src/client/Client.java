package client;

import gui.BillWindowController;
import gui.ConnectToServerController;
import gui.LoginController;
import gui.StageSingleton;
import gui.workers.CeoSelectManagers;
import gui.workers.CeoSelectWorker;
import gui.workers.SelectReportGui;
import javafx.application.Platform;
import models.Request;
import models.Response;
import ocsf.client.AbstractClient;

import java.io.IOException;
import java.util.List;

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
	public static Response MsgResFromServer;


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
		Response response = (Response) msg;
		List<Object> listObject = response.getBody();
		if(listObject != null && listObject.get(0) instanceof String && listObject.get(0).toString().equals("Msg")) {
			MsgResFromServer = response;
			System.out.printf("[Messages] Got status %s as response from Server%n", response.getCode());
		}
		else {
			resFromServer = response;
			System.out.printf("Got status %s as response from Server%n", response.getCode());
		}
		awaitResponse = false;
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
			Request request = (Request) message;
			String req_type = String.format("Sent %s Request of %s To Server", request.getMethod(), request.getPath());
			System.out.println(req_type);
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
		Platform.runLater(() -> {
            try {
				if (LoginController.threadListeningNewMsg != null)
					LoginController.threadListeningNewMsg.interrupt();
				// closing all opened popups
				if (CeoSelectManagers.popupDialog != null)
					CeoSelectManagers.popupDialog.close();
				if (CeoSelectWorker.popupDialog != null)
					CeoSelectWorker.popupDialog.close();
				if (SelectReportGui.popupDialog != null)
					SelectReportGui.popupDialog.close();
				if (BillWindowController.popupDialog != null)
					BillWindowController.popupDialog.close();
				// opening ConnectToServer window panel
				ConnectToServerController.start(StageSingleton.getInstance().getStage());
            } catch (IOException e) {
			throw new RuntimeException(e);
            }
        });
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
