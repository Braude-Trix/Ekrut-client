package gui;

import client.ClientController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectToServerController {
	public static ClientController chat; //only one instance
	private Stage primaryStage = null;
    @FXML
    private TextField txtHost;

    @FXML
    private TextField txtPort;
    
    @FXML
    private Button connectBtn;
    
    private void loadDefaultConnect() {
    	txtHost.setText("localhost");
    	txtPort.setText("5555"); 
    }

	public void start(Stage primaryStage)  throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ConnectToServer.fxml"));
		//loadDefaultConnect();
		Scene scene = new Scene(root);
		//scene.getStylesheets().add(getClass().getResource("/gui/AcademicFrame.css").toExternalForm());
		primaryStage.setTitle("Connect To Server Tool");
		primaryStage.setScene(scene);
		primaryStage.show();	
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());	
		this.primaryStage = primaryStage;
		
	}
	
    @FXML
    void connectToServer(ActionEvent event) throws Exception {
//		chat = new ClientController(txtHost.getText(), Integer.parseInt(txtPort.getText()));
//		
//		((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
//
//		MainClientPanelFormController mainClientController = new MainClientPanelFormController();
//		
//		mainClientController.start(primaryStage);

    }
	
	

}