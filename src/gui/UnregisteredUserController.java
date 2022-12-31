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
import utils.Util;

public class UnregisteredUserController implements Initializable{

    @FXML
    private Label labelName;

    @FXML
    private Button logoutBtn;
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        Util.setNameNavigationBar(labelName);
	}
	

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/UnregisteredUser.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Unregistered User");
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

}