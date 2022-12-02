package client;
import javafx.application.Application;
import gui.StageSingleton;
import javafx.stage.Stage;
import gui.ConnectToServerController;
<<<<<<< HEAD
=======
import gui.MainClientPanelFormController;
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591


public class ClientUI extends Application {
	public static ClientController chat; //only one instance
	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   } // end main 
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
<<<<<<< HEAD

		ConnectToServerController connectToServer = new ConnectToServerController();
		StageSingleton.getInstance().setStage(primaryStage);
		connectToServer.start(primaryStage);

=======
//		ConnectToServerController connectToServer = new ConnectToServerController();
//		
//		connectToServer.start(primaryStage);
//		
		ClientConfiguration clientConfig = new ClientConfiguration("localhost",5555);
		chat = new ClientController( clientConfig.getLocalHost(), clientConfig.getPort());
		
		MainClientPanelFormController mainClientController = new MainClientPanelFormController();
		StageSingleton.getInstance().setStage(primaryStage);
		mainClientController.start(primaryStage); 
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
		
	}
	
	
}
<<<<<<< HEAD



=======
>>>>>>> 0299c993c15b98b833fafa02e661d3ce144bf591
