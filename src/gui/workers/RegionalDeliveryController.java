package gui.workers;


import client.Client;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import models.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class RegionalDeliveryController implements Initializable {
    @FXML
    private ImageView bgImage;

    @FXML
    private VBox bottomBroderVbox;

    @FXML
    private VBox centerBroderVbox;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button confirmDeliveriesBtn;

    @FXML
    private Button pendingDeliveriesBtn;

    @FXML
    private VBox topBorderVBox;

    @FXML
    private Label userNameLabel;


    private TableView<PendingDeliveryTable> ordersTable;

    private TableColumn<PendingDeliveryTable, CheckBox> appDenyCol;

    private TableColumn<PendingDeliveryTable, String> deliveryAddCol;

    private TableColumn<PendingDeliveryTable, String> orderDateCol;

    private TableColumn<PendingDeliveryTable, String> orderIdCol;

    private TableView<ConfirmDeliveryTable> ConfirmDeliveryTable;

    private TableColumn<ConfirmDeliveryTable, String> orderIdColConfirmTable;
    private TableColumn<ConfirmDeliveryTable, String> orderDateColConfirmTable;
    private TableColumn<ConfirmDeliveryTable, String> deliveryDateConfirmTable;
    private TableColumn<ConfirmDeliveryTable, Button> confirmBtnColConfirmTable;


    @FXML
    private Label mainTitleLabel;
    @FXML
    private Label sideTitleLabel;

    private static String staticRegion = "Haifa";

    private Integer customerId = 1;


    public static class PendingDeliveryTable {
        private String orderId;
        private CheckBox approveDenyCheckBox;
        private String deliveryDate;
        private String deliveryAddress;

        public PendingDeliveryTable(String orderId, String deliveryDate, String deliveryAddress, CheckBox approveDenyCheckBox) {
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


    public static class ConfirmDeliveryTable {
        private String orderId;
        private String deliveryAcceptanceDate;
        private String deliveryDate;
        private Button confirmDeliveryBtn;

        public ConfirmDeliveryTable(String orderId, String deliveryAcceptanceDate, String deliveryDate, Button confirmDeliveryBtn) {
            this.orderId = orderId;
            this.deliveryAcceptanceDate = deliveryAcceptanceDate;
            this.deliveryDate = deliveryDate;
            this.confirmDeliveryBtn = confirmDeliveryBtn;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getDeliveryAcceptanceDate() {
            return deliveryAcceptanceDate;
        }

        public void setDeliveryAcceptanceDate(String deliveryAcceptanceDate) {
            this.deliveryAcceptanceDate = deliveryAcceptanceDate;
        }

        public String getDeliveryDate() {
            return deliveryDate;
        }

        public void setDeliveryDate(String deliveryDate) {
            this.deliveryDate = deliveryDate;
        }

        public Button getConfirmDeliveryBtn() {
            return confirmDeliveryBtn;
        }

        public void setConfirmDeliveryBtn(Button confirmDeliveryBtn) {
            this.confirmDeliveryBtn = confirmDeliveryBtn;
        }
    }

    public void initTablePendingOrders(String region) {
        ordersTable = new TableView<>();
        ordersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ordersTable.setPrefSize(580, 300);
        ordersTable.setMaxSize(ordersTable.getPrefWidth(), ordersTable.getPrefHeight());
        ordersTable.setPadding(new Insets(5, 0, 0, 0));

        orderIdCol = new TableColumn<>(StyleConstants.ORDER_ID_COL);
        orderDateCol = new TableColumn<>(StyleConstants.ORDER_DATE_COL);
        deliveryAddCol = new TableColumn<>(StyleConstants.DELIVERY_ADD_COL);
        appDenyCol = new TableColumn<>(StyleConstants.APP_DENY_COL);

        ordersTable.getColumns().addAll(orderIdCol, orderDateCol, deliveryAddCol, appDenyCol);

        orderIdCol.setCellValueFactory(new PropertyValueFactory<PendingDeliveryTable, String>("orderId"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<PendingDeliveryTable, String>("deliveryDate"));
        deliveryAddCol.setCellValueFactory(new PropertyValueFactory<PendingDeliveryTable, String>("deliveryAddress"));
        appDenyCol.setCellValueFactory(new PropertyValueFactory<PendingDeliveryTable, CheckBox>("approveDenyCheckBox"));
        addItemsToPendingTable(region);
    }

    public void initTableConfirmOrders(String region) {
        ConfirmDeliveryTable = new TableView<>();
        ConfirmDeliveryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ConfirmDeliveryTable.setPrefSize(580, 300);
        ConfirmDeliveryTable.setMaxSize(ConfirmDeliveryTable.getPrefWidth(), ConfirmDeliveryTable.getPrefHeight());
        ConfirmDeliveryTable.setPadding(new Insets(5, 0, 0, 0));

        orderIdColConfirmTable = new TableColumn<>(StyleConstants.ORDER_ID_COL);
        orderDateColConfirmTable = new TableColumn<>(StyleConstants.ORDER_DATE_COL);
        deliveryDateConfirmTable = new TableColumn<>(StyleConstants.DELIVERY_DATE_COL);
        confirmBtnColConfirmTable = new TableColumn<>(StyleConstants.CONFIRM_BTN_COL);

        ConfirmDeliveryTable.getColumns().addAll(orderIdColConfirmTable, orderDateColConfirmTable, deliveryDateConfirmTable, confirmBtnColConfirmTable);

        orderIdColConfirmTable.setCellValueFactory(new PropertyValueFactory<ConfirmDeliveryTable, String>("orderId"));
        orderDateColConfirmTable.setCellValueFactory(new PropertyValueFactory<ConfirmDeliveryTable, String>("deliveryDate"));
        deliveryDateConfirmTable.setCellValueFactory(new PropertyValueFactory<ConfirmDeliveryTable, String>("deliveryAcceptanceDate"));
        confirmBtnColConfirmTable.setCellValueFactory(new PropertyValueFactory<ConfirmDeliveryTable, Button>("confirmDeliveryBtn"));
        addItemsToConfirmTable(region);
    }

    public void addItemsToPendingTable(String region) {
        //List<DeliveryOrder> deliveryOrders = requestDeliveriesOrdersByRegion(region);
        DeliveryOrder deliv = new DeliveryOrder(null, "25", 0.0, "1",OrderStatus.WaitingApproveDelivery, PickUpMethod.delivery, 1, "null", "1", "5234523453", "zohar", Regions.South, "15","asdf");
        List<DeliveryOrder> deliveryOrders = new ArrayList<>();
        deliveryOrders.add(deliv);


        ObservableList<PendingDeliveryTable> regionalDeliveriesList = FXCollections.observableArrayList();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            CheckBox approveCheckBox = new CheckBox();
            PendingDeliveryTable PendingDeliveryTable = new PendingDeliveryTable(deliveryOrder.getOrderId(), deliveryOrder.getDate(), deliveryOrder.getFullAddress(), approveCheckBox);
            regionalDeliveriesList.add(PendingDeliveryTable);
        }
        ordersTable.setItems(regionalDeliveriesList);
        ordersTable.fixedCellSizeProperty();
    }

    void VImageClicked(Button confirmBtn, ObservableList<ConfirmDeliveryTable> confirmDeliveriesList, ConfirmDeliveryTable confirmDeliveryTable) {
        confirmBtn.setOnMouseClicked(event -> {
            confirmDeliveriesList.remove(confirmDeliveryTable);
            ConfirmDeliveryTable.setItems(confirmDeliveriesList);
        });
    }

    public void addItemsToConfirmTable(String region) {
        //List<DeliveryOrder> deliveryOrders = requestDeliveriesOrdersByRegion(region);
        DeliveryOrder deliv = new DeliveryOrder(null, "25", 0.0, "1",OrderStatus.WaitingApproveDelivery, PickUpMethod.delivery, 1, "null", "1", "5234523453", "zohar", Regions.South, "15","asdf");
        List<DeliveryOrder> deliveryOrders = new ArrayList<>();
        deliveryOrders.add(deliv);


        ObservableList<ConfirmDeliveryTable> confirmDeliveriesList = FXCollections.observableArrayList();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            Button confirmBtn = new Button();
            Image imageSave = new Image(StylePaths.VIMAGE_PATH);
            ImageView imageViewV = new ImageView();
            imageViewV.setImage(imageSave);
            imageViewV.setFitWidth(20);
            imageViewV.setFitHeight(20);
            confirmBtn.setGraphic(imageViewV);
            ConfirmDeliveryTable confirmDeliveryTable = new ConfirmDeliveryTable(deliveryOrder.getOrderId(), deliveryOrder.getDate(), deliveryOrder.getDateReceived(), confirmBtn);
            VImageClicked(confirmBtn, confirmDeliveriesList, confirmDeliveryTable);
            confirmDeliveriesList.add(confirmDeliveryTable);
        }
        ConfirmDeliveryTable.setItems(confirmDeliveriesList);
        ConfirmDeliveryTable.fixedCellSizeProperty();
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

    public Integer requestCustomerId(String orderId) {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/getCustomerIdByOrderId");
        request.setMethod(Method.GET);
        paramList.add(orderId);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                return Integer.parseInt(Client.resFromServer.getBody().get(0).toString());
            default:
                System.out.println("Some error occurred");
                return null;
        }
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {

    }


    void writeNewMsgToDB(String msg, Integer fromCustomerId, Integer toCustomerId) {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/postMsg");
        request.setMethod(Method.POST);
        paramList.add(msg);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
    }

    public void saveFunctionallity(Button saveBtnId) {
        String msg;
        int orderTableSize = ordersTable.getItems().size();
        List<PendingDeliveryTable> PendingDeliveryTableListToRemove = new ArrayList<>();
        if (ordersTable.getItems().size() > 0) {
            for (PendingDeliveryTable PendingDeliveryTable : ordersTable.getItems()) {
                if (PendingDeliveryTable.getApproveDenyCheckBox().isSelected()) {
                    String orderId = PendingDeliveryTable.getOrderId();
                    String deliveryDate = PendingDeliveryTable.getDeliveryDate();
                    String deliveryAddress = PendingDeliveryTable.getDeliveryAddress();
                    msg = StyleConstants.HEADER_MSG_TO_CLIENT + orderId + StyleConstants.AFTER_HEADER_MSG_TO_CLIENT + deliveryDate + StyleConstants.FOOTER_HEADER_MSG_TO_CLIENT + deliveryAddress;
                    //writeNewMsgToDB(msg, customerId, requestCustomerId(orderId));

                    //write to DB the result
                    PendingDeliveryTableListToRemove.add(PendingDeliveryTable);
                }
            }
            ordersTable.getItems().removeAll(PendingDeliveryTableListToRemove);
            if (PendingDeliveryTableListToRemove.size() == orderTableSize) {
                saveBtnId.setOpacity(0.5);
            }
            if (PendingDeliveryTableListToRemove.size() > 0)
                createAnAlert(Alert.AlertType.INFORMATION, StyleConstants.INFORMATION, StyleConstants.ALERT_CLIENT);

        }
    }


    public void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
        Alert alert = new Alert(alertType); //Information, Error
        alert.setContentText(alertTitle); // Information, Error
        alert.setContentText(alertMessage);
        alert.show();
    }


    @FXML
    void confirmDeliveriesClicked(ActionEvent event) {
        bgImage.setImage(new Image(StylePaths.REGIONAL_DELIVERY_SEC_BG));
        enableAll();
        clearBorderPane();
        initTableConfirmOrders(staticRegion);
        confirmDeliveriesBtn.setDisable(true);
        Label mainTitle = new Label(StyleConstants.CONFIRM_DELIVERY_HEADER + staticRegion);
        mainTitle.setFont(new Font("System", 32));
        topBorderVBox.getChildren().add(mainTitle);
        centerBroderVbox.getChildren().add(ConfirmDeliveryTable);
        Image imageRefresh = new Image(StylePaths.REFRESH_IMAGE_PATH);
        ImageView imageViewRefresh = new ImageView();
        imageViewRefresh.setImage(imageRefresh);
        imageViewRefresh.setFitWidth(25);
        imageViewRefresh.setFitHeight(25);
        imageViewRefresh.setOnMouseClicked(event2 -> refreshImageViewClickedInConfirmTable(imageViewRefresh));
        VBox vboxRefresh = new VBox();
        vboxRefresh.setAlignment(Pos.CENTER_LEFT);
        vboxRefresh.setPadding(new Insets(0, 20, 1, 20));
        vboxRefresh.getChildren().add(imageViewRefresh);
        bottomBroderVbox.getChildren().add(vboxRefresh);
    }

    public void refreshImageViewClickedInConfirmTable(ImageView refreshImageView) {
        refreshImageView.setOnMouseClicked((event3) -> {
            addItemsToConfirmTable(staticRegion);
        });
    }


    @FXML
    void pendingDeliveriesClicked(ActionEvent event) {
        bgImage.setImage(new Image(StylePaths.REGIONAL_DELIVERY_SEC_BG));
        initTablePendingOrders(staticRegion);
        enableAll();
        clearBorderPane();
        pendingDeliveriesBtn.setDisable(true);
        Label mainTitle = new Label(StyleConstants.PENDING_DELIVERY_HEADER + staticRegion);
        mainTitle.setFont(new Font("System", 32));
        topBorderVBox.getChildren().add(mainTitle);
        centerBroderVbox.getChildren().add(ordersTable);
        HBox btnHbox = new HBox();
        Image imageSave = new Image(StylePaths.SAVE_IMAGE_PATH);
        ImageView imageViewSave = new ImageView();
        imageViewSave.setImage(imageSave);
        imageViewSave.setFitWidth(25);
        imageViewSave.setFitHeight(25);
        Image imageRefresh = new Image(StylePaths.REFRESH_IMAGE_PATH);
        ImageView imageViewRefresh = new ImageView();
        imageViewRefresh.setImage(imageRefresh);
        imageViewRefresh.setFitWidth(25);
        imageViewRefresh.setFitHeight(25);
        Button saveBtn = new Button(StyleConstants.SAVE);
        saveBtn.setFont(new Font("System", 16));
        saveBtn.setGraphic(imageViewSave);
        VBox vboxSave = new VBox();
        vboxSave.setAlignment(Pos.CENTER_RIGHT);
        VBox vboxRefresh = new VBox();
        vboxRefresh.setAlignment(Pos.CENTER_LEFT);
        vboxSave.getChildren().add(saveBtn);
        vboxRefresh.getChildren().add(imageViewRefresh);
        btnHbox.getChildren().addAll(vboxRefresh, vboxSave);
        btnHbox.setAlignment(Pos.CENTER);
        btnHbox.setSpacing(450);
        btnHbox.setPadding(new Insets(0, 20, 1, 20));
        refreshImageViewClickedInPendingTable(imageViewRefresh, saveBtn);
        bottomBroderVbox.getChildren().add(btnHbox);
        saveBtnClicked(saveBtn);

    }

    public void saveBtnClicked(Button saveBtn) {
        saveBtn.setOnMouseClicked((event2) -> {
            saveFunctionallity(saveBtn);
        });
    }

    public void refreshImageViewClickedInPendingTable(ImageView refreshImageView, Button saveBtn) {
        refreshImageView.setOnMouseClicked((event3) -> {
            addItemsToPendingTable(staticRegion);
            if (ordersTable.getItems().size() > 0)
                saveBtn.setOpacity(1);
        });
    }

    private void enableAll() {
        confirmDeliveriesBtn.setDisable(false);
        pendingDeliveriesBtn.setDisable(false);
    }

    private void clearBorderPane() {
        topBorderVBox.getChildren().clear();
        centerBroderVbox.getChildren().clear();
        bottomBroderVbox.getChildren().clear();
    }

}
