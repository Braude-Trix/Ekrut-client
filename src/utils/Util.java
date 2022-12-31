package utils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.ClientUI;
import gui.LoginController;
import gui.StageSingleton;
import gui.LoginController;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.Method;
import models.Request;
import models.ResponseCode;

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
		requestLogOut(getClass);
	}
	
	public static void setNameNavigationBar(Label labelName) {
		labelName.setText(LoginController.user.getFirstName() +" " +LoginController.user.getLastName());
	}
	
	private static void requestLogOut(Class getClass) throws IOException {
    	List<Object> userDetails = new ArrayList<>();
    	userDetails.add(LoginController.user.getId());
    	userDetails.add(false);
    	Request request = new Request();
        request.setPath("/login/setLoggedIn");
        request.setMethod(Method.PUT);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
        case OK:
        	setNewScene(getClass);
            break;
        default:
            break;
        }
	}
		
	private static void setNewScene(Class getClass) throws IOException {
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
		stage.setOnCloseRequest(e -> Exit());

	}
	
    static void Exit() {
		Stage stage = StageSingleton.getInstance().getStage();
        stage.close();
        System.exit(0);
    }
	
    public static void forcedExit() throws IOException {
    	List<Object> userDetails = new ArrayList<>();
    	userDetails.add(LoginController.user.getId());
    	userDetails.add(false);
    	Request request = new Request();
        request.setPath("/login/setLoggedIn");
        request.setMethod(Method.PUT);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);// sending the request to the server.
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }

}
