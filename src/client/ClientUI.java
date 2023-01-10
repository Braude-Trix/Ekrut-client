package client;

import gui.ConnectToServerController;
import gui.StageSingleton;
import javafx.application.Application;
import javafx.stage.Stage;


public class ClientUI extends Application {
	public static client.ClientController chat; //only one instance
	public static void main(String[] args) throws Exception {
		    launch(args);  
	} // end main
//	start method, starts the whole application.
	@Override
	public void start(Stage primaryStage) throws Exception {
		StageSingleton.getInstance().setStage(primaryStage);
		ConnectToServerController.start(primaryStage);
	}
}



