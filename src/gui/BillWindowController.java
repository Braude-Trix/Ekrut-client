package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

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
        changeToConfirmationOrderPopUpWindow();
    }

    void requestSaveOrder() {
        List<Object> orderList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/newOrder");
        request.setMethod(Method.POST);
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


//    public class checkMessages implements Runnable {
//        public void run() {
//            while (true) {
//                try {
//                    checkNewMessage();
//                    Thread.sleep(5000);
//
//                } catch (InterruptedException e) {
//                    System.out.println(e);
//                }
//
//            }
//        }
//
//        public void checkNewMessage(){
//            switch (Client.resFromServer.getCode()) {
//                case OK:
//                    if(Client.resFromServer.getBody() != null){
//                        System.out.println((String)Client.resFromServer.getBody().get(0));
//                    }
//                default:
//                    System.out.println("No message");
//            }
//
//        }
//    }


}
