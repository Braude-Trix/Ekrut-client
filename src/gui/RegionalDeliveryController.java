package gui;

import client.Client;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import models.DeliveryOrder;
import models.Method;
import models.Request;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegionalDeliveryController implements Initializable {

    @FXML
    private TableView<DeliveryTable> ordersTable;

    @FXML
    private TableColumn<DeliveryTable, CheckBox> appDenyCol;

    @FXML
    private TableColumn<DeliveryTable, String> deliveryAddCol;

    @FXML
    private TableColumn<DeliveryTable, String> orderDateCol;

    @FXML
    private TableColumn<DeliveryTable, String> orderIdCol;

    @FXML
    private Button saveBtnId;

    @FXML
    private ImageView saveImg;
    @FXML
    private ImageView refreshBtn;

    @FXML
    private Label mainTitleLabel;
    @FXML
    private Label sideTitleLabel;

    private static String staticRegion = "Haifa";


    public static class DeliveryTable {
        private String orderId;
        private CheckBox approveDenyCheckBox;
        private String deliveryDate;
        private String deliveryAddress;

        public DeliveryTable(String orderId, String deliveryDate, String deliveryAddress, CheckBox approveDenyCheckBox) {
            this.orderId = orderId;
            this.deliveryDate = deliveryDate;
            this.deliveryAddress = deliveryAddress;
            this.approveDenyCheckBox = approveDenyCheckBox;
        }


        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(String orderDate) {
            this.deliveryDate = orderDate;
        }

        public String getDeliveryAddress() {
            return deliveryAddress;
        }

        public void setDeliveryAddress(String deliveryAddress) {
            this.deliveryAddress = deliveryAddress;
        }

        public CheckBox getApproveDenyCheckBox() {
            return approveDenyCheckBox;
        }

        public void setApproveDenyCheckBox(CheckBox approveDenyCheckBox) {
            this.approveDenyCheckBox = approveDenyCheckBox;
        }
    }

    public void initTable(String region) {
        orderIdCol.setCellValueFactory(new PropertyValueFactory<DeliveryTable, String>("orderId"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<DeliveryTable, String>("deliveryDate"));
        deliveryAddCol.setCellValueFactory(new PropertyValueFactory<DeliveryTable, String>("deliveryAddress"));
        appDenyCol.setCellValueFactory(new PropertyValueFactory<DeliveryTable, CheckBox>("approveDenyCheckBox"));
        addItemsToTable(region);
    }

    public void addItemsToTable(String region) {
        //List<DeliveryOrder> deliveryOrders = requestDeliveriesOrdersByRegion(region);
        DeliveryOrder deliv = new DeliveryOrder("1234","Nesher", "Haifa", "2734563");
        List<DeliveryOrder> deliveryOrders = new ArrayList<>();
        deliveryOrders.add(deliv);


        ObservableList<DeliveryTable> regionalDeliveriesList = FXCollections.observableArrayList();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            CheckBox approveCheckBox = new CheckBox();
            DeliveryTable deliveryTable = new DeliveryTable(deliveryOrder.getOrderId(), deliveryOrder.getDeliveryDate(), deliveryOrder.getDeliveryAddress(), approveCheckBox);
            regionalDeliveriesList.add(deliveryTable);
        }
        ordersTable.setItems(regionalDeliveriesList);
        ordersTable.fixedCellSizeProperty();
    }

    public List<DeliveryOrder> requestDeliveriesOrdersByRegion(String region) {
        List<DeliveryOrder> deliveryOrders = new ArrayList<>();
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/getDeliveriesOrdersByRegion");
        request.setMethod(Method.GET);
        paramList.add(region);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                for (Object deliveryOrder : Client.resFromServer.getBody())
                    deliveryOrders.add((DeliveryOrder) deliveryOrder);

            default:
                System.out.println("Some error occurred");
        }
        return deliveryOrders;
    }


    public void initialize(URL url, ResourceBundle resourceBundle) {
        reload();
    }


    @FXML
    void saveClicked(ActionEvent event) throws Exception {
        saveFunctionallity();
    }

    public void saveFunctionallity() {
        int orderTableSize = ordersTable.getItems().size();
        List<DeliveryTable> deliveryTableListToRemove = new ArrayList<>();
        if (ordersTable.getItems().size() > 0) {
            for (DeliveryTable deliveryTable : ordersTable.getItems()) {
                if (deliveryTable.getApproveDenyCheckBox().isSelected()) {
                    String orderId = deliveryTable.getOrderId();
                    //write to DB the result
                    deliveryTableListToRemove.add(deliveryTable);
                }
            }
            ordersTable.getItems().removeAll(deliveryTableListToRemove);
            if (deliveryTableListToRemove.size() == orderTableSize) {
                saveBtnId.setOpacity(0.5);
                saveImg.setOpacity(0.5);
            }
            //if not raised excpetion so
            createAnAlert(Alert.AlertType.INFORMATION, "Information", "All your updates save successfully");
            //else
            //createAnFailedAlert();
        }
    }

    @FXML
    void saveImgClicked(MouseEvent event) {
        saveFunctionallity();
    }

    public void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
        Alert alert = new Alert(alertType); //Information, Error
        alert.setContentText(alertTitle); // Information, Error
        alert.setContentText(alertMessage);
        alert.show();
    }

    @FXML
    void refreshBtnClicked(MouseEvent event) {
        reload();
        createAnAlert(Alert.AlertType.INFORMATION, "Information", "Successfully reload");
    }

    void reload() {
        initTable(staticRegion);
        saveBtnId.setOpacity(1);
        saveImg.setOpacity(1);
        if (ordersTable.getItems().size() == 0) {
            saveBtnId.setOpacity(0.5);
            saveImg.setOpacity(0.5);
        }
    }

}
