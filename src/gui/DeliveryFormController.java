package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class DeliveryFormController {
	public static Scene scene;

	
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/DeliveryForm.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/deliveryForm.css").toExternalForm());
		primaryStage.setTitle("Delivery Form");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
	
    @FXML
    void LogOut(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(loginController.scene);
		stage.show();
    }
    
    @FXML
    void Back(MouseEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(OLController.scene);
		stage.show();
    }
}
