package client;
import javafx.application.Application;
import gui.StageSingleton;
import javafx.stage.Stage;
import gui.ConnectToServerController;


public class ClientUI extends Application {
	public static client.ClientController chat; //only one instance
	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main 
	 
	
//	start method, starts the whole application.
	@Override
	public void start(Stage primaryStage) throws Exception {

		ConnectToServerController connectToServer = new ConnectToServerController();
		StageSingleton.getInstance().setStage(primaryStage);
		connectToServer.start(primaryStage);

		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
	
	
}



