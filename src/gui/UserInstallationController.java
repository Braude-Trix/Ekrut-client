package gui;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Machine;
import models.Method;
import models.Regions;
import models.Request;


public class UserInstallationController implements Initializable {
	public static LoginController LoginCon;
	public static Machine machine = null;
	public static String configuration = null;

    @FXML
    private VBox VboxAfterClickedEk;
    
    @FXML
    private HBox HboxConfiguration;
    
    @FXML
    private ComboBox<Regions> regionComboBoxId;
    
    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> machineComboboxId;
    
	private List<Machine> machinesSet;
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		machinesSet = new ArrayList<>();
		machine = null;
	}
	
	
	public void start(Stage primaryStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("/assets/UserInstallation.fxml"));
				
		Scene scene = new Scene(root);
//		scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
		primaryStage.setTitle("Install Window");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setOnCloseRequest(e -> forcedExit());
	}
	
    private static void forcedExit() {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }
	
    @FXML
    void EkClicked(ActionEvent event) {
    	HboxConfiguration.setVisible(false);
    	setRegionComboBox();
    	VboxAfterClickedEk.setVisible(true);
    	configuration = "EK";
    	    	
    }
    
	private void setRegionComboBox() {
		ObservableList<Regions> options = FXCollections.observableArrayList(Regions.class.getEnumConstants());
		regionComboBoxId.getItems().addAll(options);
		regionComboBoxId.getItems().remove(Regions.All);
	}

    @FXML
    void OLClicked(ActionEvent event) throws Exception {
    	configuration = "OL";
		LoginCon = new LoginController();
		Stage stage = StageSingleton.getInstance().getStage();
		LoginCon.start(stage);
    }

    @FXML
    void RegionComboBoxClicked(ActionEvent event) {
    	machineComboboxId.getItems().clear();
		Regions selectedItem = regionComboBoxId.getSelectionModel().getSelectedItem();
		setMachinesNameComboBox(selectedItem);
    }
    
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
			machineComboboxId.setDisable(true);
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
		machineComboboxId.getItems().addAll(options);
		machineComboboxId.setDisable(false);
	}
	
    @FXML
    void installClicked(ActionEvent event) throws Exception {
		removeErrorStyle();
		if (!isValidFillComboBoxes()) {
			return;
		}
		
		machine = getMachine();
		LoginCon = new LoginController();
		Stage stage = StageSingleton.getInstance().getStage();
		LoginCon.start(stage);		
    }
    
	private boolean isValidFillComboBoxes() {
		if (regionComboBoxId.getValue() == null) {
			regionComboBoxId.getStyleClass().add("validation-error");
			machineComboboxId.getStyleClass().add("validation-error");
			errorLabel.setText("Please select region and machine");
			return false;
		} else if (machineComboboxId.getValue() == null) {
			machineComboboxId.getStyleClass().add("validation-error");
			errorLabel.setText("Please select machine");
			return false;
		}
		return true;
	}

	private void removeErrorStyle() {
		regionComboBoxId.getStyleClass().remove("validation-error");
		machineComboboxId.getStyleClass().remove("validation-error");
		errorLabel.setText("");
	}
	
	private Machine getMachine() {
		for (int i = 0; i < machinesSet.size(); i++) {
			if (machineComboboxId.getValue().equals(machinesSet.get(i).getName())) {
				return machinesSet.get(i);
			}
		}
		return null;
	}

}
