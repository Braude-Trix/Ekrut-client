package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;

import client.Client;
import client.ClientUI;
import gui.StageSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
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


    public static Order order;


    Integer machineId = 1;
    int customerId = 100;

    public void initBillWindow() {
        totalPriceLabel.setText("Total price:" + Double.toString(NewOrderController.previousOrder.getPrice()) + " ₪");
        initBillTable(NewOrderController.previousOrder);
        restoreOrder = NewOrderController.previousOrder;

    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        initBillWindow();
    }

    public void initBillTable(Order order) {
        amountCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("amount"));
        priceCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("totalProductPrice"));
        productCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, String>("productName"));
        ObservableList<ProductInOrder> productsInBill = FXCollections.observableArrayList();
        productsInBill.addAll(order.getProductsInOrder());
        billTable.setItems(productsInBill);
    }


    @FXML
    void proceedPaymentClicked(ActionEvent event) {
        requestSaveOrder();
        requestSaveProductsInOrder();
        updateInventoryInDB();
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
            loader.setLocation(getClass().getResource("/assets/OrderConfirmationPopUpWindow.fxml"));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = StageSingleton.getInstance().getStage();
        stage.setTitle("Order confirmation");
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
        AnchorPane pane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/NewOrder.fxml"));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = StageSingleton.getInstance().getStage();
        stage.setTitle("Order confirmation");
        stage.setScene(new Scene(pane));
        stage.centerOnScreen();
        stage.setResizable(false);
    }


}
