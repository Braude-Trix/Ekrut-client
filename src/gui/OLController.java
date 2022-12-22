package gui;



import java.net.URL;
import java.util.ResourceBundle;

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
    
    
	/**
	 *This method describes the initialization of information that will be displayed in the window depending on the client.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setNotificationForApprovalDelivery();
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
		if (DeliveryCon == null)
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
		if (PickupCon == null)
		{
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

	
	/**
	 * Defines the amount of notifications waiting for the customer if he has shipments that he has not confirmed the arrival of.
	 */
	private void setNotificationForApprovalDelivery()
	{
	      Image img = new Image("/assets/ring-bell.png");
	      ImageView view = new ImageView(img);
	      view.setFitHeight(25);
	      view.setPreserveRatio(true);
	      newNotification.setGraphic(view);
	      newNotification.setText("2 shipping confirmation");
	      newNotification.setVisible(true);
	}

}

