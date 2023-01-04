package gui;

import java.io.IOException;
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
import models.OrderStatus;
import models.PickUpMethod;
import models.Regions;
import models.User;

/**
 * @author gal
 * This class describes the functionality of the shipping details entry page,
 * including validation and the option to go back, continue an order or disconnect
 */
public class DeliveryFormController implements Initializable { 
	/**
	 * This field saves the scene of the window that opens for open back this window from others windows
	 */
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
	
    @FXML
    private Label labelName;
        
    private User user;
    
	/**
	 *This method describes the initialization of information that will be displayed in the window depending on the client.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        setRegionComboBox();
        user = LoginController.user;
        setValuesInTextFields();
        Util.setNameNavigationBar(labelName);
	}
    
	/**
	 * This method describes setting up a new scene.
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
	 */
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
        primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	private void setValuesInTextFields() {
		txtFirstName.setText(user.getFirstName());
		txtLastName.setText(user.getLastName());
		txtPhoneNumber.setText(user.getPhoneNumber());
	}
	
    /**
     * This method includes validation of the data entered by the customer 
     * if everything is correct beyond the order continuation window (item selection)
     * @param event, Description: Clicking the "continue" button
     */
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

    	LoginController.order = new DeliveryOrder(null, null, 0, "1", OrderStatus.WaitingApproveDelivery,  PickUpMethod.delivery, LoginController.user.getId(), txtFirstName.getText(),
    			txtLastName.getText(), txtPhoneNumber.getText(), txtFullAddress.getText(), regionList.getValue(), null, txtPinCode.getText());
		try {
			new NewOrderController().start(StageSingleton.getInstance().getStage());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
    /**
     * This method requires when you click anywhere else on the screen to get the focus.
     * @param event, Description: The screen is clicked the event is sent
     */
    @FXML
    void requestFocus(MouseEvent event) {
    	anchorPane.requestFocus();
    }
	
	
	/**
	 * This method navigates the client to the login page and logging him out. This
	 * method runs when the user clicked LogOut.
	 * 
	 * @param event, Description: the current event when the click happened.
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());
    }
    

    /**
     * This method describes clicking the back button, returning to the main screen of the OL configuration
     * @param event, Description: Clicking the "Back" button
     */
    @FXML
    void Back(MouseEvent event){
		Stage primaryStage = StageSingleton.getInstance().getStage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/OLMain.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
			primaryStage.setTitle("EKrut Main");
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.setResizable(false);
			primaryStage.show();
			primaryStage.setMinHeight(primaryStage.getHeight());
			primaryStage.setMinWidth(primaryStage.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    private boolean isBlankTextField(TextField textField, Label errorLabel) {
    	if (Util.isBlankString(textField.getText())) {
    		errorLabel.setText("Required field");
    		Util.setFieldTextErrorBorder(textField);
    		return true;
    	}
    	return false;    	
    }
    
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
