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

/**
 * @author gal
 * This class describes the opening of a window for an unregistered user and the allowed actions
 */
public class UnregisteredUserController implements Initializable{

    @FXML
    private Label labelName;

    @FXML
    private Button logoutBtn;
    
	/**
	 * This method initializes the user name on the given page
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        Util.setNameNavigationBar(labelName);
	}
	

    /**
	 * This method sets a scene to a given stage.
	 * 
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/fxmls/UnregisteredUser.fxml"));
				
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Unregistered User");
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
	 * This method navigates the client to the login page and logging him out. This
	 * method runs when the user clicked LogOut.
	 * 
	 * @param event, Description: the current event when the click happened.
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
    @FXML
    void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());
    }

}