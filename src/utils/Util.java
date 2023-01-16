package utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import client.Client;
import client.ClientUI;
import gui.LoginController;
import gui.StageSingleton;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.Method;
import models.Request;

/**
 * @author gal
 * This class is intended for code reuse
 */
public class Util {

	/**

	 The variable TIME_OUT_TIME_IN_MINUTES represents the time in minutes until a timeout occurs.
	 The default value is set to 5 minute.
	 */
	public static int TIME_OUT_TIME_IN_MINUTES = 5;

	/**
	 * A method that checks whether spaces or tabs or an empty string has been inserted if so returns true otherwise false
	 * @param string, Description: A string entered by the user
	 * @return, Description: returns true if the string is spaces/tabs/empty otherwise false
	 */
	public static boolean isBlankString(String string) {
		if (string.matches("\\p{IsWhite_Space}*")) {
			return true;
		}
		return false;
	}
	
	/**
	 * Formatting the textfield in red in light of an input error
	 * @param textField, Description: The input textField is invalid
	 */
	public static void setFieldTextErrorBorder(TextField textField) {
		textField.getStyleClass().add("validation-error");
	}
	
	/**
	 * Exit using the logout button and go to the login window
	 * @param getClass, The class that caused the exit from the window
	 */
	public static void genricLogOut(Class getClass)   {
		requestLogOut(getClass);
	}
	
	/**
	 * Initializes the name of the logged in user
	 * @param labelName, Description: label for updating the user's name
	 */
	public static void setNameNavigationBar(Label labelName) {
		labelName.setText(LoginController.user.getFirstName() +" " + LoginController.user.getLastName());
	}
	
	private static void requestLogOut(Class getClass){
    	List<Object> userDetails = new ArrayList<>();
    	userDetails.add(LoginController.user.getId());
    	userDetails.add(false);
    	Request request = new Request();
        request.setPath("/login/setLoggedIn");
        request.setMethod(Method.PUT);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);
        switch (Client.resFromServer.getCode()) {
        case OK:
        	setNewScene(getClass);
            break;
        default:
            break;
        }
	}
		
	private static void setNewScene(Class getClass){
		Stage stage = StageSingleton.getInstance().getStage();
		Parent root;
		try {
			root = FXMLLoader.load(getClass.getResource("/assets/fxmls/login.fxml"));
			Scene scene = new Scene(root);
			scene.getStylesheets().add(getClass.getResource("/styles/loginForm.css").toExternalForm());
			stage.setTitle("Login");
			stage.setScene(scene);
			stage.centerOnScreen();
			stage.setResizable(false);
			stage.show();
			stage.setOnCloseRequest(e -> Exit());
		} catch (IOException e1) {
			e1.printStackTrace();
		}	
	}
	
    /**
     * Closing the system
     */
    static void Exit() {
		Stage stage = StageSingleton.getInstance().getStage();
        stage.close();
        System.exit(0);
    }
	
    /**
     * This method describes a situation in which the user closed the application through X,
     * the user being automatically disconnected as a result
     * @throws IOException, An exception may occur when closing the stage
     */
    public static void forcedExit() throws IOException {
    	List<Object> userDetails = new ArrayList<>();
    	userDetails.add(LoginController.user.getId());
    	userDetails.add(false);
    	Request request = new Request();
        request.setPath("/login/setLoggedIn");
        request.setMethod(Method.PUT);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);
        switch (Client.resFromServer.getCode()) {
        case OK:
            StageSingleton.getInstance().getStage().close();
            System.exit(0);
            break;
        default:
            break;
        }

    }

	/**
	 * A method that checks whether spaces or tabs or an empty string has been inserted if so returns true otherwise false
	 * @param cs, Description: A string entered by the user
	 * @return, boolean: returns true if the string is spaces/tabs/empty otherwise false
	 */
	public static boolean isBlank(CharSequence cs) {
		if (cs == null || cs.length() == 0) {
			return true;
		}
		return cs.chars().allMatch(Character::isWhitespace);
	}
}
