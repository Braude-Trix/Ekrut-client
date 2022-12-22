package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.MyOrders;
import models.PickUpMethod;
import utils.Util;


public class MyOrdersController implements Initializable {

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
    
    @FXML
    private Hyperlink returnToEnterAnotherOrderId;

    @FXML
    private Label pickupCode;

    @FXML
    private VBox ShowEnterOrderID;

    @FXML
    private VBox ShowPickupCode;
    
    @FXML
    private TextField txtOrderID;
    
    @FXML
    private Label errorLabel;
    
    private List<MyOrders> listMyOrders;
    
    @FXML
    private AnchorPane anchorPane;
    
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listMyOrders = new ArrayList<>();
		setCellFactoryOfTables();
		getAllOrdersForSpecificUser();
		setStyleForEmptyTable();
		addButtonToTable();
	}
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/MyOrders.fxml"));			
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/myOrders.css").toExternalForm());
		primaryStage.setTitle("My Orders");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());

	}


	private void addButtonToTable() {
        TableColumn<MyOrders, Void> colBtn = new TableColumn<>("Action");

        Callback<TableColumn<MyOrders, Void>, TableCell<MyOrders, Void>> cellFactory = new Callback<TableColumn<MyOrders, Void>, TableCell<MyOrders, Void>>() {
            @Override
            public TableCell<MyOrders, Void> call(final TableColumn<MyOrders, Void> param) {
                final TableCell<MyOrders, Void> cell = new TableCell<MyOrders, Void>() {

                    private final Button btn = new Button("Approve");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                        	//need to change status in db and in table
                        	getTableView().getItems().remove(getIndex());
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);

        tableViewApproveDel.getColumns().add(colBtn);

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
    }
    
    private void getAllOrdersForSpecificUser()
    {
		MyOrders order1 = (new MyOrders("12345","7:00 27/10/22","---", 22, "ss", "Not Collected", PickUpMethod.delivery));
		MyOrders order2 = (new MyOrders("12345","7:00 27/10/22","---", 22, "ss", "approve", PickUpMethod.latePickUp));
		MyOrders order3 = (new MyOrders("54321","7:00 27/10/22","---", 22, "ss", "Not Collected", PickUpMethod.latePickUp));

		listMyOrders.add(order1);
		listMyOrders.add(order2);
		listMyOrders.add(order3);

		ObservableList<MyOrders> orders = FXCollections.observableArrayList();
		orders.addAll(listMyOrders);
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
    
    @FXML
    void GetCode(ActionEvent event) {
    	removeErrorStyle();
    	
    	if (isErrorField()) {
    		return;
    	}
    	
    	ShowEnterOrderID.setVisible(false);
    	ShowPickupCode.setVisible(true);
    	txtOrderID.clear();
    	setPickupCode(txtOrderID.getText());
    }
    
    private void setPickupCode(String OrderID) {
    	pickupCode.setText("11111");
    }
    
    private void removeErrorStyle() {
    	txtOrderID.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
    private boolean isErrorField() {
    	if (Util.isBlankString(txtOrderID.getText())) {
    		errorLabel.setText("Required field");
    		Util.setFieldTextErrorBorder(txtOrderID);
    		return true;
    	}
    	else if (!isExistPickupOrderID(txtOrderID)) {
    		return true;
    	}
    	return false;
    }
    
    private boolean isExistPickupOrderID(TextField textField) {
    	String orderID = textField.getText();
    	for (MyOrders order : listMyOrders) {
    		//status need change 
    		if (orderID.equals(order.getOrderId()) && (order.getPickUpMethod() == PickUpMethod.latePickUp) && order.getStatus().equals("Not Collected")) {
    			return true;
    		}
    	}
		errorLabel.setText("Invalid Order ID");
		textField.getStyleClass().add("validation-error");
    	return false;
    }
    
    @FXML
    void returnToEnterAnotherOrderId(MouseEvent event) {
    	ShowEnterOrderID.setVisible(true);
    	ShowPickupCode.setVisible(false);
    }
    
    @FXML
    void requestFocus(MouseEvent event) {
    	anchorPane.requestFocus();

    }
}
