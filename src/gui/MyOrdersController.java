package gui;

import java.net.URL;
import java.time.LocalDate;
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
import models.OrderStatus;
import models.PickUpMethod;
import models.Request;
import utils.Util;


public class MyOrdersController implements Initializable {

	@FXML
    private TableColumn<MyOrders, String> IdOrder;

    @FXML
    private TableColumn<MyOrders, OrderStatus> Status;

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
    private Label errorUpdateStatusDB;
    
    @FXML
    private TextField txtOrderID;
    
    @FXML
    private Label errorLabel;
    
    private List<MyOrders> listMyOrders;
    private List<MyOrders> listDeliveryNotCollected;
    ObservableList<MyOrders> orderObser;
    
    @FXML
    private AnchorPane anchorPane;
    
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		listMyOrders = new ArrayList<>();
		listDeliveryNotCollected = new ArrayList<>();
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


    @FXML
    void refreshAllBtn(ActionEvent event) {
    	orderObser.clear();
		getAllOrdersForSpecificUser();
		setStyleForEmptyTable();
		addButtonToTable();
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
                        	errorUpdateStatusDB.setVisible(false);
                        	String time = LocalDate.now().toString();
                        	if (isChangeStatusDeliveryOrderInDB(getTableView().getItems().get(getIndex()).getOrderId(),time)) {
                            	updateStatusDeliveryInLocallyListMyOrders(getTableView().getItems().get(getIndex()),time);
                            	getTableView().getItems().remove(getIndex());
                        	}                       	
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
    void Back(MouseEvent event) throws Exception {
		Stage primaryStage = StageSingleton.getInstance().getStage();
		Parent root = FXMLLoader.load(getClass().getResource("/assets/OLMain.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Main");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
    }
    
    private void setCellFactoryOfTables() {
		IdOrder.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("orderId"));
		Status.setCellValueFactory(new PropertyValueFactory<MyOrders, OrderStatus>("Status"));
		execDate.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("date"));
		recieveDate.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("receivedDate"));
		typeOrder.setCellValueFactory(new PropertyValueFactory<MyOrders, PickUpMethod>("pickUpMethod"));
		
		idOrderConfirm.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("orderId"));
		execDateConfirm.setCellValueFactory(new PropertyValueFactory<MyOrders, String>("date"));
    }
    private void updateStatusDeliveryInLocallyListMyOrders(MyOrders order, String time) {
    	int index = orderObser.indexOf(order);
    	order.setStatus(OrderStatus.Collected);
    	order.setReceivedDate(time);
    	orderObser.set(index, order);
    }
    
    private boolean isChangeStatusDeliveryOrderInDB(String orderId, String time) {
    	List<Object> objects = new ArrayList<>();
    	objects.add(orderId);
    	objects.add(OrderStatus.Collected);
    	objects.add(time);
    	Request request = new Request();
        request.setPath("/order/deliveryOrder/changeStatusAndDateReceived");
        request.setMethod(Method.PUT);
        request.setBody(objects);
        ClientUI.chat.accept(request);// sending the request to the server.
        return isSetStatusDelivery();
    }
    
    private boolean isSetStatusDelivery() {
    	switch (Client.resFromServer.getCode()) {
        case OK:
            return true;
        default:
        	errorUpdateStatusDB.setVisible(true);
        	errorUpdateStatusDB.setText(Client.resFromServer.getDescription());
            break;
    	}
    	return false;
    }
    
    private void getAllOrdersForSpecificUser()
    {
    	List<Object> userId = new ArrayList<>();
    	userId.add(LoginController.user.getId());
    	Request request = new Request();
        request.setPath("/user/myOrders");
        request.setMethod(Method.GET);
        request.setBody(userId);
        ClientUI.chat.accept(request);// sending the request to the server.
    	
        handleRsponseGetMyOrders();
    	 
        orderObser = FXCollections.observableArrayList();
        orderObser.addAll(listMyOrders);
		tableViewOrders.setItems(orderObser);

		ObservableList<MyOrders> DeliveriesNotCollected = FXCollections.observableArrayList();
		DeliveriesNotCollected.addAll(listDeliveryNotCollected);
		tableViewApproveDel.setItems(DeliveriesNotCollected);
    }
    
    private void handleRsponseGetMyOrders() {
    	switch (Client.resFromServer.getCode()) {
        case OK:
        	updateMyOrders(Client.resFromServer.getBody());
            break;
        default:
        	setErrorPlaceHolder();
            break;
    	}
    }
    
    private void setErrorPlaceHolder() {
		Label tableViewApproveLabel = new Label ("Error: loading data");
		tableViewApproveLabel.setStyle(
				"-fx-text-fill : #FF3547;-fx-font-weight: bold;  -fx-font-size: 12");
		Label tableViewOrdersLabel = new Label ("Error: loading data");
		tableViewOrdersLabel.setStyle(
				"-fx-text-fill : #FF3547;-fx-font-weight: bold; -fx-font-size: 17");
		tableViewOrders.setPlaceholder(tableViewOrdersLabel);
		tableViewApproveDel.setPlaceholder(tableViewApproveLabel);
    }
    
    
    private void updateMyOrders(List<Object> listOrders){
    	listMyOrders.clear();
    	listDeliveryNotCollected.clear();
    	if (listOrders == null) {
    		return;
    	}
    	for (Object order:listOrders) {
    		if (order instanceof MyOrders) {
    			MyOrders tempOrder = (MyOrders)order;
    			if (tempOrder.getPickUpMethod() == PickUpMethod.delivery || tempOrder.getPickUpMethod() == PickUpMethod.latePickUp) {
        			checkAndAddDeliveryOrderNotCollected(tempOrder);
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
            request.setPath("/order/ReceivedDateDelivery");
    	}
    	else {
            request.setPath("/order/ReceivedDatePickup");
    	}
        request.setMethod(Method.GET);
        request.setBody(orderDelivery);
        ClientUI.chat.accept(request);// sending the request to the server.
        return handleRecivedDateFromServer();
    }
    
    private void checkAndAddDeliveryOrderNotCollected(MyOrders order) {
		if (order.getPickUpMethod() == PickUpMethod.delivery && order.getStatus() == OrderStatus.NotCollected ) {
			listDeliveryNotCollected.add(order);
		}
    }
    
    private String handleRecivedDateFromServer() {
          	switch (Client.resFromServer.getCode()) {
            case OK:
            	return (String) (Client.resFromServer.getBody().get(0));
            default:
            	return "Loading problem";
        	}
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
      	switch (Client.resFromServer.getCode()) {
        case OK:
        	pickupCode.setText((String) (Client.resFromServer.getBody().get(0)));
        	break;
        default:
        	pickupCode.setText(Client.resFromServer.getDescription());
            break;
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
    		if (orderID.equals(order.getOrderId())){
    			if (order.getPickUpMethod() == PickUpMethod.latePickUp) {
    				if ( order.getStatus() == OrderStatus.NotCollected) {
    	    			return true;
    				}
    				setErrorLabel("The order was taken");
    				return false;
    			}
    			setErrorLabel("The selected order is not of the pickup type");
    			return false;
    		}
    	}
    	setErrorLabel("Invalid Order ID");
    	return false;
    }
    
    private void setErrorLabel(String msg) {
    	errorLabel.setText(msg);
    	txtOrderID.getStyleClass().add("validation-error");
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
