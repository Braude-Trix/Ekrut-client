package gui.workers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import utils.WorkerNodesUtils;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Gui controller for presenting CEO window
 */
public class CeoGui implements Initializable {

    public static CeoGui controller;
    public static String userName = "Richard"; // todo: replace this with db
    public static WorkerType workerType;

    @FXML
    ImageView bgImage;

    @FXML
    VBox bottomBroderVbox;

    @FXML
    VBox centerBroderVbox;

    @FXML
    Button deliveryOperatorsBtn;

    @FXML
    Label userNameLabel;

    @FXML
    Button logoutBtn;

    @FXML
    Button managersBtn;

    @FXML
    Button marketingWorkersBtn;

    @FXML
    Button operationalWorkersBtn;

    @FXML
    Button serviceOperatorsBtn;

    @FXML
    VBox topBorderVBox;

    /**
     * Initializing CEO window
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setting username
        WorkerNodesUtils.setUserName(userNameLabel, userName);

        managersBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            managersBtn.setDisable(true);
            new CeoSelectManagers().loadMyManagers();
        });

        marketingWorkersBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            marketingWorkersBtn.setDisable(true);
            workerType = WorkerType.MARKETING;
            new CeoSelectWorker().loadMyMarketWorkers();
        });

        operationalWorkersBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            operationalWorkersBtn.setDisable(true);
            workerType = WorkerType.OPERATION;
            new CeoSelectWorker().loadMyMarketWorkers();
        });

        serviceOperatorsBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            serviceOperatorsBtn.setDisable(true);
            workerType = WorkerType.SERVICE;
            new CeoSelectWorker().loadMyMarketWorkers();
        });

        deliveryOperatorsBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            deliveryOperatorsBtn.setDisable(true);
            workerType = WorkerType.DELIVERY;
            new CeoSelectWorker().loadMyMarketWorkers();
        });

        logoutBtn.setOnMouseClicked((event) -> System.out.println(event.getSource().toString()));
    }

    private void enableAll() {
        managersBtn.setDisable(false);
        marketingWorkersBtn.setDisable(false);
        operationalWorkersBtn.setDisable(false);
        operationalWorkersBtn.setDisable(false);
        serviceOperatorsBtn.setDisable(false);
        deliveryOperatorsBtn.setDisable(false);
    }

    private void clearBorderPane() {
        topBorderVBox.getChildren().clear();
        centerBroderVbox.getChildren().clear();
        bottomBroderVbox.getChildren().clear();
    }

    public enum WorkerType {
        DELIVERY, SERVICE, MARKETING, OPERATION;

        public boolean hasRegion() {
            return (this == DELIVERY) || (this == MARKETING);
        }
    }
}
