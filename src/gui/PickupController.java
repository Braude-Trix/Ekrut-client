package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.*;
import utils.Util;

/**
 * @author gal
 * This class describes the functionality of the self-pickup.
 * Selecting an region and machine for pickup
 */
public class PickupController implements Initializable {
	/**
	 * This field saves the scene of the window that opens for open back this window from others windows
	 */
	public static Scene scene = null;

	public static Regions regionForMach = null;

	@FXML
	private Label errorLabel;

	@FXML
	private ComboBox<Regions> regionList;

	@FXML
	private ComboBox<String> machineList;

	private List<Machine> machinesSet;

    @FXML
    private Label labelName;
	/**
	 * This method describes the initialization of information that will be
	 * displayed in the window depending on the client.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		setRegionComboBox();
		machinesSet = new ArrayList<>();
		scene = null;
		regionForMach = null;
        Util.setNameNavigationBar(labelName);
	}

	/**
	 * This method describes setting up a new scene.
	 * 
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/fxmls/PickUpForm.fxml"));

		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/PickupForm.css").toExternalForm());
		primaryStage.setTitle("Pickup Form");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

	/**
	 * This method describes what happens after clicking the logout button. Clicking
	 * this button will lead to the login screen.
	 * 
	 * @param event, Description: Event - clicking the Logout button
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	@FXML
	void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());
	}

	/**
	 * This method returns the client to the previous window 
	 * (Main screen of the client in OL configuration)
	 * @param event, Description: Event - clicking the Back button
	 */
	@FXML
	void Back(MouseEvent event){
		Stage primaryStage = StageSingleton.getInstance().getStage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass().getResource("/assets/fxmls/OLMain.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
			primaryStage.setTitle("EKrut Main");
			primaryStage.setScene(scene);
			primaryStage.centerOnScreen();
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void setRegionComboBox() {
		ObservableList<Regions> options = FXCollections.observableArrayList(Regions.class.getEnumConstants());
		regionList.getItems().addAll(options);
		regionList.getItems().remove(Regions.All);
	}

	/**
	 * This method describes an event of a selection from a combo box of the regions
	 * and the definition of the machines included in it within the combo box
	 * 
	 * @param event, Description: Region selection event from a combo box.
	 */
	@FXML
	void selectItem(ActionEvent event) {
		machineList.getItems().clear();
		Regions selectedItem = regionList.getSelectionModel().getSelectedItem();
		setMachinesNameComboBox(selectedItem);
	}

	private void setMachinesNameComboBox(Regions region) {
		List<Object> regionReq = new ArrayList<>();
		regionReq.add(region);
		Request request = new Request();
		request.setPath("/machines/getMachine");
		request.setMethod(Method.GET);
		request.setBody(regionReq);
		ClientUI.chat.accept(request);

		handleResponseGetMachines();
	}

	private void handleResponseGetMachines() {
		errorLabel.setText("");
		switch (Client.resFromServer.getCode()) {
		case OK:
			updateMachines((Client.resFromServer.getBody()));
			break;
		default:
			errorLabel.setText((Client.resFromServer.getDescription()));
			break;
		}
	}

	private void updateMachines(List<Object> listMachine) {
		machinesSet.clear();
		if (listMachine == null) {
			machineList.setDisable(true);
			errorLabel.setText("There are no machines in this region at the moment");
			return;
		}
		ObservableList<String> options = FXCollections.observableArrayList();
		for (Object machine : listMachine) {
			if (machine instanceof Machine) {
				Machine tempMachine = (Machine) machine;
				machinesSet.add(tempMachine);
				options.add(tempMachine.getName());
			}
		}
		machineList.getItems().addAll(options);
		machineList.setDisable(false);
	}

	/**
	 * This method describes the event of clicking on the "Continue" button that
	 * goes to the window that allows you to start choosing products to order. Only
	 * if all the details on the page have been entered correctly.
	 * 
	 * @param event, Description: Event - clicking the Continue button
	 */
	@FXML
	void ContinueToOrder(ActionEvent event) {
		removeErrorStyle();
		if (!isValidFillComboBoxes()) {
			return;
		}

		LoginController.order = new PickupOrder(null, null, 0, getMachineId(), OrderStatus.NotCollected, PickUpMethod.latePickUp, null,
				LoginController.user.getId(), null);
		try {
			regionForMach = regionList.getValue();
			new NewOrderController().start(StageSingleton.getInstance().getStage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getMachineId() {
		for (int i = 0; i < machinesSet.size(); i++) {
			if (machineList.getValue().equals(machinesSet.get(i).getName())) {
				return machinesSet.get(i).getId();
			}
		}
		return null;
	}

	private void removeErrorStyle() {
		regionList.getStyleClass().remove("validation-error");
		machineList.getStyleClass().remove("validation-error");
		errorLabel.setText("");
	}

	private boolean isValidFillComboBoxes() {
		if (regionList.getValue() == null) {
			regionList.getStyleClass().add("validation-error");
			machineList.getStyleClass().add("validation-error");
			errorLabel.setText("Please select region and machine");
			return false;
		} else if (machineList.getValue() == null) {
			machineList.getStyleClass().add("validation-error");
			errorLabel.setText("Please select machine");
			return false;
		}
		return true;
	}


}
