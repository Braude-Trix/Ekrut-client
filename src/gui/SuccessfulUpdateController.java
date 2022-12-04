package gui;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.Node;

import javafx.scene.control.Button;
import javafx.stage.Stage;

public class SuccessfulUpdateController {

    @FXML
    private Button backBtn;
    
    public void handleBackBtnClicked(ActionEvent event) {
    	Stage stage = (Stage)((Node) event.getSource()).getScene().getWindow();
    	stage.close();
    	}
    

}