package gui;

import client.ClientController;
import client.ClientUI;
import clientModels.ClientConfiguration;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/*
 * This class describes all the functionality in the first screen - the process of connecting to the server
 * @author gal
 */
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

    /**
	 * This method sets a scene to a given stage.
	 * 
	 * @param primaryStage Description: The stage on which the scene is presented
	 * @throws IOException Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
    public static void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(ConnectToServerController.class.getResource("/assets/fxmls/ConnectToServer.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Connect To Server");
        Image iconImage = new Image(Objects.requireNonNull(
                ConnectToServerController.class.getResourceAsStream("/assets/clientIcon.png")));
        primaryStage.getIcons().clear();
        primaryStage.getIcons().add(iconImage);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> forcedExit());
    }

    /**
     * forcedExit method - deals with forced exits - closes the single stage and closes the program.
     */
    private static void forcedExit() {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }

    /**
     * connectToServer method - checks for valid server input and if valid - tried to connect to the server.
     * creates new stage using the singletone stage.
     * @param event Description: A click on the connect button occurred
     * @throws Exception Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
     */
    @FXML
    void connectToServer(ActionEvent event) throws Exception {
        if (!ValidatingTextField(txtPort, "port"))
            return;

        ClientConfiguration clientConfig = new ClientConfiguration(txtHost.getText(),
                Integer.parseInt(txtPort.getText()));
        try {
            //try to connect to server
            ClientUI.chat = new ClientController(clientConfig.getHost(), clientConfig.getPort());
        } catch (Exception e) {
            HandleConnectionError();
            return;
        }

        UserInstallationController UserCon = new UserInstallationController();
		Stage stage = StageSingleton.getInstance().getStage();
		UserCon.start(stage);
    }

    private void HandleConnectionError() {
        ErrorLabel.setText("Error: can't connect to the server");
        ErrorHelpLabel.setText("Please check IP and PORT");
    }

    private boolean ValidatingTextField(TextField textField, String errorSubject) {
        String str = textField.getText();
        if (txtPort.getText().isEmpty()) {
            ErrorLabel.setText("Error: " + errorSubject + " can't be empty port");
            ErrorHelpLabel.setText("Please try again.");
            textField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            return false;
        }
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                ErrorLabel.setText("Error: " + errorSubject + " contains only numbers..");
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