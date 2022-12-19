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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.MyOrders;
import models.PickUpMethod;


public class MyOrdersController implements Initializable {
	public static Scene scene;

	@FXML
    private TableColumn<MyOrders, String> IdOrder;

    @FXML
    private TableColumn<MyOrders, String> Status;

    @FXML
    private TableColumn<MyOrders, String> execDate;

    @FXML
    private TableColumn<MyOrders, String> recieveDate;

    @FXML
    private TableColumn<MyOrders, PickUpMethod> typeOrder;

    @FXML
    private TableView<MyOrders> tableViewOrders;
    
    @FXML
    private TableColumn<MyOrders, String> execDateConfirm;

    @FXML
    private TableColumn<MyOrders, String> idOrderConfirm;
    
    @FXML
    private TableColumn<MyOrders, Button> approveBtn;
    
    @FXML
    private TableView<MyOrders> tableViewApproveDel;
    
    @FXML
    private ImageView backBtn;
    

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/MyOrders.fxml"));			
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/myOrders.css").toExternalForm());
		primaryStage.setTitle("My Orders");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		setCellFactoryOfTables();
		getAllOrdersForSpecificUser();
		setStyleForEmptyTable();
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
    
    private void setCellFactoryOfTables() {
		IdOrder.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("orderId"));
		Status.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("Status"));
		execDate.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("date"));
		recieveDate.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("receivedDate"));
		typeOrder.setCellValueFactory(new PropertyValueFactory<MyOrders, PickUpMethod>("pickUpMethod"));
		
		idOrderConfirm.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("orderId"));
		execDateConfirm.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("date"));
		approveBtn.setCellValueFactory(new PropertyValueFactory<MyOrders, Button>("btnApprove"));
    }
    
    private void getAllOrdersForSpecificUser()
    {
		MyOrders order1 = (new MyOrders("12345","7:00 27/10/22","---", 22, "ss", "not confirmed", PickUpMethod.delivery));
		MyOrders order2 = (new MyOrders("12345","7:00 27/10/22","---", 22, "ss", "approve", PickUpMethod.latePickUp));
		ObservableList<MyOrders> orders = FXCollections.observableArrayList();
		orders.add(order1);
		orders.add(order2);
		tableViewOrders.setItems(orders);
		
		//need to approve
		ObservableList<MyOrders> waitForApprove = FXCollections.observableArrayList();
		waitForApprove.add(order1);
		tableViewApproveDel.setItems(waitForApprove);
    }
    
    private void setStyleForEmptyTable() {
		Label tableViewApproveLabel = new Label ("There are no shipments awaiting approval");
		tableViewApproveLabel.setStyle(
				"-fx-text-fill : #FF3547;-fx-font-weight: bold;  -fx-font-size: 12");
		Label tableViewOrdersLabel = new Label ("No orders yet");
		tableViewOrdersLabel.setStyle(
				"-fx-text-fill : #FF3547;-fx-font-weight: bold; -fx-font-size: 17");
		tableViewOrders.setPlaceholder(tableViewOrdersLabel);
		tableViewApproveDel.setPlaceholder(tableViewApproveLabel);
    }
}
