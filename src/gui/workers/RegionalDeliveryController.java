package gui.workers;


import client.Client;
import client.ClientUI;
import gui.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.*;
import utils.Util;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import static utils.Util.forcedExit;

/**
 * class that represents Controller to RegionalDelivery
 */
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

    private Worker worker = (Worker) LoginController.user;
    private Integer myId = worker.getId();
    private String staticRegion = worker.getRegion().toString(); //"North";

    /**
     * function that start the fxml of the current window
     *
     * @param primaryStage - Singleton in our program
     * @throws Exception - FXMLLoader Exception
     */
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/assets/workers/RegionalDeliveryHomePage_Default.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Regional Manager");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setOnCloseRequest(e -> {
            try {
                forcedExit();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * class that represents the table of Pending deliveries, with 4 columns of orderId, checkBox, deliveryDate, and deliveryAddress.
     */
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

    /**
     * class that represents the table of Confirmed deliveries, with 4 columns of orderId, deliveryAcceptanceDate, deliveryDate, and confirm btn.
     */
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

    /**
     * function that initialize the pending orders table according to the worker region
     *
     * @param region - Region Enum
     */
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

    /**
     * function that initialize the confirm orders table according to the worker region
     *
     * @param region - Region Enum
     */
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

    /**
     * function that add items the pending orders table according to the worker region
     *
     * @param region - Region Enum
     */
    public void addItemsToPendingTable(String region) {
        List<DeliveryOrder> deliveryToRemove = new ArrayList<>();
        List<DeliveryOrder> deliveryOrders = requestPendingDeliveriesOrdersByRegion(region);
        Map<String, String> resMap = requestWaitingDeliveryOrdersWithDate();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            if (!resMap.containsKey(deliveryOrder.getOrderId())) {
                deliveryToRemove.add(deliveryOrder);
            }
            deliveryOrder.setDate(resMap.get(deliveryOrder.getOrderId()));
        }
        deliveryOrders.removeAll(deliveryToRemove);
        ObservableList<PendingDeliveryTable> regionalDeliveriesList = FXCollections.observableArrayList();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            CheckBox approveCheckBox = new CheckBox();
            PendingDeliveryTable PendingDeliveryTable = new PendingDeliveryTable(deliveryOrder.getOrderId(), deliveryOrder.getDate(), deliveryOrder.getFullAddress(), approveCheckBox);
            regionalDeliveriesList.add(PendingDeliveryTable);
        }
        ordersTable.setItems(regionalDeliveriesList);
        ordersTable.fixedCellSizeProperty();
        if (ordersTable.getItems().size() == 0) {

        }
    }

    /**
     * function that handle that in confirm table, one of the V buttons in some row clicked.
     *
     * @param confirmBtn, confirmDeliveriesList, confirmDeliveryTable
     */
    void VImageClicked(Button confirmBtn, ObservableList<ConfirmDeliveryTable> confirmDeliveriesList, ConfirmDeliveryTable confirmDeliveryTable) {
        confirmBtn.setOnMouseClicked(event -> {
            confirmDeliveriesList.remove(confirmDeliveryTable);
            ConfirmDeliveryTable.setItems(confirmDeliveriesList);
            updateOrderStatus(confirmDeliveryTable.getOrderId(), OrderStatus.Done);
        });
    }

    /**
     * function that add items the confirm orders table according to the worker region
     *
     * @param region - Region Enum
     */
    public void addItemsToConfirmTable(String region) {
        List<DeliveryOrder> deliveryToRemove = new ArrayList<>();
        List<DeliveryOrder> deliveryOrders = requestPendingDeliveriesOrdersByRegion(region);
        Map<String, String> resMap = requestCollectedDeliveryOrdersWithDate();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            if (!resMap.containsKey(deliveryOrder.getOrderId())) {
                deliveryToRemove.add(deliveryOrder);
            }
            deliveryOrder.setDate(resMap.get(deliveryOrder.getOrderId()));
        }
        deliveryOrders.removeAll(deliveryToRemove);
        ObservableList<ConfirmDeliveryTable> confirmDeliveriesList = FXCollections.observableArrayList();
        for (DeliveryOrder deliveryOrder : deliveryOrders) {
            Button confirmBtn = new Button();
            Image imageSave = new Image(StylePaths.VIMAGE_PATH);
            ImageView imageViewV = new ImageView();
            imageViewV.setImage(imageSave);
            imageViewV.setFitWidth(20);
            imageViewV.setFitHeight(20);
            confirmBtn.setGraphic(imageViewV);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT);
            LocalDate date = LocalDate.parse(deliveryOrder.getDate(), formatter);
            LocalDate newDeliveryDate = date.plusDays(7);
            //formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
           //String formattedDate = newDeliveryDate.format(formatter);
            String formattedDate = newDeliveryDate.format(DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT));



            ConfirmDeliveryTable confirmDeliveryTable = new ConfirmDeliveryTable(deliveryOrder.getOrderId(), formattedDate.toString(),deliveryOrder.getDate(), confirmBtn);
            VImageClicked(confirmBtn, confirmDeliveriesList, confirmDeliveryTable);
            confirmDeliveriesList.add(confirmDeliveryTable);
        }
        ConfirmDeliveryTable.setItems(confirmDeliveriesList);
        ConfirmDeliveryTable.fixedCellSizeProperty();
    }

    private List<DeliveryOrder> requestPendingDeliveriesOrdersByRegion(String region) {
        List<DeliveryOrder> resList = new ArrayList<>();
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/getPendingDeliveriesOrdersByRegion");
        request.setMethod(Method.GET);
        paramList.add(region);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                if (Client.resFromServer.getBody() == null) break;
                for (Object deliveryOrder : Client.resFromServer.getBody())
                    resList.add((DeliveryOrder) deliveryOrder);
                break;
            default:
                System.out.println("Some error occurred");
        }
        return resList;
    }


    private Map<String, String> requestCollectedDeliveryOrdersWithDate() {
        Request request = new Request();
        request.setPath("/getCollectedDeliveryOrdersWithDate");
        request.setMethod(Method.GET);
        request.setBody(null);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                return (Map<String, String>) Client.resFromServer.getBody().get(0);
            default:
                System.out.println("Some error occurred");
        }
        return null;
    }

    private Map<String, String> requestWaitingDeliveryOrdersWithDate() {
        Request request = new Request();
        request.setPath("/getWaitingDeliveryOrdersWithDate");
        request.setMethod(Method.GET);
        request.setBody(null);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                return (Map<String, String>) Client.resFromServer.getBody().get(0);
            default:
                System.out.println("Some error occurred");
        }
        return null;
    }

    private Integer requestCustomerId(String orderId) {
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

    /**
     * function that called when the fxml loaded, handle the username label text
     *
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {
        userNameLabel.setText("Hello " + worker.getFirstName() + " " + worker.getLastName());


    }


    private void writeNewMsgToDB(String msg, Integer fromCustomerId, Integer toCustomerId) {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/postMsg");
        request.setMethod(Method.POST);
        paramList.add(msg);
        paramList.add(fromCustomerId);
        paramList.add(toCustomerId);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
    }

    private void updateOrderStatus(String orderId, OrderStatus orderStatus) {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/updateOrderStatus");
        request.setMethod(Method.PUT);
        paramList.add(orderId);
        paramList.add(orderStatus);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
    }

    private void saveFunctionallity(Button saveBtnId) {
        String msg;
        int orderTableSize = ordersTable.getItems().size();
        List<PendingDeliveryTable> PendingDeliveryTableListToRemove = new ArrayList<>();
        if (ordersTable.getItems().size() > 0) {
            for (PendingDeliveryTable PendingDeliveryTable : ordersTable.getItems()) {
                if (PendingDeliveryTable.getApproveDenyCheckBox().isSelected()) {
                    String orderId = PendingDeliveryTable.getOrderId();
                    updateOrderStatus(orderId, OrderStatus.NotCollected);
                    String deliveryDate = PendingDeliveryTable.getDeliveryDate();
                    String deliveryAddress = PendingDeliveryTable.getDeliveryAddress();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT);
                    LocalDate date = LocalDate.parse(deliveryDate, formatter);
                    LocalDate newDeliveryDate = date.plusDays(7);
                    String formattedDate = newDeliveryDate.format(DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT));
                    msg = StyleConstants.HEADER_MSG_TO_CLIENT + orderId + StyleConstants.AFTER_HEADER_MSG_TO_CLIENT + formattedDate + StyleConstants.FOOTER_HEADER_MSG_TO_CLIENT + deliveryAddress;
                    writeNewMsgToDB(msg, myId, requestCustomerId(orderId));
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


    private void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
        Alert alert = new Alert(alertType); //Information, Error
        alert.setContentText(alertTitle); // Information, Error
        alert.setContentText(alertMessage);
        alert.show();
    }


    @FXML
    private void confirmDeliveriesClicked(ActionEvent event) {
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

    private void refreshImageViewClickedInConfirmTable(ImageView refreshImageView) {
        refreshImageView.setOnMouseClicked((event3) -> {
            addItemsToConfirmTable(staticRegion);
        });
    }


    @FXML
    private void pendingDeliveriesClicked(ActionEvent event) {

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
        if (ordersTable.getItems().size() == 0) {
            saveBtn.setOpacity(0.5);
        }
        saveBtnClicked(saveBtn);

    }

    private void saveBtnClicked(Button saveBtn) {
        saveBtn.setOnMouseClicked((event2) -> {
            saveFunctionallity(saveBtn);
        });
    }

    private void refreshImageViewClickedInPendingTable(ImageView refreshImageView, Button saveBtn) {
        refreshImageView.setOnMouseClicked((event3) -> {
            addItemsToPendingTable(staticRegion);
            if (ordersTable.getItems().size() > 0) saveBtn.setOpacity(1);
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

    @FXML
    private void logOutClicked(ActionEvent event) {
        try {
            Util.genricLogOut(getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
