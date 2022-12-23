package gui;

import client.Client;
import clientModels.ClientConfiguration;
import client.ClientController;
import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Method;
import models.ProductInMachine;
import models.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    //	start method - starts the first stage.
    public static void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(ConnectToServerController.class.getResource("/assets/ConnectToServer.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Connect To Server");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setOnCloseRequest(e -> forcedExit());
    }

    //	forcedExit method - deals with forced exits - closes the single stage and closes the program.
    private static void forcedExit() {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }

    //	connectToServer method - checks for valid server input and if valid - tried to connect to the server.
//	creates new stage using the singletone stage.
    @FXML
    void connectToServer(ActionEvent event) throws Exception {
        if (!ValidatingTextField(txtPort, "port"))
            return;

        ClientConfiguration clientConfig = new ClientConfiguration(txtHost.getText(),
                Integer.parseInt(txtPort.getText()));
        try {
            //try connect to server
            ClientUI.chat = new ClientController(clientConfig.getHost(), clientConfig.getPort());
        } catch (Exception e) {
            HandleConnectionError();
            return;
        }

        Stage stage = StageSingleton.getInstance().getStage();
        AnchorPane root = FXMLLoader.load(getClass().getResource("/assets/NewOrder.fxml"));
        stage.setTitle("Subscribers Panel");
        stage.setScene(new Scene(root));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.show();
        stage.setMinHeight(stage.getHeight());
        stage.setMinWidth(stage.getWidth());


        /////////
        Thread thread = new Thread(new getMessages());
        thread.start();
    }

    //	HandleConnectionError method - deals with invalid connection errors.
    private void HandleConnectionError() {

        ErrorLabel.setText("Error: can't connect to the server");
        ErrorHelpLabel.setText("Please check IP and PORT");

    }

    //	ValidatingTextField - checks for correct server input and responds with correct error labels.
    private boolean ValidatingTextField(TextField textField, String errorSubject) {
        String str = textField.getText();

        //checks if the port is empty and throws error
        if (txtPort.getText().isEmpty()) {
            ErrorLabel.setText("Error: " + errorSubject + " can't be empty port");
            ErrorHelpLabel.setText("Please try again.");
            textField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
            return false;
        }

        //checks if in the text field has a character
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


    public class getMessages implements Runnable {

        @Override
        public void run() {
            while (true) {
                List<Object> paramList = new ArrayList<>();
                Request request = new Request();
                request.setPath("/getMessages");
                request.setMethod(Method.GET);
                paramList.add("1");
                request.setBody(paramList);
                ClientUI.chat.accept(request);// sending the request to the server.
                switch (Client.MsgResFromServer.getCode()) {
                    case OK:
                        StringBuilder msgToClient = new StringBuilder();
                        List<Object> result = Client.MsgResFromServer.getBody();
                        if (result.size() == 0 || result.size() == 1)
                            break;
                        for(int i = 1; i < result.size(); i++){
                            msgToClient.append("\n");
                            msgToClient.append(result.get(i).toString());
                        }
                        Platform.runLater(()->createAnAlert(Alert.AlertType.INFORMATION, "Information", msgToClient.toString()));
                        break;

                    default:
                        System.out.println("Some error occurred");
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Client.MsgResFromServer = null;
            }

        }

        public void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
            Alert alert = new Alert(alertType); //Information, Error
            alert.setContentText(alertTitle); // Information, Error
            alert.setContentText(alertMessage);
            alert.show();
        }
    }

}