package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Method;
import models.Order;
import models.OrderStatus;
import models.PickUpMethod;
import models.Request;
import utils.Utils;

/**
 * @author gal
 * This class describes the client home page in EK configuration and the functionality enabled on the home screen in this window
 */
public class EKController implements Initializable {
	/**
	 * This field saves the scene of the window that opens for open back this window from others windows
	 */
	public static Scene scene;
    /**
     * This field describes when we are in this window and when we are not for a thread
     * that describes a window of time allowed without any activity on this page
     */
    public static boolean EKPageReplace = false;

    @FXML
    private Label errorLabel;
    
    @FXML
    private TextField txtPickupCode;
    
    @FXML
    private VBox VBoxEnterPickUp;

    @FXML
    private VBox VboxSuccessfulPickUpCode;

    @FXML
    private Button submitPickUpBtn;
    
    @FXML
    private AnchorPane anchorPane;
    
    @FXML
    private Label labelName;
    public static Thread staticTimeOutThread;
    
    /**
	 * This method initializes data before the screen comes up
     */
    @Override
	public void initialize(URL location, ResourceBundle resources) {
        Utils.setNameNavigationBar(labelName);
        EKPageReplace = false;
        Thread timeOutThread = new Thread(new TimeOutControllerEkMain());
        staticTimeOutThread = timeOutThread;
        timeOutThread.start();
	}
    
	/**
	 * This method describes setting up a new scene.
	 * @param primaryStage Description: The stage on which the scene is presented
	 * @throws Exception Description: An exception will be thrown if there is a problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/fxmls/EKMain.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
		primaryStage.setTitle("EKrut Main");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
			try {
				Utils.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
	
	/**
	 * This method navigates the client to the login page and logging him out. This
	 * method runs when the user clicked LogOut.
	 * 
	 * @param event Description: the current event when the click happened.
	 * @throws Exception Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
    @FXML
    void LogOut(ActionEvent event) throws Exception {
    	EKPageReplace = true;
		Utils.genericLogOut(getClass());

    }
    
    /**
     * This method describes clicking the submit button.
     *  Clicking this button, if the pickup code entered is correct, 
     *  will transfer the order to ek-op which it will have to bring to the customer.
     * @param event Description: Event - clicking the Submit button
     */
    @FXML
    void SubmitPickupCode(ActionEvent event) {
    	removeErrorStyle();
    	if (Utils.isBlankString(txtPickupCode.getText())) {
    		errorLabel.setText("Entered code is incorrect, please try again");
    		Utils.setFieldTextErrorBorder(txtPickupCode);
    		return;
    	}
    	
    	requestOrderByCode();
    }
    
    private void requestOrderByCode() {
		List<Object> details = new ArrayList<>();
		details.add(LoginController.user.getId());
		details.add(txtPickupCode.getText());
		details.add(UserInstallationController.machine.getId());
		Request request = new Request();
		request.setPath("/order/checkExistPickupOrderAndChangeStatus");
		request.setMethod(Method.PUT);
		request.setBody(details);
		ClientUI.chat.accept(request);// sending the request to the server.
		switch (Client.resFromServer.getCode()) {
		case OK:
			replaceHboxSuccessEnterPickupCode();
			break;
		default:
			errorLabel.setText((Client.resFromServer.getDescription()));
			Utils.setFieldTextErrorBorder(txtPickupCode);
			break;
		}
    }
    
    private void replaceHboxSuccessEnterPickupCode() {
    	VBoxEnterPickUp.setVisible(false);
    	VboxSuccessfulPickUpCode.setVisible(true);
    	txtPickupCode.setText("");
    }
    
    /**
     * This method describes a transition to the window of starting an order
     * @param event Description: Clicking the "create new order" button
     */
    @FXML
    void createNewOrder(ActionEvent event) {
    	LoginController.order = new Order(null, null, 0, UserInstallationController.machine.getId(), OrderStatus.Collected,
    			PickUpMethod.selfPickUp, LoginController.user.getId());
    	EKPageReplace = true;
		try {
			new NewOrderController().start(StageSingleton.getInstance().getStage());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * This method describes the possibility of entering an additional pickup code if the customer has another code.
     * @param event Description: Event - clicking the hyper link "Would you like to enter another code? Click here"
     */
    @FXML
    void EnterAnotherPickupCode(ActionEvent event) {
    	VBoxEnterPickUp.setVisible(true);
    	VboxSuccessfulPickUpCode.setVisible(false);
    }
    
    /**
     * This method requires when you click anywhere else on the screen to get the focus.
     * @param event Description: The screen is clicked the event is sent
     */
    @FXML
    void requestFocus(MouseEvent event) {
    	anchorPane.requestFocus();
    }
    
    private void removeErrorStyle() {
    	txtPickupCode.getStyleClass().remove("validation-error");
    	errorLabel.setText("");
    }
    
    /**
     * A class that implements a runnable task for detecting and handling a time out event.
     * The time out event occurs when the elapsed time since the time out start time exceeds a specified time out time.
     */
    static class TimeOutControllerEkMain implements Runnable {
        private int TimeOutTime = Utils.TIME_OUT_TIME_IN_MINUTES;//
        private long TimeOutStartTime = System.currentTimeMillis();

        /**
         * Detects and handles a time out event.
         * This task is executed every 10 seconds until the thread is interrupted or the `EKPageReplace` flag is set to `true`.
         * If a time out event occurs, the log out process is initiated.
         */
        @Override
        public void run() {
            Platform.runLater(()->handleAnyClick());
            while (!Thread.currentThread().isInterrupted()) {
                long TimeOutCurrentTime = System.currentTimeMillis();
                if (TimeOutCurrentTime - TimeOutStartTime >= TimeOutTime * 60 * 1000) {
                    System.out.println("Time Out passed");
                    try {
                        Platform.runLater(()-> {
                            try {
                                Utils.genericLogOut(getClass());
                            } catch (Exception e) {
                            }
                        });
                    } catch (Exception e) {
                    }
                    return;
                }
                if(EKPageReplace) {
                    System.out.println("Thread closed from TimeOut Controller");
                    return;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        
        private void handleAnyClick() {
            StageSingleton.getInstance().getStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<javafx.scene.input.MouseEvent>(){
                @Override
                public void handle(MouseEvent mouseEvent) {
                    System.out.print("Mouse clicked, timeout time reset\n");
                    TimeOutStartTime = System.currentTimeMillis();
                }
            });
        }
    }
    
}
