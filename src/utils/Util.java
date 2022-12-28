package utils;

import java.io.IOException;

import gui.StageSingleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Util {
	public static boolean isBlankString(String string) {
		if (string.matches("\\p{IsWhite_Space}*")) {
			return true;
		}
		return false;
	}
	
	public static void setFieldTextErrorBorder(TextField textField) {
		textField.getStyleClass().add("validation-error");
	}
	
	public static void genricLogOut(Class getClass) throws Exception   {
		Stage stage = StageSingleton.getInstance().getStage();
		Parent root = FXMLLoader.load(getClass.getResource("/assets/login.fxml"));
		
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass.getResource("/styles/loginForm.css").toExternalForm());
		stage.setTitle("Login");
		stage.setScene(scene);
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.show();
		stage.setMinHeight(stage.getHeight());
		stage.setMinWidth(stage.getWidth());
	}

}
