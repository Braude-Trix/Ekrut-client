// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import models.Request;
<<<<<<< HEAD
import models.Response;
import models.Subscriber;

import java.io.*;
import java.util.List;

/**
 * This class overrides some of the methods defined in the abstract superclass
 * in order to give more functionality to the client.
=======

import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
<<<<<<< HEAD
public class ChatClient extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */
	ChatIF clientUI;
	public static boolean awaitResponse = false;

	// Constructors ****************************************************

	/**
	 * Constructs an instance of the chat client.
	 *
	 * @param host     The server to connect to.
	 * @param port     The port number to connect on.
	 * @param clientUI The interface type variable.
	 */

	public ChatClient(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		openConnection();
	}

	// Instance methods ************************************************

	/**
	 * This method handles all data that comes in from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		Response response = (Response) msg;
//		System.out.println(response.getCode());
//		System.out.println(response.getDescription());
		List<Object> lst = response.getBody();
//		System.out.println(lst.size());
//		System.out.println(((Subscriber) (lst.get(0))).getId());
//		System.out.println(((Subscriber) (lst.get(1))).getId());

		awaitResponse = false;
		clientUI.display(msg.toString());
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
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
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
=======
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  public static boolean awaitResponse = false;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
//  public void handleMessageFromClientUI(Object message)  
//  {
//    	try {
//			openConnection();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}//in order to send more than one message
//       	awaitResponse = true;
//       	
//       
//    	try {
//			sendToServer(message);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//    
//  }
  
  public void handleMessageFromClientUI(Object message)  
  {
    try
    {
    	openConnection();//in order to send more than one message
       	awaitResponse = true;
//    	sendToServer("bye");
    	sendToServer(message);
    	System.out.println("dididdidi");
		// wait for response
//		while (awaitResponse) {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
    }
    catch(IOException e)
    {
      e.printStackTrace();
      clientUI.display("Could not send message to server: Terminating client."+ e);
      quit();
    }
  }

  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
}
//End of ChatClient class
