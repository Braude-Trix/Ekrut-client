package gui;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class MainClientPanelFormController {

    @FXML
    private Button viewSubscribersBtn;
    
    @FXML
    private Button ExitBtn;
    
	public void start(Stage primaryStage) throws Exception {	
		Parent root = FXMLLoader.load(getClass().getResource("/gui/MainClientPanel.fxml"));
	
		Scene scene = new Scene(root);
		
		//scene.getStylesheets().add(getClass().getResource("/gui/AcademicFrame.css").toExternalForm());
		primaryStage.setTitle("Client Tool");
		primaryStage.setScene(scene);
		primaryStage.show();	
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
	   
    
    
    @FXML
    void ViewingSubscribers(ActionEvent event) throws Exception {
    	Stage stage = StageSingleton.getInstance().getStage();
        Parent root = FXMLLoader.load(getClass().getResource("/gui/ViewSubscribers.fxml"));
        stage.setTitle("Subscribers Panel");
        stage.setScene(new Scene(root)); 
        stage.show();
    }
    

    @FXML
    void ExitClientGUI(ActionEvent event) {
//    	javafx.application.Platform.exit();
    	Stage stage = (Stage) ExitBtn.getScene().getWindow();
    	stage.close();
    	System.exit(0);
    }
	
}
