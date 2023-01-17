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
import models.*;
import utils.StyleConstants;
import utils.StylePaths;
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
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    public void initialize(URL location, ResourceBundle resources) {

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
    	boolean isOk = requestSaveOrder();
		if (!isOk)
			return;
		NewOrderController.aliveSale = false;
        try {
            returnToMainPage();
        } catch (IOException e) {
        }
        changeToConfirmationOrderPopUpWindow();
    }

    private boolean requestSaveOrder() {
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
        NewOrderController.previousOrder.setMachineId(machineId.toString());
        NewOrderController.previousOrder.setCustomerId(customerId);
        orderList.add(NewOrderController.previousOrder);
        request.setBody(orderList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                break;
            case INVALID_DATA:
            	NewOrderController.aliveSale = false;
            	restoreOrder = null;
            	
            	LoginController.order.getProductsInOrder().clear();
            	LoginController.order.setPrice(0.0);
            	
                replaceWindowToNewOrder();
                createAnAlert(Alert.AlertType.ERROR, StyleConstants.OUT_OF_STOCK_LABEL, StyleConstants.INVENTORY_UPDATE_ALERT_MSG);
                return false;
            default:
                System.out.println(Client.resFromServer.getDescription());
        }
        return true;
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
        BillReplaced = true;
        Utils.genericLogOut(getClass());
    }

    /**
     * A class that implements a runnable task for detecting and handling a time out event.
     * The time out event occurs when the elapsed time since the time out start time exceeds a specified time out time.
     */
    static class TimeOutControllerBillWindow implements Runnable {
        private int TimeOutTime = Utils.TIME_OUT_TIME_IN_MINUTES;
        private long TimeOutStartTime = System.currentTimeMillis();

        /**
         * Detects and handles a time out event.
         * This task is executed every 10 seconds until the thread is interrupted or the `EKPageReplace` flag is set to `true`.
         * If a time out event occurs, the log out process is initiated.
         */

        @Override
        public void run() {
            Platform.runLater(()->handleAnyClick());
            while (!Thread.currentThread().isInterrupted()) {
                long TimeOutCurrentTime = System.currentTimeMillis();
                if (TimeOutCurrentTime - TimeOutStartTime >= TimeOutTime * 60 * 1000) {
                    System.out.println("Time Out passed");
                    try {
                        Platform.runLater(()-> {
                            try {
                                Utils.genericLogOut(getClass());
                            } catch (Exception e) {
                            }
                        });
                    } catch (Exception e) {
                    }
                    return;
                }
                if(BillReplaced) {
                    System.out.println("Thread closed from TimeOut Controller");
                    return;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void handleAnyClick() {
            StageSingleton.getInstance().getStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<javafx.scene.input.MouseEvent>(){
                @Override
                public void handle(MouseEvent mouseEvent) {
                    System.out.print("Mouse clicked, timeout time reset\n");
                    TimeOutStartTime = System.currentTimeMillis();
                }
            });
        }
    }


}
