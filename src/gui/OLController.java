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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javafx.stage.Stage;
import models.Method;
import models.Request;
import utils.Util;

/**
 * @author gal
 * This class describes the client home page in OL configuration
 */
public class OLController implements Initializable {
	public MyOrdersController MyOrderCon = null;
	public DeliveryFormController DeliveryCon = null;
	public PickupController PickupCon = null;

	public static Scene scene;

    @FXML
    private Label newNotification;

    @FXML
    private Label errorLabel;
    
	/**
	 *This method describes the initialization of information that will be displayed in the window depending on the client.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		checkDeliveryNotCollected();
	}
	
	/**
	 * This method describes setting up a new scene.
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/OLMain.fxml"));
				
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
	
    /**
     * This method describes what happens after clicking the MyOrders button.
     * The MyOrders window opens.
     * @param event, Description: Event - clicking the MyOrders button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void MoveMyOrdersWindow(ActionEvent event) throws Exception {		
		Stage stage = StageSingleton.getInstance().getStage();
		MyOrderCon = new MyOrdersController();	
		MyOrderCon.start(stage);
    }
    
//	forcedExit method - deals with forced exits - closes the single stage and closes the program.
//	private void forcedExit() {
//		StageSingleton.getInstance().getStage().close();
//		System.exit(0);
//	}


   
    /**
     * This method describes what happens after clicking the Delivery button.
     * @param event, Description: Event - clicking the MyOrders button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void MoveDeliveryForm(ActionEvent event) throws Exception {
		Stage stage = StageSingleton.getInstance().getStage();

		if (DeliveryFormController.scene == null)
		{
			DeliveryCon = new DeliveryFormController();	
			DeliveryCon.start(stage);
		}
		else
		{
			stage.setScene(DeliveryFormController.scene);
			stage.show();
		}

    }

    /**
     * This method describes what happens after clicking the Pickup button.
     * @param event, Description: Event - clicking the MyOrders button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void MovePickupForm(ActionEvent event) throws Exception {
		Stage stage = StageSingleton.getInstance().getStage();
		if (PickupController.scene == null) {
			PickupCon = new PickupController();	
			PickupCon.start(stage);
		}
		else
		{
			stage.setScene(PickupController.scene);
			stage.show();
		}

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
	 * Defines the amount of notifications waiting for the customer if he has shipments that he has not confirmed the arrival of.
	 */
	private void setNotificationForApprovalDelivery(int amount)
	{
		errorLabel.setText("");
		if (amount == 0) {
		      newNotification.setVisible(false);
		      return;
		}
		newNotification.setText(amount+" shipping confirmation");
		newNotification.setVisible(true);

	}
	
	void checkDeliveryNotCollected() {
    	List<Object> userDetails = new ArrayList<>();
    	userDetails.add(LoginController.user.getId());
    	Request request = new Request();
        request.setPath("/user/myOrders/deliveryNotCollected");
        request.setMethod(Method.GET);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);// sending the request to the server.
        
    	switch (Client.resFromServer.getCode()) {
        case OK:
        	setNotificationForApprovalDelivery((Integer)Client.resFromServer.getBody().get(0));
            break;
        default:
    		errorLabel.setText(Client.resFromServer.getDescription());
            break;
    	}
    	
	}

}

