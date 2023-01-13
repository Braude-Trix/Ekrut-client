package gui;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import client.Client;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Messages;
import models.*;
import utils.Util;
import utils.Utils;

/**
 * class that represents the bill window controller
 */
public class BillWindowController implements Initializable {
    public static Stage popupDialog;

    public static Order restoreOrder;

    public static boolean BillReplaced = false;
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

    private void initBillWindow() {
        totalPriceLabel.setText(StyleConstants.TOTAL_PRICE_STRING + " "+Double.toString(NewOrderController.previousOrder.getPrice()) + StyleConstants.CURRENCY_SYMBOL);
        initBillTable(NewOrderController.previousOrder);
        restoreOrder = NewOrderController.previousOrder;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT);
        restoreOrder.setDate(LocalDate.now().format(formatter));
        if (restoreOrder.getPickUpMethod() == PickUpMethod.latePickUp)
            ((PickupOrder) restoreOrder).setPickupCode(UUID.randomUUID().toString().replace("-", "").substring(0, 8));


    }

    /**
     * function that called while BillWindow fxml loaded, open the Timeout thread, init the window table and set vars
     *
     * @param url
     * @param resourceBundle
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (UserInstallationController.configuration.equals("EK")) {
            BillReplaced = false;
            Thread timeOutThread = new Thread(new TimeOutControllerBillWindow());
            timeOutThread.start();
        }
        initBillWindow();
        nameLabel.setText(NewOrderController.user.getFirstName() + " " + NewOrderController.user.getLastName());
        machineId = Integer.parseInt(NewOrderController.previousOrder.getMachineId());
        customerId = NewOrderController.user.getId();
    }

    private void initBillTable(Order order) {
        amountCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("amount"));
        priceCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, Double>("totalProductPrice"));
        productCol.setCellValueFactory(new PropertyValueFactory<ProductInOrder, String>("productName"));
        ObservableList<ProductInOrder> productsInBill = FXCollections.observableArrayList();
        productsInBill.addAll(order.getProductsInOrder());
        billTable.setItems(productsInBill);
    }

    private boolean validateProductsInInventory() {
        List<ProductInMachine> machineProducts = requestMachineProducts(machineId);
        for (ProductInOrder productInOrder : restoreOrder.getProductsInOrder()) {
            for (ProductInMachine productInMachine : machineProducts) {
                if (productInOrder.getProduct().getProductId().equals(productInMachine.getProductId()) && productInOrder.getAmount() > productInMachine.getAmount())
                    return false;
            }
        }
        return true;
    }

    /**
     * function that createAnAlert of javaFX using given alertType, String title of the alert and the alert message
     *
     * @param alertType    - AlertType object of javaFX
     * @param alertTitle   - String alert title
     * @param alertMessage - String alert message
     */
    public void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
        Alert alert = new Alert(alertType); //Information, Error
        alert.setContentText(alertTitle); // Information, Error
        alert.setContentText(alertMessage);
        alert.show();
    }

    /**
     * function that handle while proceedPayment Clicked, checked the inventory still exists, update the DB and handle the confirmation order popUp
     *
     * @param event
     */
    @FXML
    void proceedPaymentClicked(ActionEvent event) {
        if (!validateProductsInInventory()) {
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
        try {
            returnToMainPage();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        changeToConfirmationOrderPopUpWindow();
    }

    private void requestSaveOrder() {
        List<Object> orderList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/newOrder");
        request.setMethod(Method.POST);
        UUID uuid = UUID.randomUUID();
        String orderId = uuid.toString();
        if (NewOrderController.user instanceof Customer) {
            CustomerType customerType = ((Customer) NewOrderController.user).getType();
            if (customerType == CustomerType.Client)
                orderId = "0" + orderId;
            else if (customerType == CustomerType.Subscriber)
                orderId = "1" + orderId;
        }
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

    private void requestSaveDeliveryOrder() {
        if (NewOrderController.previousOrder.getPickUpMethod() == PickUpMethod.delivery) {
            DeliveryOrder deliveryOrder = (DeliveryOrder) NewOrderController.previousOrder;
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

    private void requestSaveLatePickUpOrder() {
        if (NewOrderController.previousOrder.getPickUpMethod() == PickUpMethod.latePickUp) {
            PickupOrder latePickUpOrder = (PickupOrder) NewOrderController.previousOrder;
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

    private void requestSaveProductsInOrder() {
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

    private int getMachineThreshold() {
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
                break;
            default:
                System.out.println("Some error occurred");
        }
        System.out.println(threshold);
        return threshold;
    }


    private void updateInventoryInDB() {
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

    private List<ProductInMachine> getUpdatedInventory() {
        boolean postMsg;
        int currentlyAmount, newAmount;
        String machineName;
        List<ProductInMachine> updatedMachineList = new ArrayList<>();
        List<ProductInMachine> productsInMachineList = requestMachineProducts(machineId);
        StatusInMachine newStatusInMachine;
        Integer getMachineThreshold = getMachineThreshold();
        ProductInMachine productInMachine;
        for (ProductInOrder productInOrder : restoreOrder.getProductsInOrder()) {
            postMsg = false;
            currentlyAmount = getProductMachineAmountFromList(productsInMachineList, machineId, Integer.valueOf(productInOrder.getProduct().getProductId()));
            newAmount = currentlyAmount - productInOrder.getAmount();
            if (currentlyAmount > getMachineThreshold && newAmount < getMachineThreshold && machineId != 1)
                postMsg = true;
            if (newAmount == 0) newStatusInMachine = StatusInMachine.Not_Available;
            else if (newAmount < getMachineThreshold) {
                newStatusInMachine = StatusInMachine.Below;
            } else {
                newStatusInMachine = StatusInMachine.Above;
            }
            machineName = getMachineNameById();
            updateManagerAboutProductsStatus(postMsg, "Inventory status:\nMachine id: " + machineId.toString() + "\nMachine name: " + machineName + "\nProduct id: " + Integer.valueOf(productInOrder.getProduct().getProductId()) +
                    "\nProduct name: " + productInOrder.getProduct().getName() + "\nStatus in machine: " + newStatusInMachine.toString() + "\nProduct amount: " + newAmount);
            productInMachine = new ProductInMachine(machineId.toString(), productInOrder.getProduct().getProductId(), newStatusInMachine, newAmount);
            updatedMachineList.add(productInMachine);
        }
        return updatedMachineList;
    }

    private String getMachineNameById() {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/getMachineName");
        request.setMethod(Method.GET);
        paramList.add(machineId.toString());
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            default:
                System.out.println("Some error occurred");
        }
        return Client.resFromServer.getBody().get(0).toString();

    }

    private void updateManagerAboutProductsStatus(boolean postMsg, String message) {
        if (postMsg) {
            List<Integer> managersIds = getRegionalManagerIds();
            for (Integer managerId : managersIds) {
                Messages.writeNewMsgToDB(message, customerId, managerId);
            }
        }
    }

    private Regions getRegionByMachineId() {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/requestRegionByMachineId");
        request.setMethod(Method.GET);
        paramList.add(machineId);
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                return Regions.valueOf(Client.resFromServer.getBody().get(0).toString());
            default:
                System.out.println("Some error occurred");
        }
        return null;
    }

    private List<Integer> getRegionalManagerIds() {
        List<Integer> managersIds = new ArrayList<>();
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/requestRegionalManagersIds");
        request.setMethod(Method.GET);
        paramList.add(getRegionByMachineId());
        request.setBody(paramList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> result = Client.resFromServer.getBody();
                for (Object obj : result) {
                    managersIds.add((Integer) obj);
                }
                break;
            default:
                System.out.println("Some error occurred");
        }
        return managersIds;
    }


    private Integer getProductMachineAmountFromList(List<ProductInMachine> productsInMachineList, Integer machineId, Integer productId) {
        for (ProductInMachine productInMachine : productsInMachineList) {
            if ((machineId.toString()).equals(productInMachine.getMachineId()) && Objects.equals(productId, Integer.valueOf(productInMachine.getProductId())))
                return productInMachine.getAmount();
        }
        return -1;
    }

    private List<ProductInMachine> requestMachineProducts(Integer machineId) {
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
                break;
            default:
                System.out.println("Some error occurred");
        }
        return productInMachineList;
    }


    private void changeToConfirmationOrderPopUpWindow() {
        BillReplaced = true;
        AnchorPane pane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(StylePaths.CONFIRM_ORDER_POPUP_PATH));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        restoreOrder = null;
        popupDialog = new Stage();
        popupDialog.setTitle(StyleConstants.ORDER_CONFIRMATION_TITLE_LABEL);
        Image iconImage = new Image(Objects.requireNonNull(
                ConnectToServerController.class.getResourceAsStream("/assets/clientIcon.png")));
        popupDialog.getIcons().clear();
        popupDialog.getIcons().add(iconImage);
        popupDialog.setScene(new Scene(pane));
        popupDialog.centerOnScreen();
        popupDialog.setMinHeight(ConfirmationOrderPopUpWindowController.POP_UP_HEIGHT);
        popupDialog.setMinWidth(ConfirmationOrderPopUpWindowController.POP_UP_WIDTH);
        popupDialog.setWidth(ConfirmationOrderPopUpWindowController.POP_UP_WIDTH);
        popupDialog.setHeight(ConfirmationOrderPopUpWindowController.POP_UP_HEIGHT);
        popupDialog.show();

        if (UserInstallationController.configuration.equals("EK")) {
            playThanksForBuyingVoice();
        }
    }

    private void playThanksForBuyingVoice() {
        String audioFile = Objects.requireNonNull(getClass().getResource("/assets/sounds/thanks.mp3")).toString();
        Media media = new Media(audioFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.totalDurationProperty().addListener((observable, oldDuration, newDuration) -> {
            if (newDuration.greaterThan(Duration.ONE)) {
                Platform.runLater(mediaPlayer::play);
            }
        });
    }

    private void returnToMainPage() throws IOException {
        BillReplaced = true;
        Parent root;
        Stage primaryStage = StageSingleton.getInstance().getStage();
        if (restoreOrder.getPickUpMethod() == PickUpMethod.delivery || restoreOrder.getPickUpMethod() == PickUpMethod.latePickUp) {
        	DeliveryFormController.scene = null;
        	PickupController.scene = null;
            root = FXMLLoader.load(getClass().getResource("/assets/fxmls/OLMain.fxml"));
        }
        else {
            root = FXMLLoader.load(getClass().getResource("/assets/fxmls/EKMain.fxml"));
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
        primaryStage.setTitle("EKrut Main");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    /**
     * function that handle while back button is clicked. it will open the new order window
     *
     * @param event
     */
    @FXML
    void backBtnClicked(MouseEvent event) {
        replaceWindowToNewOrder();
    }

    private void replaceWindowToNewOrder() {
        BillReplaced = true;
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

    /**
     * function that handle while logout button is clicked. back to the login page and log out the user.
     *
     * @param event
     */
    @FXML
    void logOutClicked(ActionEvent event) throws Exception {
        System.out.println("OOUT");
        BillReplaced = true;
        Util.genricLogOut(getClass());
    }

    /**
     * inner class that handle the timeOut of this current scene. if TIME_OUT_TIME_IN_MINUTES time over wihout activity, the system will logout the user.
     */
    static class TimeOutControllerBillWindow implements Runnable {
        private int TimeOutTime = Utils.TIME_OUT_TIME_IN_MINUTES;//
        private long TimeOutStartTime = System.currentTimeMillis();

        /**
         * run method, running while new thread started with object of this class. measuring
         * time,  logout the user and close the thread if there is no click identified in the scene.
         */
        @Override
        public void run() {
            Platform.runLater(() -> handleAnyClick());

            while (true) {
                long TimeOutCurrentTime = System.currentTimeMillis();
                if (TimeOutCurrentTime - TimeOutStartTime >= TimeOutTime * 60 * 1000) {
                    System.out.println("Time Out passed");
                    try {
                        Platform.runLater(() -> {
                            try {
                                Util.genricLogOut(getClass());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                if (BillReplaced) {
                    System.out.println("Thread closed from " + getClass().toString());
                    return;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        /**
         * method that reset the TimeOutStartTime with any click on the screen.
         */
        public void handleAnyClick() {
            StageSingleton.getInstance().getStage().getScene().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                    System.out.print("Mouse clicked, timeout time reset\n");
                    TimeOutStartTime = System.currentTimeMillis();
                }
            });

        }
    }
}
