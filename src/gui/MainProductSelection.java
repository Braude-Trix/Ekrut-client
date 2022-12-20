package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainProductSelection extends Application  {
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// This class manages the scene and presents
		AnchorPane anchorPane;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource("NewOrder.fxml"));
			anchorPane = loader.load(); // Management of the vbox by fxml

			Scene scene = new Scene(anchorPane); // Set up a new scene using vbox obtained in fxml
			primaryStage.setScene(scene);
			primaryStage.setTitle("Voting Machine");
			primaryStage.show();
			primaryStage.setMinHeight(primaryStage.getHeight());
			primaryStage.setMinWidth(primaryStage.getWidth());
			
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}
}





