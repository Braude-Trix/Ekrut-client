package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import gui.workers.*;
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
 * This class describes the functionality of the login page, Including connecting in different configurations,
 * entering different pages according to the types of user
 */
public class LoginController implements Initializable{
    /**
     * This field describes the user connected at login
     */
    public static User user = null;
    
    /**
     * This field describes an order object depending on what the user chooses (self-pickup, delivery) or on-site
     */
    public static Order order = null;
    
    /**
     * This field describes a user who is in the OL configuration and is both a customer and an employee,
     * the customer's details are saved in position 0 and the employee's details in position 1
     */
    public static List<Object> customerAndWorker = null;

    private String configuration;
    
    private Stage stage = StageSingleton.getInstance().getStage();
    
    /**
     * This field describes a thread that checks every 10 seconds if a new message has been received for the connected user.
     */
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

    /**
     * This method initializes data before the screen comes up
     */
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

    private void removeErrorStyle() {
        txtUsername.getStyleClass().remove("validation-error");
        txtPassword.getStyleClass().remove("validation-error");
        errorLabel.setText("");
    }

    private void SelectHomePageToOpen() throws Exception {
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
        ClientUI.chat.accept(request);
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
        ClientUI.chat.accept(request);

        handleResponseGetUser();
    }

    private void handleResponseGetUser() {
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
        ClientUI.chat.accept(request);

        handleResponseGetCustomerForEkConfiguration();
    }

    private void handleResponseGetCustomerForEkConfiguration() {
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
        ClientUI.chat.accept(request);
        handleResponseGetUserForOLConfiguration();
    }

    private void handleResponseGetUserForOLConfiguration() throws Exception {
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

    /**
     * @param stage, Description: This is a singleton stage to display the selected window on it
     * @param worker, Description: This parameter is the employee returned from the db
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
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
                new ServiceOperatorController().start(stage); 
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
        ClientUI.chat.accept(request);

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


    /**
     * @author gal
     * A class that implements a runnable task for retrieving messages from the server.
     */
    public class getMessages implements Runnable {

    	/**
         * Sends a request to the server to retrieve messages and displays the received messages in an alert window.
         * If there are no messages or an error occurs, the appropriate message is printed to the console.
         * This task is executed every 10 seconds until the thread is interrupted.
         */
        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                List<Object> paramList = new ArrayList<>();
                Request request = new Request();
                request.setPath("/getMessages");
                request.setMethod(Method.GET);
                if(LoginController.user == null)
                    return;
                paramList.add(LoginController.user.getId().toString());
                request.setBody(paramList);
                ClientUI.chat.accept(request);
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
                } catch (Exception e) {
                	System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
                Client.MsgResFromServer = null;
            }
        }


        private void createAnAlert(Alert.AlertType alertType, String alertTitle, String alertMessage) {
            Alert alert = new Alert(alertType); 
            alert.setTitle(alertTitle);
            alert.setHeaderText("New message for you !");
            alert.setContentText(alertMessage);
            alert.show();
        }
    }

}
