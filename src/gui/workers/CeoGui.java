package gui.workers;

import gui.LoginController;
import gui.SelectOptionWorkerOrCustomer;
import gui.StageSingleton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Worker;
import models.WorkerType;
import utils.Utils;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static utils.Utils.forcedExit;

/**
 * Gui controller for presenting CEO window
 */
public class CeoGui implements Initializable {
    public static CeoGui controller;
    private Worker worker = (Worker) LoginController.user;
    public static WorkerType chosenWorkerType = models.WorkerType.CEO;

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
    @FXML
	 private Button backBtn;

    @FXML
    private Label userRoleLabel;

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
        WorkerNodesUtils.setUserName(userNameLabel, worker);
        WorkerNodesUtils.setRole(userRoleLabel, worker.getType());

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
            chosenWorkerType = WorkerType.MarketingWorker;
            new CeoSelectWorker().loadMyWorkers();
        });

        operationalWorkersBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            operationalWorkersBtn.setDisable(true);
            chosenWorkerType = WorkerType.OperationalWorker;
            new CeoSelectWorker().loadMyWorkers();
        });

        serviceOperatorsBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            serviceOperatorsBtn.setDisable(true);
            chosenWorkerType = WorkerType.ServiceOperator;
            new CeoSelectWorker().loadMyWorkers();
        });

        deliveryOperatorsBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            deliveryOperatorsBtn.setDisable(true);
            chosenWorkerType = WorkerType.RegionalDelivery;
            new CeoSelectWorker().loadMyWorkers();
        });

        logoutBtn.setOnMouseClicked((event) -> {
        	RegionalManagerGui.isCEOLogged = false;
        	MarketingManagerController.isCEOLogged = false;
        	
        	OperationalWorkerGui.isCEOLogged = false;
        	RegionalDeliveryController.isCEOLogged = false;
        	MarketingWorkerWindowController.isCEOLogged = false;
        	ServiceOperatorController.isCEOLogged = false;
        	try {            	
                Utils.genericLogOut(getClass());
            } catch (Exception e) {
            }
        });
		setBackBtnIfExists();
    }

    private void setBackBtnIfExists() {
		if (LoginController.customerAndWorker != null) {
			backBtn.setVisible(true);
		}

	}

    /**
	 * This method is the actual back button, shoots onAction and changes the stage to be the selection page.
	 * @param event - the current event when clicking.
	 */
	@FXML
	void back(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(SelectOptionWorkerOrCustomer.scene);
		stage.show();
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

    /**
     * function that start the fxml of the current window
     * @param primaryStage - Singleton in our program
     */
    public void start(Stage primaryStage) {
        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/workers/fxmls/CeoHomePage_Default.fxml"));
            anchorPane = loader.load();
            CeoGui.controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(anchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("CEO"); // set window title
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            try {
                forcedExit();
            } catch (IOException ex) {
            }
        });
    }
}
