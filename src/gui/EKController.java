package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EKController {
	public static Scene scene;

    @FXML
    private VBox VBoxEnterPickUp;

    @FXML
    private VBox VboxSuccessfulPickUpCode;

    @FXML
    private Button submitPickUpBtn;
    
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/EKMain.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Main");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
	
    @FXML
    void LogOut(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(loginController.scene);
		stage.show();
    }
    
    @FXML
    void SubmitPickupCode(ActionEvent event) {
    	VBoxEnterPickUp.setVisible(false);
    	VboxSuccessfulPickUpCode.setVisible(true);
    }
    

    @FXML
    void EnterAnotherPickupCode(ActionEvent event) {
    	VBoxEnterPickUp.setVisible(true);
    	VboxSuccessfulPickUpCode.setVisible(false);
    }
}
