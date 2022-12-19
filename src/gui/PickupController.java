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

public class PickupController implements Initializable {
	public static Scene scene;
    @FXML
    private Label errorLabel;

    @FXML
    private ComboBox<String> regionList;

    @FXML
    private ComboBox<String> machineList;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        setRegionComboBox();
	}
    
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
    
    @FXML
    void Back(MouseEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(OLController.scene);
		stage.show();
    }
    
    private void setRegionComboBox() {
        ObservableList<String> options = FXCollections.observableArrayList("Option 1","Option 2","Option 3");
        regionList.getItems().addAll(options); 
    }


    @FXML
    void selectItem(ActionEvent event) {
        machineList.getItems().clear(); 
	    String selectedItem = regionList.getSelectionModel().getSelectedItem();
	    setMachinesNameComboBox(selectedItem);
    }

    private void setMachinesNameComboBox(String region) {
        ObservableList<String> options = FXCollections.observableArrayList("Option 1","Option 2","Option 3");
        machineList.getItems().addAll(options);

	    machineList.setDisable(false);

    }
    
    @FXML
    void ContinueToOrder(ActionEvent event) {
    	removeErrorStyle();
    	if(!isValidFillComboBoxes()) {
    		return;
    	}

    }
    
    private void removeErrorStyle() {
    	regionList.getStyleClass().remove("validation-error");
    	machineList.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
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
