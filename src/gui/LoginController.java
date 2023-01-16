package gui;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import client.IClient;
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
import models.Response;
import models.ResponseCode;
import models.User;
import models.Worker;
import utils.IUtil;
import utils.Utils;

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

    private static IUtil utilInterface; 
    private IClient clientInterface; 
    private static IInitWindow initWindowInterface;
    private IRunThread iRunThreadInterface;


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

        Parent root = FXMLLoader.load(getClass().getResource("/assets/fxmls/login.fxml"));

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> forcedExit());
    }
    
    public LoginController() {
    	utilInterface = new UtilWrapper();
    	clientInterface = new ClientWrapper();
    	initWindowInterface = new InitWindow();
    	iRunThreadInterface = new RunThread();
    }
    
    public LoginController(IUtil utilI, IClient iclient, IInitWindow iInitWindow, IRunThread iRunThread) {
    	utilInterface = utilI;
    	clientInterface = iclient;
    	initWindowInterface = iInitWindow;
    	iRunThreadInterface = iRunThread;
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
    void login(ActionEvent event) throws Exception {
        boolean isInputErrorUsername = false;
        boolean isInputErrorPassword = false;

        utilInterface.setErrorLabel("");

        if (utilInterface.isBlankString(utilInterface.getTxtUsername())){
            isInputErrorUsername = true;
        }
        if (utilInterface.isBlankString(utilInterface.getTxtPassword())){
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
        	utilInterface.setErrorLabel("The username and password are incorrect");
        }
        else if (invalidPassword) {
        	utilInterface.setErrorLabel("The password is incorrect");
        }
        else if (invalidUsername) {
        	utilInterface.setErrorLabel("The username is incorrect");
        }
    }

    private void SelectHomePageToOpen() throws Exception {
        requestUser();
        if (user == null) {
            return;
        }

        if (configuration.equals("EK")) {
            requestEKCustomer();
        }
        else if (configuration.equals("OL")){
            requestOLUser();
        }
        
        if (clientInterface.getResFromServer().getCode() == ResponseCode.DB_ERROR ||
        		clientInterface.getResFromServer().getCode() == ResponseCode.SERVER_ERROR) {
        	return;
        }
        
        iRunThreadInterface.Run();
    }

    private void setComboBoxFastLogin() {
        Request request = new Request();
        request.setPath("/login/getAllSubscriberForFastLogin");
        request.setMethod(Method.GET);
        request.setBody(null);
        clientInterface.setRequestForServer(request);
    	if (clientInterface.getResFromServer() == null) {
        	utilInterface.setErrorTouch("Communication error");
        	return;
    	}
        switch (clientInterface.getResFromServer().getCode()) {
            case OK:
                setSubscribers(clientInterface.getResFromServer().getBody());
                break;
            default:
            	utilInterface.setErrorTouch(clientInterface.getResFromServer().getDescription());
                break;
        }
    }

    private void setSubscribers(List<Object> subscribers) {
    	if (subscribers == null) {
    		return;
    	}
        ObservableList<Integer> options = FXCollections.observableArrayList();
        for (int i = 0;i<subscribers.size();i++) {
            options.add((Integer)subscribers.get(i));
        }
        utilInterface.setAllSubscribers(options);
    }

    private void requestUser() {
        List<Object> usernameAndPassword = new ArrayList<>();
        usernameAndPassword.add(utilInterface.getTxtUsername());
        usernameAndPassword.add(utilInterface.getTxtPassword());

        Request request = new Request();
        request.setPath("/login/getUser");
        request.setMethod(Method.GET);
        request.setBody(usernameAndPassword);
        clientInterface.setRequestForServer(request);
        handleResponseGetUser();
    }

    private void handleResponseGetUser() {
    	if (clientInterface.getResFromServer() == null) {
        	utilInterface.setErrorLabel("Communication error");
        	return;
    	}
        switch (clientInterface.getResFromServer().getCode()) {
            case OK:
            	if (clientInterface.getResFromServer().getBody() != null) {
                    user = (User) clientInterface.getResFromServer().getBody().get(0);
            	}
            	else {
                	utilInterface.setErrorLabel("Data error");
            	}
                break;
            case INVALID_DATA:
            	utilInterface.setErrorLabel(clientInterface.getResFromServer().getDescription());
                break;
            default:
            	utilInterface.setErrorLabel(clientInterface.getResFromServer().getDescription());
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
        clientInterface.setRequestForServer(request);

        handleResponseGetCustomerForEkConfiguration();
    }

    private void handleResponseGetCustomerForEkConfiguration() throws Exception {
    	if (clientInterface.getResFromServer() == null) {
        	utilInterface.setErrorLabel("Communication error");
        	return;
    	}
    	switch (clientInterface.getResFromServer().getCode()) {
            case OK:
            	if (clientInterface.getResFromServer().getBody() != null && (clientInterface.getResFromServer().getBody().get(0) instanceof Customer)) {
                    user = (Customer) clientInterface.getResFromServer().getBody().get(0);
                	initWindowInterface.runWindow("EKController");
            	}
            	else {
            		utilInterface.setErrorLabel("Data error");
            	}
                break;
            case INVALID_DATA:
            	initWindowInterface.runWindow("UnregisteredUserController");
                break;
            default:
            	utilInterface.setErrorLabel(clientInterface.getResFromServer().getDescription());
                break;
        }
    }

    private void requestOLUser() throws Exception {
        List<Object> userDetails = new ArrayList<>();
        userDetails.add(user);
        Request request = new Request();
        request.setPath("/login/getUserForOLConfiguration");
        request.setMethod(Method.GET);
        request.setBody(userDetails);
        clientInterface.setRequestForServer(request);
        handleResponseGetUserForOLConfiguration();
    }

    private void handleResponseGetUserForOLConfiguration() throws Exception {
    	if (clientInterface.getResFromServer() == null) {
        	utilInterface.setErrorLabel("Communication error");
        	return;
    	}
    	switch (clientInterface.getResFromServer().getCode()) {
            case OK:
                setUserWindow(clientInterface.getResFromServer().getBody());
                break;
            case INVALID_DATA:
            	initWindowInterface.runWindow("UnregisteredUserController");
                break;
            default:
            	utilInterface.setErrorLabel(clientInterface.getResFromServer().getDescription());
                break;
        }
    }


    private void setUserWindow(List<Object> userDetails) throws Exception {
        PickupController.scene = null;
        DeliveryFormController.scene = null;
        if (userDetails == null) {
        	utilInterface.setErrorLabel("Data error");
        	user = null;
        }
        else if (userDetails.size() == 2 && (userDetails.get(0) instanceof Customer) && (userDetails.get(1) instanceof Worker)) {
            customerAndWorker = userDetails;
            initWindowInterface.runWindow("SelectOptionWorkerOrCustomer");
        }
        else if(userDetails.size() == 1 && (userDetails.get(0) instanceof Customer)) {
            user = (Customer) userDetails.get(0);
            initWindowInterface.runWindow("OLController");
        }
        else if(userDetails.size() == 1 && (userDetails.get(0) instanceof Worker)) {
            user = (Worker) userDetails.get(0);
            setWindowByTypeWorker(stage, (Worker)userDetails.get(0));
        }
        else {
        	utilInterface.setErrorLabel("Data error");
        	user = null;
        }
    }

    /**
     * @param stage, Description: This is a singleton stage to display the selected window on it
     * @param worker, Description: This parameter is the employee returned from the db
     * @throws Exception, Description: An exception will be thrown if there is a problem with the window that opens
     */
    public static void setWindowByTypeWorker(Stage stage, Worker worker) throws Exception {
    	if (worker.getType() == null) {
    		user = null;
    		utilInterface.setErrorLabel("Employee error");
        	return;
    	}
        switch (worker.getType()) {
            case CEO:
            	initWindowInterface.runWindow("CeoGui");
                break;
            case OperationalWorker:
            	initWindowInterface.runWindow("OperationalWorkerGui");
                break;
            case MarketingManager:
            	initWindowInterface.runWindow("MarketingManagerController");
                break;
            case MarketingWorker:
            	initWindowInterface.runWindow("MarketingWorkerWindowController");
                break;
            case RegionalManager:
            	initWindowInterface.runWindow("RegionalManagerGui");
                break;
            case RegionalDelivery:
            	initWindowInterface.runWindow("RegionalDeliveryController");
                break;
            case ServiceOperator:
            	initWindowInterface.runWindow("ServiceOperatorController"); 
                break;
            default:
            	user = null;
        		utilInterface.setErrorLabel("Employee error");
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
//        comboBoxSubscribers.getStyleClass().remove("validation-error");
        if (!isValidFillComboBoxes()){
            return;
        }
        requestUserById();
    }

    private boolean isValidFillComboBoxes() {
        if (utilInterface.getValueSubscriberSelected() == null) {
//            comboBoxSubscribers.getStyleClass().add("validation-error");
        	utilInterface.setErrorTouch("Please select subscriber id");
            return false;
        }
        return true;
    }

    private void requestUserById() throws Exception {
        List<Object> idSubscriber = new ArrayList<>();
        idSubscriber.add(utilInterface.getValueSubscriberSelected());
        Request request = new Request();
        request.setPath("/login/getCustomerById");
        request.setMethod(Method.GET);
        request.setBody(idSubscriber);
        clientInterface.setRequestForServer(request);

        handleResponseGetUserById();
    }

    private void handleResponseGetUserById() throws Exception {
    	if (clientInterface.getResFromServer() == null) {
        	utilInterface.setErrorTouch("Communication error");
        	return;
    	}
        switch (clientInterface.getResFromServer().getCode()) {
            case OK:
            	if (clientInterface.getResFromServer().getBody() != null) {
	                user = (Customer) clientInterface.getResFromServer().getBody().get(0);
	            	initWindowInterface.runWindow("EKController");
	                iRunThreadInterface.Run();
            	}
            	else {
            		utilInterface.setErrorTouch("Data error");
            	}
                break;
            default:
            	utilInterface.setErrorTouch(clientInterface.getResFromServer().getDescription());
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
                try {
                    switch (Client.MsgResFromServer.getCode()) {
                        case OK:
                            StringBuilder msgToClient = new StringBuilder();
                            List<Object> result = Client.MsgResFromServer.getBody();
                            if (result.size() == 0 || result.size() == 1)
                                break;
                            for (int i = 1; i < result.size(); i++) {
                                msgToClient.append("\n");
                                msgToClient.append(result.get(i).toString());
                            }
                            Platform.runLater(() -> createAnAlert(Alert.AlertType.INFORMATION, "Information", msgToClient.toString()));
                            break;

                        default:
                            System.out.println(Client.MsgResFromServer.getDescription());
                    }
                }
                catch(Exception e){
                    System.out.println(e.getMessage());
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
    
    
	public class UtilWrapper implements IUtil {
	    public boolean isBlankString(String s) {
	        return Utils.isBlankString(s);
	    }
	    
	    public String getTxtPassword() {
			return txtPassword.getText();
		}

		public String getTxtUsername() {
			return txtUsername.getText();
		}

		public String getErrorLabel() {
			return errorLabel.getText();
		}
		
		public void setErrorLabel(String msgError) {
			errorLabel.setText(msgError);
		}
		
		public String getErrorTouch() {
			return errorTouch.getText();
		}
		
		public void setErrorTouch(String msgError) {
			errorTouch.setText(msgError);
		}
		
		public Integer getValueSubscriberSelected() {
			return comboBoxSubscribers.getValue();
		}
		
		public void setValueSubscriberSelected(Integer id) {
			comboBoxSubscribers.setValue(id);
		}
		
		public ObservableList<Integer> getAllSubscribers() {
			return comboBoxSubscribers.getItems();
		}
		
		public void setAllSubscribers(ObservableList<Integer> options) {
			comboBoxSubscribers.getItems().addAll(options);
		}
	}
	
	public class ClientWrapper implements IClient{

		@Override
		public void setRequestForServer(Request request) {
	        ClientUI.chat.accept(request);			
		}

		@Override
		public Response getResFromServer() {
			return Client.resFromServer;
		}
		
	}
	
	public class InitWindow implements IInitWindow{

		@Override
		public void runWindow(String nameController) throws Exception {
			switch(nameController) {
            case "EKController":
                new EKController().start(stage);
                break;
            case "OLController":
                new OLController().start(stage);
                break;
            case "UnregisteredUserController":
                new UnregisteredUserController().start(stage);
                break;
            case "SelectOptionWorkerOrCustomer":
                new SelectOptionWorkerOrCustomer().start(stage);
                break;    
            case "CeoGui":
                new CeoGui().start(stage);
                break;
            case "OperationalWorkerGui":
                new OperationalWorkerGui().start(stage);
                break;
            case "MarketingManagerController":
            	new MarketingManagerController().start(stage);
                break;
            case "MarketingWorkerWindowController":
            	new MarketingWorkerWindowController().start(stage);
                break;
            case "RegionalManagerGui":
                new RegionalManagerGui().start(stage);
                break;
            case "RegionalDeliveryController":
                (new RegionalDeliveryController()).start(stage);
                break;
            case "ServiceOperatorController":
                new ServiceOperatorController().start(stage); 
                break;
            default:
            	break;
			}
		}
		
	}
	
    public class RunThread implements IRunThread{
		@Override
		public void Run() {
	        threadListeningNewMsg = new Thread(new getMessages());
	        threadListeningNewMsg.start();			
		}	
    }

}
