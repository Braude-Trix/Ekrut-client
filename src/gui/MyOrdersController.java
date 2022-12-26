package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
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
import models.Method;
import models.MyOrders;
import models.PickUpMethod;
import models.Request;
import models.User;
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
		Util.genricLogOut(getClass());
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
    	//request orders
    	//request date recived from pickup and delivery
    	List<Object> userId = new ArrayList<>();
    	userId.add(loginController.user.getIdNumber());
    	Request request = new Request();
        request.setPath("/user/myOrders");
        request.setMethod(Method.GET);
        request.setBody(userId);
        ClientUI.chat.accept(request);// sending the request to the server.
    	
        handleRsponseGetMyOrders();
    	 
//		MyOrders order1 = (new MyOrders("12345","7:00 27/10/22","---", 22, "ss", "Not Collected", PickUpMethod.delivery, 222));
//		MyOrders order2 = (new MyOrders("12345","7:00 27/10/22","---", 22, "ss", "approve", PickUpMethod.latePickUp,333));
//		MyOrders order3 = (new MyOrders("54321","7:00 27/10/22","---", 22, "ss", "Not Collected", PickUpMethod.latePickUp,777));
//
//		listMyOrders.add(order1);
//		listMyOrders.add(order2);
//		listMyOrders.add(order3);
//
		ObservableList<MyOrders> orders = FXCollections.observableArrayList();
		orders.addAll(listMyOrders);
		tableViewOrders.setItems(orders);
//		
//		//need to approve
//		ObservableList<MyOrders> waitForApprove = FXCollections.observableArrayList();
//		waitForApprove.add(order1);
//		tableViewApproveDel.setItems(waitForApprove);
    }
    
    private void handleRsponseGetMyOrders() {
    	if (Client.resFromServer.getPath().equals("/user/myOrders")) {
        	switch (Client.resFromServer.getCode()) {
            case OK:
            	updateMyOrders(Client.resFromServer.getBody());
                break;
            default:
                break;
        	}
    	}
    }
    
    private void updateMyOrders(List<Object> listOrders){
    	listMyOrders.clear();
    	for (Object order:listOrders) {
    		if (order instanceof MyOrders) {
    			MyOrders tempOrder = (MyOrders)order;
    			if (tempOrder.getPickUpMethod() == PickUpMethod.delivery || tempOrder.getPickUpMethod() == PickUpMethod.latePickUp) {
    				String tempReceivedDate = getRecivedDate(tempOrder.getOrderId(), tempOrder.getPickUpMethod());
    				if (tempReceivedDate == null)
    				{
    					tempReceivedDate = "-----";
    				}
    				tempOrder.setReceivedDate(tempReceivedDate);
    			}
    			listMyOrders.add((MyOrders)order);
    		}
    	}
    }
    
    private String getRecivedDate(String orderID, PickUpMethod method) {
       	List<Object> orderDelivery = new ArrayList<>();
       	orderDelivery.add(orderID);
    	Request request = new Request();
    	if (method == PickUpMethod.delivery) {
            request.setPath("/order/RecivedDateDelivery");
    	}
    	else {
            request.setPath("/order/RecivedDatePickup");
    	}
        request.setMethod(Method.GET);
        request.setBody(orderDelivery);
        ClientUI.chat.accept(request);// sending the request to the server.
        return handleRecivedDateFromServer(method);
    }
    
    private String handleRecivedDateFromServer(PickUpMethod method) {
    	if (method == PickUpMethod.delivery && Client.resFromServer.getPath().equals("/order/RecivedDateDelivery") ||
    			method == PickUpMethod.latePickUp && Client.resFromServer.getPath().equals("/order/RecivedDatePickup")) {
    		
          	switch (Client.resFromServer.getCode()) {
            case OK:
            	return (String) (Client.resFromServer.getBody().get(0));
            default:
                break;
        	}
    	}
    	return null;
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
    	
    	setPickupCode(txtOrderID.getText());
    	ShowEnterOrderID.setVisible(false);
    	ShowPickupCode.setVisible(true);
    	txtOrderID.clear();
    }
    
    private void setPickupCode(String OrderID) {
    	List<Object> orderId = new ArrayList<>();
    	orderId.add(OrderID);
    	Request request = new Request();
        request.setPath("/order/pickupOrder/getPickupCode");
        request.setMethod(Method.GET);
        request.setBody(orderId);
        ClientUI.chat.accept(request);// sending the request to the server.
        
        handleGetPickupCodeFromServer();
    }
    
    private void handleGetPickupCodeFromServer()
    {
    	if (Client.resFromServer.getPath().equals("/order/pickupOrder/getPickupCode")) {
          	switch (Client.resFromServer.getCode()) {
            case OK:
            	pickupCode.setText( (String) (Client.resFromServer.getBody().get(0)));
            	break;
            default:
                break;
        	}
    	}
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
