package gui;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import utils.Util;
import models.DeliveryOrder;
import models.PickUpMethod;
import models.Regions;
import models.User;

public class DeliveryFormController implements Initializable { 
	public static Scene scene;
    private static final int NUMBER_DIGIT_IN_PHONE_NUMBER = 10;

    @FXML
    private ComboBox<Regions> regionList;

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
    private Label errorLabelRegion;

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
    
    @FXML
    private AnchorPane anchorPane;
	
    private User user;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        setRegionComboBox();
        user = loginController.user;
        setValuesInTextFields();
	}
    
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
	
	private void setValuesInTextFields() {
		txtFirstName.setText(user.getFirstName());
		txtLastName.setText(user.getLastName());
		txtPhoneNumber.setText(user.getPhoneNumber());
	}
	
    @FXML
    void ContinueToOrder(ActionEvent event) {
    	anchorPane.requestFocus();
    	removeErrorStyle();
    	boolean isValidFirstName = !isBlankTextField(txtFirstName, errorLabelFirstName);
    	boolean isValidLastName = !isBlankTextField(txtLastName, errorLabelLastName);
    	boolean isValidCity = isValidFillComboBoxes();
    	boolean isValidFullAddress = !isBlankTextField(txtFullAddress, errorLabelFullAddress);
    	boolean isValidPhoneNumber = isValidatePhone(txtPhoneNumber, errorLabelPhone);
    	boolean isValidPinCode = isValidatePincode(txtPinCode, errorLabelPinCode);
    	
    	if (!isValidCity || !isValidFirstName || !isValidLastName || !isValidPhoneNumber || !isValidFullAddress || !isValidPinCode) {
    		return;
    	}

    	loginController.order = new DeliveryOrder(null, null, 0, null, null,  PickUpMethod.delivery, loginController.user.getId(), txtFirstName.getText(),
    			txtLastName.getText(), txtPhoneNumber.getText(), txtFullAddress.getText(), regionList.getValue(), null, txtPinCode.getText());
    }
	
    @FXML
    void requestFocus(MouseEvent event) {
    	anchorPane.requestFocus();

    }
	
	
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());
    }
    
    @FXML
    void Back(MouseEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(OLController.scene);
		stage.show();
    }
    
    private boolean isBlankTextField(TextField textField, Label errorLabel) {
    	if (Util.isBlankString(textField.getText())) {
    		errorLabel.setText("Required field");
    		Util.setFieldTextErrorBorder(textField);
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
    	if (isBlankTextField(textField, errorLabel)) {
    		return false;
    	}
        if (textField.getText().length() != NUMBER_DIGIT_IN_PHONE_NUMBER) {
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("10 digits are required");
            return false;
        }
        
        if (!isOnlyDigits(textField.getText())){
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("Digits only required");
        	return false;
        }
        
        if (textField.getText().charAt(0) != '0' || textField.getText().charAt(1) != '5') {
    		textField.getStyleClass().add("validation-error");
    		errorLabel.setText("Phone starts with 05");
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
    	regionList.getStyleClass().remove("validation-error");
    	txtFirstName.getStyleClass().remove("validation-error");
    	txtFullAddress.getStyleClass().remove("validation-error");
    	txtLastName.getStyleClass().remove("validation-error");
    	txtPhoneNumber.getStyleClass().remove("validation-error");
    	txtPinCode.getStyleClass().remove("validation-error");
    	errorLabelRegion.setText("");
    	errorLabelFirstName.setText("");
    	errorLabelFullAddress.setText("");
    	errorLabelLastName.setText("");
    	errorLabelPhone.setText("");
    	errorLabelPinCode.setText("");
    }
    
    private void setRegionComboBox() {
        ObservableList<Regions> options = FXCollections.observableArrayList(Regions.class.getEnumConstants());
        options.remove(Regions.All);
        regionList.getItems().addAll(options); 
    }
    
    
    private boolean isValidFillComboBoxes() {
    	if(regionList.getValue() == null) {
    		regionList.getStyleClass().add("validation-error");
    		errorLabelRegion.setText("Required field");
    		return false;
    	}
    	return true;
    }
    
}
