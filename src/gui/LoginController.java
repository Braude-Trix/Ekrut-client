package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import gui.workers.CeoGui;
import gui.workers.MarketingManagerController;
import gui.workers.MarketingWorkerWindowController;
import gui.workers.OperationalWorkerGui;
import gui.workers.RegionalDeliveryController;
import gui.workers.RegionalManagerGui;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Customer;
import models.Method;
import models.Order;
import models.Request;
import models.ResponseCode;
import models.User;
import models.Worker;
import utils.Util;

/**
 * @author gal
 * This class describes the functionality of the login page
 */
public class LoginController implements Initializable{
    public static User user = null;
    public static Order order = null;
    public static List<Object> customerAndWorker = null;

    private String configuration;
    private Stage stage = StageSingleton.getInstance().getStage();
    public static Thread threadListeningNewMsg;

    @FXML
    private ComboBox<Integer> comboBoxSubscribers;

    @FXML
    private VBox simulationTouch;

    @FXML
    private Label errorLabel;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUsername;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label errorTouch;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        user = null;
        order = null;
        customerAndWorker = null;
        configuration = UserInstallationController.configuration;
        if (configuration.equals("EK")) {
            simulationTouch.setVisible(true);
            setComboBoxFastLogin();
        }
    }

    /**
     * This method describes setting up a new scene.
     * @param primaryStage, Description: The stage on which the scene is presented
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/assets/login.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setOnCloseRequest(e -> forcedExit());
    }


    private static void forcedExit() {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }

    /**
     * This method opens a customer or employee window depending on the type of user.
     * And only if the details are correct.
     * @param event, Description: Event - clicking the Login button
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    @FXML
    void moveHomePage(ActionEvent event) throws Exception {
        anchorPane.requestFocus();
        boolean isInputErrorUsername = false;
        boolean isInputErrorPassword = false;

        removeErrorStyle();

        if (Util.isBlankString(txtUsername.getText())){
            Util.setFieldTextErrorBorder(txtUsername);
            isInputErrorUsername = true;
        }
        if (Util.isBlankString(txtPassword.getText())){
            Util.setFieldTextErrorBorder(txtPassword);
            isInputErrorPassword = true;
        }

        if (isInputErrorUsername || isInputErrorPassword) {
            setErrorLabel(isInputErrorUsername, isInputErrorPassword);
            return;
        }

        SelectHomePageToOpen();
    }

    /**
     * This method sets an appropriate error message.
     * @param invalidUsername, Description: This parameter is boolean if false does not set an error message associated with it.
     * @param invalidPassword, Description: This parameter is boolean if false does not set an error message associated with it.
     */
    private void setErrorLabel(boolean invalidUsername, boolean invalidPassword) {
        if (invalidPassword && invalidUsername) {
            errorLabel.setText("The username and password are incorrect");
        }
        else if (invalidPassword) {
            errorLabel.setText("The password is incorrect");
        }
        else if (invalidUsername) {
            errorLabel.setText("The username is incorrect");
        }
    }

    /**
     * This method removes error formatting for normal outputs.
     */
    private void removeErrorStyle() {
        txtUsername.getStyleClass().remove("validation-error");
        txtPassword.getStyleClass().remove("validation-error");
        errorLabel.setText("");
    }

    /**
     * This method opens a customer or employee window depending on the type of user.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    private void SelectHomePageToOpen() throws Exception {
        //request to user from db
        requestUser();
        if (user == null) {
            return;
        }


        if (configuration.equals("EK")) {
            requestEKCustomer();
            if (Client.resFromServer.getCode() == ResponseCode.INVALID_DATA) {
                setWindowUnregisteredUser();
            }
            else {
                EKController EKcon = new EKController();
                EKcon.start(stage);
            }

        }
        else if (configuration.equals("OL")){
            requestOLUser();
        }
        threadListeningNewMsg = new Thread(new getMessages());
        threadListeningNewMsg.start();

    }

    private void setComboBoxFastLogin() {
        Request request = new Request();
        request.setPath("/login/getAllSubscriberForFastLogin");
        request.setMethod(Method.GET);
        request.setBody(null);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                setSubscribers(Client.resFromServer.getBody());
                break;
            default:
                errorTouch.setText(Client.resFromServer.getDescription());
                break;
        }
    }

    private void setSubscribers(List<Object> subscribers) {
        ObservableList<Integer> options = FXCollections.observableArrayList();
        for (int i = 0;i<subscribers.size();i++) {
            options.add((Integer)subscribers.get(i));
        }
        comboBoxSubscribers.getItems().addAll(options);
    }

    private void requestUser() {
        List<Object> usernameAndPassword = new ArrayList<>();
        usernameAndPassword.add(txtUsername.getText());
        usernameAndPassword.add(txtPassword.getText());

        Request request = new Request();
        request.setPath("/login/getUser");
        request.setMethod(Method.GET);
        request.setBody(usernameAndPassword);
        ClientUI.chat.accept(request);// sending the request to the server.

        handleResponseGetUser();
    }

    private void handleResponseGetUser() {
//		handle response info:
        switch (Client.resFromServer.getCode()) {
            case OK:
                user = (User) Client.resFromServer.getBody().get(0);
                break;
            case INVALID_DATA:
                errorLabel.setText(Client.resFromServer.getDescription());
                break;
            default:
                errorLabel.setText(Client.resFromServer.getDescription());
                break;
        }
    }

    private void requestEKCustomer() throws Exception {
        List<Object> userDetails = new ArrayList<>();
        userDetails.add(user);
        Request request = new Request();
        request.setPath("/login/getUserForEkConfiguration");
        request.setMethod(Method.GET);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);// sending the request to the server.

        handleResponseGetCustomerForEkConfiguration();
    }

    private void handleResponseGetCustomerForEkConfiguration() {
//		handle response info:
        switch (Client.resFromServer.getCode()) {
            case OK:
                user = (Customer) Client.resFromServer.getBody().get(0);
                break;
            case INVALID_DATA:
                break;
            default:
                errorLabel.setText(Client.resFromServer.getDescription());
                break;
        }
    }

    private void setWindowUnregisteredUser() throws Exception {
        UnregisteredUserController unregisteredCon = new UnregisteredUserController();
        unregisteredCon.start(stage);
    }

    private void requestOLUser() throws Exception {
        List<Object> userDetails = new ArrayList<>();
        userDetails.add(user);
        Request request = new Request();
        request.setPath("/login/getUserForOLConfiguration");
        request.setMethod(Method.GET);
        request.setBody(userDetails);
        ClientUI.chat.accept(request);// sending the request to the server.
        handleResponseGetUserForOLConfiguration();
    }

    private void handleResponseGetUserForOLConfiguration() throws Exception {
//		handle response info:
        switch (Client.resFromServer.getCode()) {
            case OK:
                setUserWindow(Client.resFromServer.getBody());
                break;
            case INVALID_DATA:
                setWindowUnregisteredUser();
                break;
            default:
                errorLabel.setText(Client.resFromServer.getDescription());
                break;
        }
    }


    private void setUserWindow(List<Object> userDetails) throws Exception {
        if (userDetails.size() == 2) {
            customerAndWorker = userDetails;
            SelectOptionWorkerOrCustomer selectWindow = new SelectOptionWorkerOrCustomer();
            selectWindow.start(stage);
        }
        else if(userDetails.get(0) instanceof Customer) {
            user = (Customer) userDetails.get(0);
            OLController OLcon = new OLController();
            OLcon.start(stage);
        }
        else if(userDetails.get(0) instanceof Worker) {
            user = (Worker) userDetails.get(0);
            setWindowByTypeWorker(stage, (Worker)userDetails.get(0));
        }
    }

    public static void setWindowByTypeWorker(Stage stage, Worker worker) throws Exception {
        switch (worker.getType()) {
            case CEO:
                new CeoGui().start(stage);
                break;
            case OperationalWorker:
                new OperationalWorkerGui().start(stage);
                break;
            case MarketingManager:
            	new MarketingManagerController().start(stage);
                break;
            case MarketingWorker:
            	new MarketingWorkerWindowController().start(stage);
                break;
            case RegionalManager:
                new RegionalManagerGui().start(stage);
                break;
            case RegionalDelivery:
                (new RegionalDeliveryController()).start(stage);
                break;
            case ServiceOperator:
                new ServiceOperatorController().start(stage); // badihi
                break;

        }
    }


    /**
     * This method responds to the event of interfacing with EKT (pressing a button).
     * @param event, Description: Event - clicking the Touch button.
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens.
     */
    @FXML
    void btnTouch(ActionEvent event) throws Exception {
        comboBoxSubscribers.getStyleClass().remove("validation-error");
        if (!isValidFillComboBoxes()){
            return;
        }
        requestUserById();
    }

    private boolean isValidFillComboBoxes() {
        if (comboBoxSubscribers.getValue() == null) {
            comboBoxSubscribers.getStyleClass().add("validation-error");
            errorTouch.setText("Please select subscriber id");
            return false;
        }
        return true;
    }

    private void requestUserById() throws Exception {
        List<Object> idSubscriber = new ArrayList<>();
        idSubscriber.add(comboBoxSubscribers.getValue());
        Request request = new Request();
        request.setPath("/login/getCustomerById");
        request.setMethod(Method.GET);
        request.setBody(idSubscriber);
        ClientUI.chat.accept(request);// sending the request to the server.

        handleResponseGetUserById();
    }

    private void handleResponseGetUserById() throws Exception {
        switch (Client.resFromServer.getCode()) {
            case OK:
                user = (Customer) Client.resFromServer.getBody().get(0);
                EKController EKcon = new EKController();
                EKcon.start(stage);
                break;
            default:
                errorTouch.setText(Client.resFromServer.getDescription());
                break;
        }
    }
    /**
     * This method describes an event of clicking exit and exiting the application.
     * @param event, Description: Event - clicking the Exit button.
     */
    @FXML
    void Exit(ActionEvent event) {
        stage.close();
        System.exit(0);
    }

    /**
     * This method requires when you click anywhere else on the screen to get the focus.
     * @param event, Description: The screen is clicked the event is sent
     */
    @FXML
    void requestFocus(MouseEvent event) {
        anchorPane.requestFocus();

    }


    public class getMessages implements Runnable {

        @Override
        public void run() {
            System.out.println("Another one");
        	//While (true)
            while (!Thread.currentThread().isInterrupted()) {
                List<Object> paramList = new ArrayList<>();
                Request request = new Request();
                request.setPath("/getMessages");
                request.setMethod(Method.GET);
                if(LoginController.user == null)
                    return;
                paramList.add(LoginController.user.getId().toString());
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
                	System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    // handle the exception and stop the thread
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
                Client.MsgResFromServer = null;
            }
        }

        public void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
            Alert alert = new Alert(alertType); //Information, Error
            alert.setContentText(alertTitle); // Information, Error
            alert.setHeaderText("New message for you !");
            alert.setContentText(alertMessage);
            alert.show();
        }
    }

}
