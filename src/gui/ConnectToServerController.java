package gui;

import client.ClientConfiguration;
import client.ClientController;
import client.ClientUI;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class ConnectToServerController {
	@FXML
	private TextField txtHost;

	@FXML
	private TextField txtPort;

	@FXML
	private Button connectBtn;

	@FXML
	private Label ErrorLabel;

	@FXML
	private Label ErrorHelpLabel;

	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/gui/ConnectToServer.fxml"));
		Scene scene = new Scene(root);
		primaryStage.setTitle("Connect To Server");
		primaryStage.setScene(scene);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());

	}

	@FXML
	void connectToServer(ActionEvent event) throws Exception {

		if (!ValidatingTextField(txtPort,"port"))
			return;
		ClientConfiguration clientConfig = new ClientConfiguration(txtHost.getText(),
				Integer.parseInt(txtPort.getText()));
		try {
			ClientUI.chat = new ClientController(clientConfig.getHost(), clientConfig.getPort());
		} catch (Exception e) {
			HandleConnectionError();
			return;
		}

		Stage stage = StageSingleton.getInstance().getStage();
		Parent root = FXMLLoader.load(getClass().getResource("/gui/MainSubscruibersPanel.fxml"));
		stage.setTitle("Subscribers Panel");
		stage.setScene(new Scene(root));
		stage.show();

	}

	private void HandleConnectionError() {

		ErrorLabel.setText("Error: can't connect to the server");
		ErrorHelpLabel.setText("Please check IP and PORT");

	}

	private boolean ValidatingTextField(TextField textField, String errorSubject) {
		String str = textField.getText();

		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				ErrorLabel.setText("Error: "+errorSubject+" contains only numbers..");
				ErrorHelpLabel.setText("Please try again.");
				textField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
				return false;
			}
		}

		ErrorLabel.setText("");
		ErrorHelpLabel.setText("");
		textField.setStyle("-fx-text-box-border: #6e6b6b; -fx-focus-color: #6e6b6b;");
		return true;
	}

}