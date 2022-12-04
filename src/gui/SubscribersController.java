package gui;

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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import models.Subscriber;

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
	private Label ErrorCardNumLabel;

	@FXML
	private Label ErrorSubscruiberNumLabel;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		firstNameCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("firstName"));
		lastNameCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("lastName"));
		idCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("id"));
		phoneNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("phoneNumber"));
		emailAddressCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("emailAddress"));
		creditCardNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("creditCardNumber"));
		subscriberNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("subscriberNumber"));
		inputCreditCardNumber.setStyle("-fx-text-box-border: #000000; -fx-border-width:1px; -fx-border-color: black; -fx-focus-color: #000000;");
		inputSubscriberNumber.setStyle("-fx-text-box-border: #000000; -fx-border-width:1px; -fx-border-color: black; -fx-focus-color: #000000;");
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

				isValidateFlag = ValidateInputs(subscriber.getCreditCardNumber(), subscriber.getSubscriberNumber(),
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
					inputCreditCardNumber.setText("");
					inputSubscriberNumber.setText("");
					

					break;

//				response is ERROR, Server didn't update.
				default:
					handleResponseError();
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
//		allowing null as subscriber number. transforms empty strings to null.
		// toUpdateSubscriber.setSubscriberNumber(inputSubscriberNumber.getText());
		toUpdateSubscriber.setSubscriberNumber(inputSubscriberNumber.getText());

	}

//	ValidateInputs method, validates the update form data.
//	this method checks:
//	1)update contains new details
//	2)credit card contains only digits
//	3)subscriber's number is only digits or empty.
	private boolean ValidateInputs(String oldCreditCardNumber, String oldSubscriberNumber, String newCreditCardNumber,
			String newSubscriberNumber) {

		boolean validFlag = true;
		resetErrorStyle();
		// checking same input Error
		if (isSameInput(oldCreditCardNumber, oldSubscriberNumber, newCreditCardNumber, newSubscriberNumber)) {
			ErrorSubscruiberNumLabel.setText("Error:Please make any changes before updating");
			makeRedErrorBorders(inputCreditCardNumber);
			makeRedErrorBorders(inputSubscriberNumber);
			setErrorLabel(ErrorSubscruiberNumLabel, "Nothing to update...");
			setErrorLabel(ErrorCardNumLabel, "Please change anything and try again.");
			validFlag = false;
			return false;// no need to check future validations, input already invalid.
		}

		// checking credit card length - must be 16.
		if (newCreditCardNumber.length() != CreditCardLength) {
			setErrorLabel(ErrorCardNumLabel, "Error:card must be 16 numbers..");
			makeRedErrorBorders(inputCreditCardNumber);
			validFlag = false;
		}
		// checking credit card input - must contain only digits.
		if (!isOnlyDigits(newCreditCardNumber)) {
			setErrorLabel(ErrorCardNumLabel, "Error: contains only numbers..");
			makeRedErrorBorders(inputCreditCardNumber);
			validFlag = false;
		}
		// checking Subscriber Number inputs. must be only digits or empty.
		if ((!newSubscriberNumber.equals("") && (!isOnlyDigits(newSubscriberNumber)))) {
			setErrorLabel(ErrorSubscruiberNumLabel, "Error:Subscriber Number contains only numbers...");
			makeRedErrorBorders(inputSubscriberNumber);
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
		if(oldCreditCardNumber.equals(newCreditCardNumber)){
			if(oldSubscriberNumber==null&&newSubscriberNumber.isEmpty()) {
				return true;
			}
			else if(oldSubscriberNumber!=null&&(oldSubscriberNumber.equals(newSubscriberNumber))) {
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
			inputFirstName.setText(String.valueOf(clickedSub.getFirstName()));
			inputLastName.setText(String.valueOf(clickedSub.getLastName()));
			inputId.setText(String.valueOf(clickedSub.getId()));
			inputPhoneNumber.setText(String.valueOf(clickedSub.getPhoneNumber()));
			inputEmailAddress.setText(String.valueOf(clickedSub.getEmailAddress()));
			inputCreditCardNumber.setPromptText(String.valueOf(clickedSub.getCreditCardNumber()));
			inputSubscriberNumber.setPromptText(String.valueOf(clickedSub.getSubscriberNumber()));

//			if(inputSubscriberNumber.equals("null")||inputSubscriberNumber.equals(null)) {
//				System.out.println("got here");
//				inputSubscriberNumber.setText("");
//			}
			inputCreditCardNumber.setDisable(false);
			inputSubscriberNumber.setDisable(false);
			SubmitBtn.setDisable(false);

		} catch (NullPointerException e) {
//			catches null pointer exceptions whenever a user clicks the header row.
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
		lst.add(NewSub);
		Request request = new Request();
		request.setPath("/UpdateSubscriber");
		request.setMethod(Method.PUT);
		request.setBody(lst);
		ClientUI.chat.accept(request);
		resetErrorStyle();
	}

//	exitClientGUI method, closes the current stage and exits the program
	@FXML
	void exitClientGUI(ActionEvent event) {
		StageSingleton.getInstance().getStage().close();
		System.exit(0);
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

//	makeRedErrorBorders method, takes a given TextField and adds a red border styling to it.
	private void makeRedErrorBorders(TextField field) {
		field.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
	}

//	setErrorLabel method, takes a given Label and String and sets the string to the label.
	private void setErrorLabel(Label label, String errorText) {
		label.setText(errorText);

	}

// resetErrorStyle method, resets available input fields and error messages back to default.
	private void resetErrorStyle() {
		ErrorCardNumLabel.setText("");
		ErrorSubscruiberNumLabel.setText("");
		inputSubscriberNumber.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000;");
		inputCreditCardNumber.setStyle("-fx-text-box-border: #000000; -fx-focus-color: #000000;");
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
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}