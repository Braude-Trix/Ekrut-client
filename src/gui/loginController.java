package gui;


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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Method;
import models.Order;
import models.Request;
import models.Subscriber;
import models.User;
import models.UserType;
import utils.Util;

/**
 * @author gal
 * This class describes the functionality of the login page
 */
public class loginController  implements Initializable{
	public static User user = null;
	public static Order order;
//	public static Subscriber subscriber;
	public OLController OLcon;
	public EKController EKcon;
//	public MarketingManagerController MarketingManagerCon;
	
    @FXML
    private Label errorLabel;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUsername;
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private TextField txtTouch;
    
    @FXML
    private Label errorTouch;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		user = null;
		order = null;
//		subscriber = null;
	}

	/**
	 * This method describes setting up a new scene.
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
	 */
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
	

	
    /**
     * This method opens a customer or employee window depending on the type of user. 
     * And only if the details are correct.
     * @param event, Description: Event - clicking the Login button
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void moveHomePage(ActionEvent event) throws Exception {
    	anchorPane.requestFocus();
    	boolean isInputErrorUsername = false;
    	boolean isInputErrorPassword = false;

    	removeErrorStyle();

    	if (Util.isBlankString(txtUsername.getText())){
    		Util.setFieldTextErrorBorder(txtUsername);
    		isInputErrorUsername = true;
    	}
    	if (Util.isBlankString(txtPassword.getText())){
    		Util.setFieldTextErrorBorder(txtPassword);
    		isInputErrorPassword = true;
    	}
    	
    	if (isInputErrorUsername || isInputErrorPassword) {
    		setErrorLabel(isInputErrorUsername, isInputErrorPassword);
    		return;
    	}
    	
    	SelectHomePageToOpen();
    }
       
    /**
     * This method sets an appropriate error message.
     * @param invalidUsername, Description: This parameter is boolean if false does not set an error message associated with it.
     * @param invalidPassword, Description: This parameter is boolean if false does not set an error message associated with it.
     */
    private void setErrorLabel(boolean invalidUsername, boolean invalidPassword) {
    	if (invalidPassword && invalidUsername) {
    		errorLabel.setText("The username and password are incorrect");
    	}
    	else if (invalidPassword) {
    		errorLabel.setText("The password is incorrect");
    	}
    	else if (invalidUsername) {
    		errorLabel.setText("The username is incorrect");
    	}
    }
    
    /**
     * This method removes error formatting for normal outputs.
     */
    private void removeErrorStyle() {
    	txtUsername.getStyleClass().remove("validation-error");
    	txtPassword.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
    /**
     * This method opens a customer or employee window depending on the type of user. 
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    private void SelectHomePageToOpen() throws Exception {
    	//request to user from db
    	requestUser();
    	
    	//user = new User(null, null, 422222222, null, null, null, null, UserType.Client, null);
    	//CheckAndGetSubscriberDetails();
    	if (user == null) {
    		return;
    	}
		Stage stage = StageSingleton.getInstance().getStage();
		OLcon = new OLController();	
		OLcon.start(stage);
//		EKcon = new EKController();
//		EKcon.start(stage);
//		MarketingManagerCon = new MarketingManagerController();
//		MarketingManagerCon.start(stage);
    }
    
    private void requestUser() {
    	List<Object> usernameAndPassword = new ArrayList<>();
    	usernameAndPassword.add(txtUsername.getText());
    	usernameAndPassword.add(txtPassword.getText());

    	Request request = new Request();
        request.setPath("/login/getUser");
        request.setMethod(Method.GET);
        request.setBody(usernameAndPassword);
        ClientUI.chat.accept(request);// sending the request to the server.

        handleRsponseGetUser();
    }
    
    private void handleRsponseGetUser() {
//		handle response info:
        switch (Client.resFromServer.getCode()) {
            case OK:
            	user = (User) Client.resFromServer.getBody().get(0);
                break;
            case INVALID_DATA:
        		errorLabel.setText("The username or password are incorrect");
            	break;
            default:
        		errorLabel.setText(Client.resFromServer.getDescription());
                break;
        }
    }
    
    
//    private void CheckAndGetSubscriberDetails() {
//    	if (user.getType() == UserType.Subscriber) {
//    		subscriber = new Subscriber(11, 0);
//    	}
//    }
    
    /**
     * This method responds to the event of interfacing with EKT (pressing a button).
     * @param event, Description: Event - clicking the Touch button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens.
     */
    @FXML
    void btnTouch(ActionEvent event) throws Exception {
    	txtTouch.getStyleClass().remove("validation-error");
    	if (Util.isBlankString(txtTouch.getText())){
    		Util.setFieldTextErrorBorder(txtTouch);
    		errorTouch.setVisible(true);
    		return;
    	}
		Stage stage = StageSingleton.getInstance().getStage();
		EKcon = new EKController();
		EKcon.start(stage);
    }
    
    /**
     * This method describes an event of clicking exit and exiting the application.
     * @param event, Description: Event - clicking the Exit button.
     */
    @FXML
    void Exit(ActionEvent event) {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }
    
    /**
     * This method requires when you click anywhere else on the screen to get the focus.
     * @param event, Description: The screen is clicked the event is sent
     */
    @FXML
    void requestFocus(MouseEvent event) {
    	anchorPane.requestFocus();

    }

}
