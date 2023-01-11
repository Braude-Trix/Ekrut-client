package gui;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import static org.mockito.Matchers.any;

import utils.IUtil;

class LoginControllerTest {

	private LoginController loginCon;
	private boolean conditionPassword, conditionUsername;
	private String stringUsername;
	private UserInstallationController install;
	private TextField txt;
	public class UtilTest implements IUtil {
	    public boolean isBlankString(String s) {
	        return s.equals(stringUsername)? conditionUsername:conditionPassword;
	    }
	}
	
	private UtilTest utilTest;
	
	@BeforeEach
	void setUp() throws Exception {
		FXMLLoader loader = FXMLLoader.load(getClass().getResource("/src/assets/fxmls/login.fxml"));
	    Parent root = loader.load();
	    loginCon = loader.getController();
		install = new UserInstallationController();
		install.configuration = "EK";
//		utilTest = new UtilTest();
//		loginCon = new LoginController(utilTest);
//		txt = new TextField("");
		
	}

	@Test
	void test() throws Exception {
		conditionUsername = true;
		conditionPassword = false;
		stringUsername = "";
		loginCon.setTxtUsername(txt);
		loginCon.getTxtUsername().setText("");
		loginCon.getTxtPassword().setText("1234");
		loginCon.login(new ActionEvent());
		String expected="The username is incorrect";
		String result=loginCon.getErrorLabel().getText();
		assertEquals(expected, result);
		

	}

}
