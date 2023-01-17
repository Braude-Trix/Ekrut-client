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
import utils.Utils;

/**
 * @author gal
 * This class describes the functionality of the employee window when it is also a client and is in OL configuration
 */
public class SelectOptionWorkerOrCustomer implements Initializable {
	
	/**
	 * This field saves the scene of the window that opens for selecting a customer or employee
	 */
	public static Scene scene;
	
	private OLController OLcon;

    @FXML
    private Label labelName;

    @FXML
    private Button logoutBtn;

	/**
	 * This method initializes the user name on the given page
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        Utils.setNameNavigationBar(labelName);
	}
	
    /**
	 * This method sets a scene to a given stage.
	 * 
	 * @param primaryStage Description: The stage on which the scene is presented
	 * @throws Exception Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/fxmls/SelectOptionWorkerOrCustomer.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Option For Worker");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
			try {
				Utils.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
    
	/**
	 * This method navigates the client to the login page and logging him out. This
	 * method runs when the user clicked LogOut.
	 * 
	 * @param event Description: the current event when the click happened.
	 * @throws Exception Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Utils.genericLogOut(getClass());
    }


    /**
     * This method describes the user's choice to log in as a customer and move to the desired window
     * @param event Description: Clicking on the "Customer" button
     */
    @FXML
    void customerClicked(ActionEvent event){
    	LoginController.user = (Customer) LoginController.customerAndWorker.get(0);
		Stage stage = StageSingleton.getInstance().getStage();
		OLcon = new OLController();	
		try {
			OLcon.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }


    /**
     * This method describes the user's choice to log in as a worker and move to the desired window by type of worker
     * @param event Description: Clicking on the "Employee" button
     */
    @FXML
    void employeeClicked(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
    	LoginController.user = (Worker) LoginController.customerAndWorker.get(1);
		try {
			LoginController.setWindowByTypeWorker(stage, (Worker) LoginController.customerAndWorker.get(1));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
}

