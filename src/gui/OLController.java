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

public class OLController implements Initializable {
	public static MyOrdersController MyOrderCon = null;
	public static DeliveryFormController DeliveryCon = null;
	public static PickupController PickupCon = null;

	public static Scene scene;

    @FXML
    private Label newNotification;
    
    @FXML
    void MoveMyOrdersWindow(ActionEvent event) throws Exception {		
		Stage stage = StageSingleton.getInstance().getStage();
		if (MyOrderCon == null)
		{
			MyOrderCon = new MyOrdersController();	
			MyOrderCon.start(stage);
		}
		else
		{
			stage.setScene(MyOrdersController.scene);
			stage.show();
		}

    }
    

//	forcedExit method - deals with forced exits - closes the single stage and closes the program.
//	private void forcedExit() {
//		StageSingleton.getInstance().getStage().close();
//		System.exit(0);
//	}

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
    
    @FXML
    void LogOut(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(loginController.scene);
		stage.show();
    }
//    IpRefreshBtn.setText("");
//    Image img = new Image("/assets/reload.png", IpRefreshBtn.getMaxWidth(), IpRefreshBtn.getMaxHeight(),
//            true, true, true);
//    ImageView imgView = new ImageView(img);
//    IpRefreshBtn.setGraphic(imgView);
//    IpRefreshBtn.setTooltip(new Tooltip("Refresh when your internet connection has changed"));


	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setNotificationForApprovalDelivery();
	}
	
	private void setNotificationForApprovalDelivery()
	{
	      Image img = new Image("/assets/ring-bell.png");
	      ImageView view = new ImageView(img);
	      view.setFitHeight(25);
	      view.setPreserveRatio(true);
	      newNotification.setGraphic(view);
	      newNotification.setText("10 shipping confirmation");
	      newNotification.setVisible(true);
	}

}

