package gui;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.Customer;
import models.Worker;
import utils.Util;

public class SelectOptionWorkerOrCustomer implements Initializable {
	public static Scene scene;
	private OLController OLcon;

    @FXML
    private Label labelName;

    @FXML
    private Button logoutBtn;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
        Util.setNameNavigationBar(labelName);
	}
	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/SelectOptionWorkerOrCustomer.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Option For Worker");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
    
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());
    }

    @FXML
    void customerClicked(ActionEvent event) throws Exception {
    	LoginController.user = (Customer) LoginController.customerAndWorker.get(0);
		Stage stage = StageSingleton.getInstance().getStage();
		OLcon = new OLController();	
		OLcon.start(stage);
    }

    @FXML
    void employeeClicked(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
    	LoginController.user = (Worker) LoginController.customerAndWorker.get(1);
		setWindowByTypeWorker(stage, (Worker) LoginController.customerAndWorker.get(1));
    }
    
    private void setWindowByTypeWorker(Stage stage, Worker worker) {
    	switch (worker.getType()) {
    	case CEO:
    		break;
    	case OperationalWorker:
    		break;
    	case MarketingManager:
    		break;
    	case MarketingWorker:
    		break;
    	case RegionalManager:
    		break;
    	case RegionalDelivery:
    		break;
    	case ServiceOperator:
    		break;

    	}
    }

}

