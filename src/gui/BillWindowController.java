package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import client.Client;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.*;


public class BillWindowController implements Initializable {

    public static Order restoreOrder;

    @FXML
    private ImageView backBtn;
    @FXML
    private TableColumn<ProductInOrder, Double> amountCol;

    @FXML
    private TableColumn<ProductInOrder, Double> priceCol;

    @FXML
    private TableColumn<ProductInOrder, String> productCol;

    @FXML
    private TableView<ProductInOrder> billTable;

    @FXML
    private Button proceedPaymentBtn;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label nameLabel;

    public static Order order;


    Integer machineId;
    int customerId;

    public void initBillWindow() {
        totalPriceLabel.setText(StyleConstants.TOTAL_PRICE_STRING + Double.toString(NewOrderController.previousOrder.getPrice()) + StyleConstants.CURRENCY_INS);
        initBillTable(NewOrderController.previousOrder);
        restoreOrder = NewOrderController.previousOrder;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT);
        restoreOrder.setDate(LocalDate.now().format(formatter));
        if(restoreOrder.getPickUpMethod() == PickUpMethod.latePickUp)
            ((PickupOrder)restoreOrder).setPickupCode(UUID.randomUUID().toString().replace("-","").substring(0,15));


    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        initBillWindow();
        nameLabel.setText(NewOrderController.user.getFirstName() + " " + NewOrderController.user.getLastName());
        machineId = Integer.parseInt(NewOrderController.previousOrder.getMachineId());
        customerId = NewOrderController.user.getId();
    }

    public void initBillTable(Order order) {
        amountCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("amount"));
        priceCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("totalProductPrice"));
        productCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, String>("productName"));
        ObservableList<ProductInOrder> productsInBill = FXCollections.observableArrayList();
        productsInBill.addAll(order.getProductsInOrder());
        billTable.setItems(productsInBill);
    }

    public boolean validateProductsInInventory(){
        List<ProductInMachine> machineProducts = requestMachineProducts(machineId);
        for(ProductInOrder productInOrder: restoreOrder.getProductsInOrder()){
            for(ProductInMachine productInMachine: machineProducts){
                if(productInOrder.getProduct().getProductId().equals(productInMachine.getProductId()) && productInOrder.getAmount() > productInMachine.getAmount())
                    return false;
            }
        }
        return true;
    }

    public void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
        Alert alert = new Alert(alertType); //Information, Error
        alert.setContentText(alertTitle); // Information, Error
        alert.setContentText(alertMessage);
        alert.show();
    }

    @FXML
    void proceedPaymentClicked(ActionEvent event) {
        if(!validateProductsInInventory()){
            restoreOrder = null;

            replaceWindowToNewOrder();
            createAnAlert(Alert.AlertType.ERROR, StyleConstants.OUT_OF_STOCK_LABEL, StyleConstants.INVENTORY_UPDATE_ALERT_MSG);
            return;
        }
        requestSaveOrder();
        requestSaveProductsInOrder();
        updateInventoryInDB();
        requestSaveDeliveryOrder();
        requestSaveLatePickUpOrder();

        changeToConfirmationOrderPopUpWindow();
    }

    void requestSaveOrder() {
        List<Object> orderList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/newOrder");
        request.setMethod(Method.POST);
        UUID uuid = UUID.randomUUID();
        String orderId = uuid.toString();
        NewOrderController.previousOrder.setOrderId(orderId);
        orderList.add(NewOrderController.previousOrder);
        request.setBody(orderList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
    }

    void requestSaveDeliveryOrder(){
        if(NewOrderController.previousOrder.getPickUpMethod() == PickUpMethod.delivery){
            DeliveryOrder deliveryOrder = (DeliveryOrder)NewOrderController.previousOrder;
            List<Object> paramList = new ArrayList<>();
            Request request = new Request();
            request.setPath("/saveDeliveryOrder");
            request.setMethod(Method.POST);
            paramList.add(deliveryOrder);
            request.setBody(paramList);
            ClientUI.chat.accept(request);// sending the request to the server.
            switch (Client.resFromServer.getCode()) {
                case OK:
                    break;
                default:
                    System.out.println("Some error occurred");
            }
        }
    }

    void requestSaveLatePickUpOrder(){
        if(NewOrderController.previousOrder.getPickUpMethod() == PickUpMethod.latePickUp){
            PickupOrder latePickUpOrder = (PickupOrder)NewOrderController.previousOrder;
            List<Object> paramList = new ArrayList<>();
            Request request = new Request();
            request.setPath("/saveLatePickUpOrder");
            request.setMethod(Method.POST);
            paramList.add(latePickUpOrder);
            request.setBody(paramList);
            ClientUI.chat.accept(request);// sending the request to the server.
            switch (Client.resFromServer.getCode()) {
                case OK:
                    break;
                default:
                    System.out.println("Some error occurred");
            }
        }
    }

    void requestSaveProductsInOrder() {
        List<Object> orderList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/saveProductsInOrder");
        request.setMethod(Method.POST);
        orderList.add(restoreOrder.getOrderId());
        orderList.add(NewOrderController.previousOrder.getProductsInOrder());
        request.setBody(orderList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
    }

    public int getMachineThreshold() {
        int threshold = -1;
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/getMachineThreshold");
        request.setMethod(Method.GET);
        paramList.add(machineId);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                threshold = (Integer) Client.resFromServer.getBody().get(0);
            default:
                System.out.println("Some error occurred");
        }
        System.out.println(threshold);
        return threshold;
    }




    public void updateInventoryInDB(){
        List<ProductInMachine> updatedInventory = getUpdatedInventory();
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/updateInventory");
        request.setMethod(Method.PUT);
        paramList.add(updatedInventory);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
    }

    public List<ProductInMachine> getUpdatedInventory() {
        List<ProductInMachine> updatedMachineList = new ArrayList<>();
        List<ProductInMachine> productsInMachineList = requestMachineProducts(machineId);
        StatusInMachine newStatusInMachine;
        Integer getMachineThreshold = getMachineThreshold();
        ProductInMachine productInMachine;
        for (ProductInOrder productInOrder : restoreOrder.getProductsInOrder()) {
            int newAmount = getProductMachineAmountFromList(productsInMachineList, machineId, Integer.valueOf(productInOrder.getProduct().getProductId())) - productInOrder.getAmount();
            if (newAmount == 0)
                newStatusInMachine = StatusInMachine.Not_Available;
            else if (newAmount < getMachineThreshold) {
                newStatusInMachine = StatusInMachine.Below;
            } else {
                newStatusInMachine = StatusInMachine.Above;
            }
            productInMachine = new ProductInMachine(machineId.toString(), productInOrder.getProduct().getProductId(), newStatusInMachine, newAmount);
            updatedMachineList.add(productInMachine);
        }
        return updatedMachineList;
    }



    public Integer getProductMachineAmountFromList(List<ProductInMachine> productsInMachineList, Integer machineId, Integer productId) {
        for (ProductInMachine productInMachine : productsInMachineList) {
            if ((machineId.toString()).equals(productInMachine.getMachineId()) && Objects.equals(productId, Integer.valueOf(productInMachine.getProductId())))
                return productInMachine.getAmount();
        }
        return -1;
    }

    public List<ProductInMachine> requestMachineProducts(Integer machineId) {
        List<ProductInMachine> productInMachineList = new ArrayList<>();
        List<Object> listObject = new ArrayList<>();
        Request request = new Request();
        request.setPath("/requestMachineProducts");
        request.setMethod(Method.GET);
        listObject.add(machineId);
        request.setBody(listObject);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> result = Client.resFromServer.getBody();
                for (Object obj : result) {
                    productInMachineList.add((ProductInMachine) obj);
                }
            default:
                System.out.println("Some error occurred");
        }
        return productInMachineList;
    }


    public void changeToConfirmationOrderPopUpWindow() {
        AnchorPane pane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(StylePaths.CONFIRM_ORDER_POPUP_PATH));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = StageSingleton.getInstance().getStage();
        stage.setTitle(StyleConstants.ORDER_CONFIRMATION_TITLE_LABEL);
        stage.setScene(new Scene(pane));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setMinHeight(ConfirmationOrderPopUpWindowController.POP_UP_HEIGHT);
        stage.setMinWidth(ConfirmationOrderPopUpWindowController.POP_UP_WIDTH);
        stage.setWidth(ConfirmationOrderPopUpWindowController.POP_UP_WIDTH);
        stage.setHeight(ConfirmationOrderPopUpWindowController.POP_UP_HEIGHT);
    }

    @FXML
    void backBtnClicked(MouseEvent event) {
        replaceWindowToNewOrder();
    }

    void replaceWindowToNewOrder(){
        AnchorPane pane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(StylePaths.NEW_ORDER_WINDOW_PATH));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = StageSingleton.getInstance().getStage();
        stage.setTitle(StyleConstants.NEW_ORDER_WINDOW_LABEL);
        stage.setScene(new Scene(pane));
        stage.centerOnScreen();
        stage.setResizable(false);
    }
}