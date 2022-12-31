package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Method;
import models.Order;
import models.OrderStatus;
import models.PickUpMethod;
import models.Request;
import utils.Util;

/**
 * @author gal
 * This class describes the client home page in EK configuration
 */
public class EKController implements Initializable {
	public static Scene scene;
	
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
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private Label labelName;
    
    @Override
	public void initialize(URL location, ResourceBundle resources) {
        Util.setNameNavigationBar(labelName);
	}
	/**
	 * This method describes setting up a new scene.
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
	 */
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
        primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
	
    /**
     * This method describes what happens after clicking the logout button.
     * Clicking this button will lead to the login screen.
     * @param event, Description: Event - clicking the Logout button
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());
    }
    
    /**
     * This method describes clicking the submit button.
     *  Clicking this button, if the pickup code entered is correct, 
     *  will transfer the order to ek-op which it will have to bring to the customer.
     * @param event, Description: Event - clicking the Submit button
     */
    @FXML
    void SubmitPickupCode(ActionEvent event) {
    	removeErrorStyle();
    	if (Util.isBlankString(txtPickupCode.getText())) {
    		errorLabel.setText("Entered code is incorrect, please try again");
    		Util.setFieldTextErrorBorder(txtPickupCode);
    		return;
    	}
    	
    	requestOrderByCode();
    }
    
    private void requestOrderByCode() {
		List<Object> details = new ArrayList<>();
		details.add(LoginController.user.getId());
		details.add(txtPickupCode.getText());
		details.add(UserInstallationController.machine.getId());
		Request request = new Request();
		request.setPath("/order/checkExistPickupOrderAndChangeStatus");
		request.setMethod(Method.PUT);
		request.setBody(details);
		ClientUI.chat.accept(request);// sending the request to the server.
		switch (Client.resFromServer.getCode()) {
		case OK:
			replaceHboxSuccessEnterPickupCode();
			break;
		default:
			errorLabel.setText((Client.resFromServer.getDescription()));
			Util.setFieldTextErrorBorder(txtPickupCode);
			break;
		}
    }
    
    private void replaceHboxSuccessEnterPickupCode() {
    	VBoxEnterPickUp.setVisible(false);
    	VboxSuccessfulPickUpCode.setVisible(true);
    	txtPickupCode.setText("");
    }
    
    @FXML
    void createNewOrder(ActionEvent event) {
    	LoginController.order = new Order(null, null, 0, UserInstallationController.machine.getId(), OrderStatus.Collected,
    			PickUpMethod.selfPickUp, LoginController.user.getId());
    }
    
    /**
     * This method describes the possibility of entering an additional pickup code if the customer has another code.
     * @param event, Description: Event - clicking the hyper link "Would you like to enter another code? Click here"
     */
    @FXML
    void EnterAnotherPickupCode(ActionEvent event) {
    	VBoxEnterPickUp.setVisible(true);
    	VboxSuccessfulPickUpCode.setVisible(false);
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
     * This method removes error formatting for normal outputs.
     */
    private void removeErrorStyle() {
    	txtPickupCode.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
}
