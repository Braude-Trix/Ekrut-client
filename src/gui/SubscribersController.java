package gui;

import client.ChatClient;
import client.ClientUI;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import models.Method;
import models.Request;
import models.Response;
import models.Subscriber;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SubscribersController implements Initializable {
    private static final int CreditCardLength = 16;
    public static Scene successfulUpdateScene;
    @FXML
    private TableView<Subscriber> subscribersTableView;

    @FXML
    private TableColumn<Subscriber, String> firstNameCol;

    @FXML
    private TableColumn<Subscriber, String> lastNameCol;

    @FXML
    private TableColumn<Subscriber, String> idCol;

    @FXML
    private TableColumn<Subscriber, String> phoneNumberCol;

    @FXML
    private TableColumn<Subscriber, String> emailAddressCol;

    @FXML
    private TableColumn<Subscriber, String> creditCardNumberCol;

    @FXML
    private TableColumn<Subscriber, String> subscriberNumberCol;

	@FXML
	private Button SubmitBtn;
	
	@FXML
	private Button refreshBtn;

    @FXML
    private TextField inputFirstName;

    @FXML
    private TextField inputLastName;

    @FXML
    private TextField inputId;

    @FXML
    private TextField inputPhoneNumber;

    @FXML
    private TextField inputEmailAddress;

    @FXML
    private TextField inputCreditCardNumber;

    @FXML
    private TextField inputSubscriberNumber;

    @FXML
    private Button ExitBtn;

    @FXML
    private Label SecondInputErrorLabel;

    @FXML
    private Label FirstInputErrorLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        firstNameCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("lastName"));
        idCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("id"));
        phoneNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("phoneNumber"));
        emailAddressCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("emailAddress"));
        creditCardNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("creditCardNumber"));
        subscriberNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("subscriberNumber"));
        requestAllSubscribers();
    }

    // submit function, handles click on 'submit' update subscriber form.
    @FXML
    void submit(ActionEvent event) {
        boolean isValidateFlag = true;
        ObservableList<Subscriber> currentTableData = subscribersTableView.getItems();
        String currentSubId = inputId.getText();

        for (Subscriber subscriber : currentTableData) {
//			searching for the correct subscriber to update.
            if (subscriber.getId().equals(currentSubId)) {// correct subscriber found.
//				checking input's validation.
//				checking if update contains new details,credit card contains only digits, subscriber's number is only digits or empty.
                isValidateFlag = ValidateInput(subscriber.getCreditCardNumber(), subscriber.getSubscriberNumber(),
                        inputCreditCardNumber.getText(), inputSubscriberNumber.getText());
//				if invalid input - don't continue with the update process.
                if (!isValidateFlag)
                    return;
//				build temporary subscriber with correct data.
                setSubscriberWithNewInput(subscriber);
//				create new update request.
                requestUpdateTableFromServer(subscriber);
//				handle response info:
                switch (ChatClient.resFromServer.getCode()) {
                    // response is OK, Server updated.
                    case OK:
                        //				updating local subscribers table view.
                        subscribersTableView.setItems(currentTableData);
                        subscribersTableView.refresh();
                        openSuccessfulUpdateScene();
                        resetInputForm();
                        break;
                    //				response is ERROR, Server didn't update.
                    default:
                        failUpdateSubscriberScene();
                        break;
                }
                break;
            }
        }
    }

    //	setSubscriberWithNewInput method, updates the data of wanted to-update subscriber.
    private void setSubscriberWithNewInput(Subscriber toUpdateSubscriber) {
        toUpdateSubscriber.setFirstName(inputFirstName.getText());
        toUpdateSubscriber.setLastName(inputLastName.getText());
        toUpdateSubscriber.setId(inputId.getText());
        toUpdateSubscriber.setPhoneNumber(inputPhoneNumber.getText());
        toUpdateSubscriber.setEmailAddress(inputEmailAddress.getText());
        toUpdateSubscriber.setCreditCardNumber(inputCreditCardNumber.getText());
        toUpdateSubscriber.setSubscriberNumber(inputSubscriberNumber.getText());

    }

    //	ValidateInputs method, validates the update form data.
//	this method checks:
//	1)update contains new details
//	2)credit card contains only digits
//	3)subscriber's number is only digits or empty.
    private boolean ValidateInput(String oldCreditCardNumber, String oldSubscriberNumber, String newCreditCardNumber,
                                  String newSubscriberNumber) {

        resetErrorStyle();
        boolean validFlag = true;
        // checking same input Error
        if (isSameInput(oldCreditCardNumber, oldSubscriberNumber, newCreditCardNumber, newSubscriberNumber)) {
            FirstInputErrorLabel.setText("Error:Please make any changes before updating");

            setErrorLabel(FirstInputErrorLabel, inputSubscriberNumber, "Nothing to update...");
            setErrorLabel(SecondInputErrorLabel, inputCreditCardNumber, "Please change anything and try again.");
            validFlag = false;
            return false;// no need to check future validations, input already invalid.
        }

        // checking credit card length - must be 16.
        if (newCreditCardNumber.length() != CreditCardLength) {
            setErrorLabel(SecondInputErrorLabel, inputCreditCardNumber, "Error:card must be 16 numbers..");
            validFlag = false;
        }
        // checking credit card input - must contain only digits.
        if (!isOnlyDigits(newCreditCardNumber)) {
            setErrorLabel(SecondInputErrorLabel, inputCreditCardNumber, "Error: credit card - only numbers..");
            validFlag = false;
        }
        // checking Subscriber Number inputs. must be only digits or empty.
        if ((!newSubscriberNumber.equals("") && (!isOnlyDigits(newSubscriberNumber)))) {
            setErrorLabel(FirstInputErrorLabel, inputSubscriberNumber, "Error: Sub Number - only numbers..");
            validFlag = false;
        }

        // reset Error labels and input invalid borders.
        if (validFlag) {
            resetErrorStyle();
        }

        return validFlag;
    }

    boolean isSameInput(String oldCreditCardNumber, String oldSubscriberNumber, String newCreditCardNumber,
                        String newSubscriberNumber) {
        if (oldCreditCardNumber.equals(newCreditCardNumber)) {
            if (oldSubscriberNumber == null && newSubscriberNumber.isEmpty()) {
                return true;
            } else if (oldSubscriberNumber != null && (oldSubscriberNumber.equals(newSubscriberNumber))) {
                return true;
            }
        }
        return false;
    }

    //	rowClicked method, occurs whenever the user clicks the subscribers table, gets selected subscriber data.
    @FXML
    void rowClicked(MouseEvent event) {
        try {
            Subscriber clickedSub = subscribersTableView.getSelectionModel().getSelectedItem();
            setInputFormDetails(clickedSub);
            allowInputFormEdit();

        } catch (NullPointerException e) {
//			catches null pointer exceptions whenever a user clicks the header row.
        }
    }

    //allowInputFormEdit method, changed credit card/sub number and submit button to available
    private void allowInputFormEdit() {
        inputCreditCardNumber.setDisable(false);
        inputSubscriberNumber.setDisable(false);
        SubmitBtn.setDisable(false);
        resetErrorStyle();
    }

    //setInputFormDetails method - setting input form details with correct subscriber
    private void setInputFormDetails(Subscriber clickedSubsciber) {

        inputFirstName.setText(String.valueOf(clickedSubsciber.getFirstName()));
        inputLastName.setText(String.valueOf(clickedSubsciber.getLastName()));
        inputId.setText(String.valueOf(clickedSubsciber.getId()));
        inputPhoneNumber.setText(String.valueOf(clickedSubsciber.getPhoneNumber()));
        inputEmailAddress.setText(String.valueOf(clickedSubsciber.getEmailAddress()));
        inputCreditCardNumber.setText(String.valueOf(clickedSubsciber.getCreditCardNumber()));
        if (String.valueOf(clickedSubsciber.getSubscriberNumber()).equals("null")) {
            inputSubscriberNumber.setText("");
        } else {
            inputSubscriberNumber.setText(String.valueOf(clickedSubsciber.getSubscriberNumber()));
        }

    }

    //	requestAllSubscribers method, requesting subscribers data in order to build the table.
//	this method creates an object of type 'Request' and sends it to the server.
    public void requestAllSubscribers() {
        Request request = new Request();
        request.setPath("/AllSubscribers");
        request.setMethod(Method.GET);
        request.setBody(null);
        ClientUI.chat.accept(request);// sending the request to the server.

//		handle response info:
        switch (ChatClient.resFromServer.getCode()) {
            case OK:
                // response is fine
                setUpTable(ChatClient.resFromServer);
                break;

//		response is ERROR, Server didn't send subscribers data.
            default:
                handleResponseError();
                break;
        }

    }

	// setUpTable method, creates the subscribers table on load.
	// this method is being called after the request of table data from server
	// occurred and after getting the response.
	private void setUpTable(Response response) {
		subscribersTableView.getItems().clear();
		List<Subscriber> subscribersList = new ArrayList<>();
		for (Object subscriber : response.getBody()) {
			if (subscriber instanceof Subscriber) {
				subscribersList.add((Subscriber) subscriber);
			}
		}
		for (Subscriber sub : subscribersList)
			subscribersTableView.getItems().add(sub);
		System.out.println("---> Subscribers table updated successfully.");
	}

    //		handleResponseError method, handles Errors brought with responses
    private void handleResponseError() {
//		clears subscribers table, sets placeholder to be error details.
        subscribersTableView.getItems().clear();
        System.out.println("response error: " + ChatClient.resFromServer.getDescription());
        Label errorLbl = new Label("Can't show subscribers..\n" + ChatClient.resFromServer.getDescription());
        errorLbl.setStyle(
                "-fx-text-fill : #FF3547;-fx-font-weight: bold;-fx-font-family: Inconsolata:700; -fx-font-size: 25");
        subscribersTableView.setPlaceholder(errorLbl);

    }

    // requestUpdateTableFromServer method, requesting to update table with new subscriber information.
//	this method creates an object of type 'Request' and sends it to the server.
    public void requestUpdateTableFromServer(Subscriber NewSub) {

        List<Object> lst = new ArrayList<>();

        // send to server null in subscriber number if the string is empty
        if (NewSub.getSubscriberNumber().isEmpty()) {
            NewSub.setSubscriberNumber(null);
        }

        lst.add(NewSub);
        Request request = new Request();
        request.setPath("/UpdateSubscriber");
        request.setMethod(Method.PUT);
        request.setBody(lst);
        ClientUI.chat.accept(request);
        resetErrorStyle();
    }

    //	isOnlyDigits method, checks if a given string contains only numbers. if so - returns true, else return false.
    private boolean isOnlyDigits(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

//	 openSuccessfulUpdateScene method, opens a new stage with success message.
	public void openSuccessfulUpdateScene() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("SuccessfulUpdate.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), 413, 251);
			Stage stage = new Stage();
			stage.setTitle("Successful Update");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			stage.setMinHeight(stage.getHeight());
			stage.setMinWidth(stage.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

//	 failUpdateScene method, opens a new stage with error message.
	public void failUpdateSubscriberScene() {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader();
			fxmlLoader.setLocation(getClass().getResource("FailUpdateSubscriber.fxml"));
			Scene scene = new Scene(fxmlLoader.load(), 413, 251);
			Stage stage = new Stage();
			stage.setTitle("Fail Update");
			stage.setScene(scene);
			stage.setResizable(false);
			stage.show();
			stage.setMinHeight(stage.getHeight());
			stage.setMinWidth(stage.getWidth());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    //	resetInputForm method- resets form entries and borders.
    public void resetInputForm() {
        inputFirstName.setText("");
        inputLastName.setText("");
        inputId.setText("");
        inputPhoneNumber.setText("");
        inputEmailAddress.setText("");
        inputCreditCardNumber.setText("");
        inputSubscriberNumber.setText("");
        inputCreditCardNumber.setDisable(true);
        inputSubscriberNumber.setDisable(true);
        SubmitBtn.setDisable(true);
        inputCreditCardNumber.setStyle("-fx-border-style: none;");
        inputSubscriberNumber.setStyle("-fx-border-style: none;");
        subscribersTableView.getSelectionModel().clearSelection();

    }

    //	setErrorLabel method, takes a given Label and String and sets the string to the label. addes red border to input
    private void setErrorLabel(Label label, TextField inputTextField, String errorText) {
        label.setText(errorText);
        inputTextField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
    }

    // resetErrorStyle method, resets available input fields and error messages back to default.
    private void resetErrorStyle() {
        SecondInputErrorLabel.setText("");
        FirstInputErrorLabel.setText("");
        inputSubscriberNumber.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000;");
        inputCreditCardNumber.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000;");

    }

    public static void moveToConnectToServer() {
        Stage stage = StageSingleton.getInstance().getStage();
        Platform.runLater(() -> {
            try {
                ConnectToServerController.start(stage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    //	exitClientGUI method, closes the current stage and exits the program
    @FXML
    void exitClientGUI(ActionEvent event) {
        StageSingleton.getInstance().getStage().close();
        System.exit(0);
    }
}
