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
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Machine;
import models.Method;
import models.Regions;
import models.Request;


/**
 * This class describes the process of installing the system with all the details required for its installation
 * Choosing a configuration, and if EK is chosen, choosing a desired machine
 * @author gal
 */
public class UserInstallationController implements Initializable {
	/**
	 * This field describes the machine on which the system was updated if we are in the EK configuration
	 */
	public static Machine machine = null;
	
	/**
	 * Describes the installation configuration on the system (EK or OL)
	 */
	public static String configuration = null;

    @FXML
    private Label title;
    
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

    
	/**
	 * This method initializes data before the screen comes up
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		machinesSet = new ArrayList<>();
		machine = null;
	}
	
	
    /**
	 * This method sets a scene to a given stage.
	 * 
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		
		Parent root = FXMLLoader.load(getClass().getResource("/assets/fxmls/UserInstallation.fxml"));

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/userInstallation.css").toExternalForm());
		primaryStage.setTitle("Installation Window");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
        primaryStage.setOnCloseRequest(e -> forcedExit());
	}
	
    private static void forcedExit() {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }
	
    /**
     * Clicking on this button describes choosing the EK installation configuration 
     * and therefore requires choosing a machine on which the system will work.
     * @param event, Description: Clicking on the EK configuration button
     */
    @FXML
    void EkClicked(ActionEvent event) {
    	HboxConfiguration.setVisible(false);
    	title.setText("Select a region and a machine in the \"EK\" configuration:");
    	title.setFont(new Font(24));
    	setRegionComboBox();
    	VboxAfterClickedEk.setVisible(true);
    	configuration = "EK";
    	    	
    }
    
	private void setRegionComboBox() {
		ObservableList<Regions> options = FXCollections.observableArrayList(Regions.class.getEnumConstants());
		regionComboBoxId.getItems().addAll(options);
		regionComboBoxId.getItems().remove(Regions.All);
	}

    /**
     * This method click describes the OL configuration. And beyond the appropriate login window.
     * @param event, Description: Clicking on the OL configuration button
     */
    @FXML
    void OLClicked(ActionEvent event){
    	configuration = "OL";
    	LoginController LoginCon = new LoginController();
		Stage stage = StageSingleton.getInstance().getStage();
		try {
			LoginCon.start(stage);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    /**
     * This method describes clicking on a region value in the combobox 
     * and changing the machines that are in the combobox of machines according to the selected region.
     * @param event, Description: Clicking on a value in the region combobox.
     */
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
	

    /**
     * This method describes a final installation in EK configuration after selecting a suitable machine and region.
     * @param event, Description: The install button was pressed.
     */
    @FXML
    void installClicked(ActionEvent event){
		removeErrorStyle();
		if (!isValidFillComboBoxes()) {
			return;
		}
		
		machine = getMachine();
		LoginController LoginCon = new LoginController();
		Stage stage = StageSingleton.getInstance().getStage();
		try {
			LoginCon.start(stage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
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
