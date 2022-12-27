package gui;

import gui.client.Client;
import gui.client.ClientUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.*;

import javax.swing.text.Style;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConfirmationOrderPopUpWindowController implements Initializable {
    final static int POP_UP_WIDTH = 600;
    final static int POP_UP_HEIGHT = 340;


    @FXML
    private Label thankLabel;
    @FXML
    private Label deliveryAddressLabel;

    @FXML
    private Label monthlyBillLabel;

    @FXML
    private Label orderDateLabel;

    @FXML
    private Label pickUpCodeLabel;

    @FXML
    private Label pickUpMachineLabel;

    @FXML
    private Label pickUpMethodLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private VBox vboxContainer;

    @FXML
    private Region regionId;

    private String deliveryAddress;
    private String choosenMachine;

    private String pinCode = "";

    private static Order order = BillWindowController.restoreOrder;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        initVars();
        initWindow();
    }

    public void initVars(){
        if (order.getPickUpMethod() == PickUpMethod.delivery){
            deliveryAddress = ((DeliveryOrder)order).getFullAddress();
        }
        if (order.getPickUpMethod() == PickUpMethod.latePickUp){
            pinCode = ((PickupOrder)order).getPickupCode();
            choosenMachine = getMachineNameById();
        }
    }
    public String getMachineNameById(){
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/getMachineName");
        request.setMethod(Method.GET);
        paramList.add(order.getMachineId());
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
    public void initWindow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(StyleConstants.DATE_FORMAT);
        String date = LocalDate.now().format(formatter);
        boolean isSubscriber = false;
        PickUpMethod pickUpMethod = order.getPickUpMethod();
        monthlyBillLabel.setVisible(true);
        totalPriceLabel.setText(totalPriceLabel.getText() + order.getPrice());
        orderDateLabel.setText(orderDateLabel.getText() + " " + date);
        if (isSubscriber) {
            thankLabel.setText(StyleConstants.SUBSCRIBER_MSG);
            monthlyBillLabel.setText(monthlyBillLabel.getText() + "300₪");
        } else {
            vboxContainer.getChildren().remove(monthlyBillLabel);
            thankLabel.setText(StyleConstants.CUSTOMER_MSG);
        }
        if (pickUpMethod == PickUpMethod.delivery) {
            vboxContainer.getChildren().remove(pickUpCodeLabel);
            vboxContainer.getChildren().remove(pickUpMachineLabel);

            pickUpMethodLabel.setText(pickUpMethodLabel.getText() + StyleConstants.DELIVERY);
            deliveryAddressLabel.setVisible(true);
            deliveryAddressLabel.setText(deliveryAddressLabel.getText() + deliveryAddress);
        } else if (pickUpMethod == PickUpMethod.latePickUp) {
            pickUpMethodLabel.setText(pickUpMethodLabel.getText() + StyleConstants.LATE_PICKUP);

            vboxContainer.getChildren().remove(deliveryAddressLabel);
            pickUpMachineLabel.setText(pickUpMachineLabel.getText() + choosenMachine);
            pickUpCodeLabel.setText(pickUpCodeLabel.getText() + pinCode);

        } else {
            vboxContainer.getChildren().remove(regionId);
            pickUpMethodLabel.setText(pickUpMethodLabel.getText() + StyleConstants.SELF_PICKUP);
            vboxContainer.getChildren().remove(pickUpCodeLabel);
            vboxContainer.getChildren().remove(pickUpMachineLabel);
            vboxContainer.getChildren().remove(deliveryAddressLabel);
        }
    }

    public void getMonthlyBill(){

    }

    public void setMonthlyBillLabel(){

    }
}
