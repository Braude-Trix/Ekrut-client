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
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.*;
import utils.Util;

/**
 * @author gal This class describes the functionality of the self-pickup way
 *         selection window.
 */
public class PickupController implements Initializable {
	public static Scene scene = null;

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
		Parent root = FXMLLoader.load(getClass().getResource("/assets/PickUpForm.fxml"));

		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/PickUpForm.css").toExternalForm());
		primaryStage.setTitle("Pickup Form");
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
	 * This method returns the client to the previous window.
	 * 
	 * @param event, Description: Event - clicking the Back button
	 * @throws IOException 
	 */
	@FXML
	void Back(MouseEvent event) throws IOException {
		Stage primaryStage = StageSingleton.getInstance().getStage();
		Parent root = FXMLLoader.load(getClass().getResource("/assets/OLMain.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Main");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}

	/**
	 * Defines the regions that exist within the displayed combo box.
	 */
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

	/**
	 * Defines the machines are in the same region into a combo box.
	 * 
	 * @param region, Describes the selected parameter in the combo box
	 */
	private void setMachinesNameComboBox(Regions region) {
		List<Object> regionReq = new ArrayList<>();
		regionReq.add(region);
		Request request = new Request();
		request.setPath("/machines/getMachine");
		request.setMethod(Method.GET);
		request.setBody(regionReq);
		ClientUI.chat.accept(request);// sending the request to the server.

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
		continueNewOrder();
	}

	private String getMachineId() {
		for (int i = 0; i < machinesSet.size(); i++) {
			if (machineList.getValue().equals(machinesSet.get(i).getName())) {
				return machinesSet.get(i).getId();
			}
		}
		return null;
	}

	/**
	 * This method removes error formatting for normal outputs.
	 */
	private void removeErrorStyle() {
		regionList.getStyleClass().remove("validation-error");
		machineList.getStyleClass().remove("validation-error");
		errorLabel.setText("");
	}

	/**
	 * This method checks if the client has selected a region and machine correctly.
	 * If it was not filled in properly, design the place where the choice is
	 * missing in red. And there is an error message. @return, Description: If the
	 * combo box was not filled in properly, it returns false. Returns true if
	 * everything is fine.
	 */
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

	private void continueNewOrder() {
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
		stage.setTitle(StyleConstants.STAGE_LABEL);
		stage.setScene(new Scene(pane));
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
	}
}
