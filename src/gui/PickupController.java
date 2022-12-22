package gui;

import java.net.URL;
import java.util.ResourceBundle;

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

/**
 * @author gal
 * This class describes the functionality of the self-pickup way selection window.
 */
public class PickupController implements Initializable {
	public static Scene scene;
    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> regionList;

    @FXML
    private ComboBox<String> machineList;
    
    
	/**
	 *This method describes the initialization of information that will be displayed in the window depending on the client.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        setRegionComboBox();
	}
    
	/**
	 * This method describes setting up a new scene.
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
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
	}
	
    /**
     * This method describes what happens after clicking the logout button.
     * Clicking this button will lead to the login screen.
     * @param event, Description: Event - clicking the Logout button
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Stage stage = StageSingleton.getInstance().getStage();
		Parent root = FXMLLoader.load(getClass().getResource("/assets/login.fxml"));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
		stage.setMinHeight(stage.getHeight());
		stage.setMinWidth(stage.getWidth());
    }
    
    /**
     * This method returns the client to the previous window.
     * @param event, Description: Event - clicking the Back button
     */
    @FXML
    void Back(MouseEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(OLController.scene);
		stage.show();
    }
    
    /**
     * Defines the regions that exist within the displayed combo box.
     */
    private void setRegionComboBox() {
        ObservableList<String> options = FXCollections.observableArrayList("Option 1","Option 2","Option 3");
        regionList.getItems().addAll(options); 
    }


    /**
     * This method describes an event of a selection from a combo box of the regions
     *  and the definition of the machines included in it within the combo box
     * @param event, Description: Region selection event from a combo box.
     */
    @FXML
    void selectItem(ActionEvent event) {
        machineList.getItems().clear(); 
	    String selectedItem = regionList.getSelectionModel().getSelectedItem();
	    setMachinesNameComboBox(selectedItem);
    }

    /**
     * Defines the machines are in the same region into a combo box.
     * @param region, Describes the selected parameter in the combo box
     */
    private void setMachinesNameComboBox(String region) {
        ObservableList<String> options = FXCollections.observableArrayList("Option 1","Option 2","Option 3");
        machineList.getItems().addAll(options);
    	machineList.setDisable(false);
    }
    
    /**
     * This method describes the event of clicking on the "Continue" button that goes to the window that allows you to start choosing products to order.
     * Only if all the details on the page have been entered correctly.
     * @param event, Description: Event - clicking the Continue button
     */
    @FXML
    void ContinueToOrder(ActionEvent event) {
    	removeErrorStyle();
    	if(!isValidFillComboBoxes()) {
    		return;
    	}

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
     * If it was not filled in properly, design the place where the choice is missing in red. 
     * And there is an error message.
     * @return, Description: If the combo box was not filled in properly, it returns false. Returns true if everything is fine.
     */
    private boolean isValidFillComboBoxes() {
    	if(regionList.getValue() == null) {
    		regionList.getStyleClass().add("validation-error");
    		machineList.getStyleClass().add("validation-error");
    		errorLabel.setText("Please select region and machine");
    		return false;
    	}
    	else if (machineList.getValue() == null) {
    		machineList.getStyleClass().add("validation-error");
    		errorLabel.setText("Please select machine");
    		return false;
    	}
    	return true;
    }
}
