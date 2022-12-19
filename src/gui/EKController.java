package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EKController {
	public Scene scene;
	
    @FXML
    private Label errorLabel;
    
    @FXML
    private TextField txtPickupCode;
    
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
    void LogOut(ActionEvent event) throws Exception {
		Stage stage = StageSingleton.getInstance().getStage();
		Parent root = FXMLLoader.load(getClass().getResource("/assets/login.fxml"));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
		stage.setMinHeight(stage.getHeight());
		stage.setMinWidth(stage.getWidth());
    }
    
    @FXML
    void SubmitPickupCode(ActionEvent event) {
    	removeErrorStyle();
    	boolean isBlankCode = isBlankTextField(txtPickupCode);
    	if (isBlankCode) {
    		return;
    	}
    	VBoxEnterPickUp.setVisible(false);
    	VboxSuccessfulPickUpCode.setVisible(true);
    }
    

    @FXML
    void EnterAnotherPickupCode(ActionEvent event) {
    	txtPickupCode.setText("");
    	VBoxEnterPickUp.setVisible(true);
    	VboxSuccessfulPickUpCode.setVisible(false);
    }
    
    private void removeErrorStyle() {
    	txtPickupCode.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
    private boolean isBlankTextField(TextField textField) {
    	if (textField.getText().isBlank()) {
    		errorLabel.setText("Invalid Code");
    		textField.getStyleClass().add("validation-error");
    		return true;
    	}
    	return false;    	
    }
}
