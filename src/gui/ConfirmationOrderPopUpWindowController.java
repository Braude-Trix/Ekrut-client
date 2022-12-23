package gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import models.Order;
import models.PickUpMethod;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ConfirmationOrderPopUpWindowController implements Initializable {
    final static int POP_UP_WIDTH = 600;
    final static int POP_UP_HEIGHT = 340;

    final String SUBSCRIBER_MSG = "Dear subscriber, your bill has been added to the monthly bill, thank you for shopping with us";
    final String CUSTOMER_MSG = "Dear customer, The credit card you provided was successfully charged, thank you for shopping with us";
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

    private static Order order = BillWindowController.restoreOrder;

    public void initialize(URL url, ResourceBundle resourceBundle) {
        initWindow();
    }

    public void initWindow() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String date = LocalDate.now().format(formatter);
        boolean isSubscriber = false;
        PickUpMethod pickUpMethod = order.getPickUpMethod();
        String deliveryAddress = "NESHER";
        String choosenMachine = "L Building";
        monthlyBillLabel.setVisible(true);
        totalPriceLabel.setText(totalPriceLabel.getText() + order.getPrice());
        orderDateLabel.setText(orderDateLabel.getText() + " " + date);
        if (isSubscriber) {
            thankLabel.setText(SUBSCRIBER_MSG);
            monthlyBillLabel.setText(monthlyBillLabel.getText() + "300₪");
        } else {
            vboxContainer.getChildren().remove(monthlyBillLabel);
            thankLabel.setText(CUSTOMER_MSG);
        }
        if (pickUpMethod == PickUpMethod.delivery) {
            vboxContainer.getChildren().remove(pickUpCodeLabel);
            vboxContainer.getChildren().remove(pickUpMachineLabel);

            pickUpMethodLabel.setText(pickUpMethodLabel.getText() + "Delivery");
            deliveryAddressLabel.setVisible(true);
            deliveryAddressLabel.setText(deliveryAddressLabel.getText() + deliveryAddress);
        } else if (pickUpMethod == PickUpMethod.latePickUp) {
            pickUpMethodLabel.setText(pickUpMethodLabel.getText() + "Late pick-up");

            vboxContainer.getChildren().remove(deliveryAddressLabel);
            pickUpMachineLabel.setText(pickUpMachineLabel.getText() + choosenMachine);
            pickUpCodeLabel.setText(pickUpCodeLabel.getText() + "423884232");
        } else {
            vboxContainer.getChildren().remove(regionId);
            pickUpMethodLabel.setText(pickUpMethodLabel.getText() + "Self pick-up");
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
