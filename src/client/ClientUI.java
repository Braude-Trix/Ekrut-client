package client;
import javafx.application.Application;
import gui.StageSingleton;
import javafx.stage.Stage;
import gui.ConnectToServerController;


public class ClientUI extends Application {
	public static ClientController chat; //only one instance
	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main 
	 
	@Override
	public void start(Stage primaryStage) throws Exception {

		ConnectToServerController connectToServer = new ConnectToServerController();
		StageSingleton.getInstance().setStage(primaryStage);
		connectToServer.start(primaryStage);

		
	}
	
	
}



