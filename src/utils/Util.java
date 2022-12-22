package utils;

import javafx.scene.control.TextField;

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

}
