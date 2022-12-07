package client;

import java.io.IOException;



/**
 * This class constructs the UI for a chat client.  It implements the
 * chat interface in order to activate the display() method.
 * Warning: Some of the code here is cloned in ServerConsole
 */
public class ClientController {
  //Instance variables **********************************************
  
  /**
   * The instance of the client that created this ConsoleChat.
   */
  Client client;

  //Constructors ****************************************************

  /**
   * Constructs an instance of the ClientConsole UI.
   *
   * @param host The host to connect to.
   * @param port The port to connect on.
   */
  public ClientController(String host, int port) throws IOException {
    try 
    {
      client = new Client(host, port);
    } 
    catch(IOException exception) 
    {
      System.out.println("Error: Can't setup connection!"+ " Terminating client.");
      throw new IOException();
//      System.exit(1);
    }
  }

  
  //Instance methods ************************************************
  
  /**
   * This method waits for input from the console.  Once it is 
   * received, it sends it to the client's message handler.
   */
  public void accept(Object request) {
	  client.handleMessageFromClientUI(request);
  }
}
