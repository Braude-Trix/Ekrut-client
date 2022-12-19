package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DeliveryFormController {
	public static Scene scene;
    private static final int NUMBER_DIGIT_IN_PHONE_NUMBER = 10;

    @FXML
    private TextField txtCity;

    @FXML
    private TextField txtFirstName;

    @FXML
    private TextField txtFullAddress;

    @FXML
    private TextField txtLastName;

    @FXML
    private TextField txtPhoneNumber;

    @FXML
    private TextField txtPinCode;
    
    @FXML
    private Label errorLabelCity;

    @FXML
    private Label errorLabelFirstName;

    @FXML
    private Label errorLabelFullAddress;

    @FXML
    private Label errorLabelLastName;

    @FXML
    private Label errorLabelPhone;

    @FXML
    private Label errorLabelPinCode;
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/DeliveryForm.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/deliveryForm.css").toExternalForm());
		primaryStage.setTitle("Delivery Form");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
	
    @FXML
    void ContinueToOrder(ActionEvent event) {
    	removeErrorStyle();
    	boolean isValidFirstName = isBlankTextField(txtFirstName, errorLabelFirstName);
    	boolean isValidLastName = isBlankTextField(txtLastName, errorLabelLastName);
    	boolean isValidCity = isBlankTextField(txtCity, errorLabelCity);
    	boolean isValidFullAddress = isBlankTextField(txtFullAddress, errorLabelFullAddress);
    	boolean isValidPhoneNumber = isValidatePhone(txtPhoneNumber, errorLabelPhone);
    	boolean isValidPinCode = isValidatePincode(txtPinCode, errorLabelPinCode);
    	
    	if (!isValidCity || !isValidFirstName || !isValidLastName || !isValidPhoneNumber || !isValidFullAddress || !isValidPinCode) {
    		return;
    	}
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
    void Back(MouseEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(OLController.scene);
		stage.show();
    }
    
    private boolean isBlankTextField(TextField textField, Label errorLabel) {
    	if (textField.getText().isBlank()) {
    		errorLabel.setText("Required field");
    		textField.getStyleClass().add("validation-error");
    		return true;
    	}
    	return false;    	
    }
    
    //	isOnlyDigits method, checks if a given string contains only numbers. if so - returns true, else return false.
    private boolean isOnlyDigits(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isValidatePhone(TextField textField, Label errorLabel) {
        if (textField.getText().length() != NUMBER_DIGIT_IN_PHONE_NUMBER) {
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("A phone number\ncontains 10 digits");
            return false;
        }
        
        if (!isOnlyDigits(textField.getText())){
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("A phone number\ncontains only digits");
        	return false;
        }
        
        if (textField.getText().charAt(0) != '0' || textField.getText().charAt(1) != '5') {
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("Phone number should\nstart with 05");
        	return false;
        }
        return true;
    }
    
    private boolean isValidatePincode(TextField textField, Label errorLabel) {
    	
    	if (isBlankTextField(textField, errorLabel)) {
    		return false;
    	}
        if (!isOnlyDigits(textField.getText())){
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("A pincode contains only digits");
        	return false;
        }
       
        return true;
    }
    
    
    private void removeErrorStyle() {
    	txtCity.getStyleClass().remove("validation-error");
    	txtFirstName.getStyleClass().remove("validation-error");
    	txtFullAddress.getStyleClass().remove("validation-error");
    	txtLastName.getStyleClass().remove("validation-error");
    	txtPhoneNumber.getStyleClass().remove("validation-error");
    	txtPinCode.getStyleClass().remove("validation-error");
    	errorLabelCity.setText("");
    	errorLabelFirstName.setText("");
    	errorLabelFullAddress.setText("");
    	errorLabelLastName.setText("");
    	errorLabelPhone.setText("");
    	errorLabelPinCode.setText("");
    }
    
}
