package gui;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginController {
	public OLController OLcon;
	public EKController EKcon;
	
    @FXML
    private Label errorLabel;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUsername;
    
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/login.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
		primaryStage.setTitle("Login");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
	
    @FXML
    void moveHomePage(ActionEvent event) throws Exception {
    	removeErrorStyle();
    	boolean usernameIsBlank = isBlankTextField(txtUsername);
    	boolean passwordIsBlank = isBlankTextField(txtPassword);
    	
    	if (usernameIsBlank || passwordIsBlank) {
    		setErrorLabel(usernameIsBlank, passwordIsBlank);
    		return;
    	}
    	
    	SelectHomePageToOpen();
    }
    
    private boolean isBlankTextField(TextField textField) {

    	if (textField.getText().isBlank()) {
    		textField.getStyleClass().add("validation-error");
    		return true;
    	}
    	return false;    	
    }
    
    private void setErrorLabel(boolean inValidUsername, boolean invalidPassword) {
    	if (invalidPassword && inValidUsername) {
    		errorLabel.setText("The username and password are incorrect");
    	}
    	else if (invalidPassword) {
    		errorLabel.setText("The password is incorrect");
    	}
    	else if (inValidUsername) {
    		errorLabel.setText("The username is incorrect");
    	}
    }
    
    private void removeErrorStyle() {
    	txtUsername.getStyleClass().remove("validation-error");
    	txtPassword.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
    private void SelectHomePageToOpen() throws Exception {
		Stage stage = StageSingleton.getInstance().getStage();
		OLcon = new OLController();	
		OLcon.start(stage);
//		EKcon = new EKController();
//		EKcon.start(stage);
    }
    
    @FXML
    void Exit(ActionEvent event) {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }
    
    
    

}
