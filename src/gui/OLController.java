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
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Method;
import models.Request;
import utils.Util;

/**
 * @author gal
 * This class describes the client home page in OL configuration and the functionality enabled on this page
 * Functionality - go to the order start page (delivery or pickup), go to the window of my orders
 */
public class OLController implements Initializable {
	private DeliveryFormController DeliveryCon = null;
	private PickupController PickupCon = null;
	private Stage stage = StageSingleton.getInstance().getStage();
	/**
	 * This field saves the scene of the window that opens for open back this window from others windows
	 */
	public static Scene scene;

    @FXML
    private Label newNotification;

    @FXML
    private Label errorLabel;
    
    @FXML
    private Label labelName;
    
    @FXML
    private ImageView backImage;
    
	/**
	 *This method describes the initialization of information that will be displayed in the window depending on the client.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		checkDeliveryNotCollected();
        Util.setNameNavigationBar(labelName);
        if (LoginController.customerAndWorker != null) {
        	backImage.setVisible(true);
        	backImage.setDisable(false);
        }
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
        primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	
    /**
     * This method describes what happens after clicking the MyOrders button.
     * The MyOrders window opens.
     * @param event, Description: Event - clicking the MyOrders button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void MoveMyOrdersWindow(ActionEvent event) throws Exception {		
		MyOrdersController MyOrderCon = new MyOrdersController();	
		MyOrderCon.start(stage);
    }
   
    /**
     * This method describes what happens after clicking the Delivery button.
     * In the event that there is a scene of a shipping details filling page, 
     * it will return the scene and if not, it will open a new scene
     * @param event, Description: Event - clicking the Delivery button.
     */
    @FXML
    void MoveDeliveryForm(ActionEvent event){
		if (DeliveryFormController.scene == null)
		{
			DeliveryCon = new DeliveryFormController();	
			try {
				DeliveryCon.start(stage);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else
		{
			stage.setScene(DeliveryFormController.scene);
			stage.show();
		}

    }

    /**
     * This method describes what happens after clicking the Pickup button.
     * In the event that there is a scene of a pickup information filling page, 
     * it will return the scene and if not, it will open a new scene
     * @param event, Description: Event - clicking the Pickup button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void MovePickupForm(ActionEvent event) throws Exception {
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
	
	/**
	 * This method shows the amount of notifications a customer has if he has not confirmed the deliveries he made
	 */
	void checkDeliveryNotCollected() {
    	List<Object> userDetails = new ArrayList<>();
    	userDetails.add(LoginController.user.getId());
    	Request request = new Request();
        request.setPath("/user/myOrders/deliveryNotCollected");
        request.setMethod(Method.GET);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);
        
    	switch (Client.resFromServer.getCode()) {
        case OK:
        	setNotificationForApprovalDelivery((Integer)Client.resFromServer.getBody().get(0));
            break;
        default:
    		errorLabel.setText(Client.resFromServer.getDescription());
            break;
    	}
    	
	}
	
    /**
     * In the OL configuration on the main screen as a default the back button does not appear,
     * If the customer enters and is also an employee, he will have an option to go to a window of choosing between an employee and a customer
     * @param event, Description: Pressing the back button
     */
    @FXML
    void Back(MouseEvent event) {
		stage.setScene(SelectOptionWorkerOrCustomer.scene);
		stage.show();
    }

}

