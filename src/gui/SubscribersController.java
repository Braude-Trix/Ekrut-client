package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import models.Method;
import models.Request;
import models.Response;
import models.ResponseCode;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.ChatClient;
import client.ClientUI;
import models.Subscriber;

public class SubscribersController implements Initializable {
	private static final int CreditCardLength = 16;

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
	private TextField InputfirstName;

	@FXML
	private TextField InputlastName;

	@FXML
	private TextField Inputid;

	@FXML
	private TextField InputphoneNumber;

	@FXML
	private TextField InputemailAddress;

	@FXML
	private TextField InputcreditCardNumber;

	@FXML
	private TextField InputsubscriberNumber;

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
		InputcreditCardNumber.setStyle("-fx-text-box-border: #d9d7d7; -fx-focus-color: #d9d7d7;");
		InputsubscriberNumber.setStyle("-fx-text-box-border: #d9d7d7; -fx-focus-color: #d9d7d7;");
		requestAllSubscribers();
	}

	@FXML
	void submit(ActionEvent event) {
		ObservableList<Subscriber> currentTableData = subscribersTableView.getItems();
		String currentSubId = Inputid.getText();

		for (Subscriber subscriber : currentTableData) {
			if (subscriber.getId().equals(currentSubId)) {
				subscriber.setFirstName(InputfirstName.getText());
				subscriber.setLastName(InputlastName.getText());
				subscriber.setId(Inputid.getText());
				subscriber.setPhoneNumber(InputphoneNumber.getText());
				subscriber.setEmailAddress(InputemailAddress.getText());
				subscriber.setCreditCardNumber(InputcreditCardNumber.getText());
				subscriber.setSubscriberNumber(InputsubscriberNumber.getText());

				// send to server
				editSubscriber(subscriber);
//				subscribers.setItems(currentTableData);
//				subscribers.refresh();
				break;
			}
		}
	}

	@FXML
	void rowClicked(MouseEvent event) {
		Subscriber clickedSub = subscribersTableView.getSelectionModel().getSelectedItem();

		InputfirstName.setText(String.valueOf(clickedSub.getFirstName()));
		InputlastName.setText(String.valueOf(clickedSub.getLastName()));
		Inputid.setText(String.valueOf(clickedSub.getId()));
		InputphoneNumber.setText(String.valueOf(clickedSub.getPhoneNumber()));
		InputemailAddress.setText(String.valueOf(clickedSub.getEmailAddress()));
		InputcreditCardNumber.setText(String.valueOf(clickedSub.getCreditCardNumber()));
		InputsubscriberNumber.setText(String.valueOf(clickedSub.getSubscriberNumber()));
		InputcreditCardNumber.setDisable(false);
		InputsubscriberNumber.setDisable(false);
		SubmitBtn.setDisable(false);
	}

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



	public void requestAllSubscribers() {
		Request request = new Request();
		request.setPath("/AllSubscribers");
		request.setMethod(Method.GET);
		request.setBody(null);
		ClientUI.chat.accept(request);
	//	ChatClient.resFromServer.setCode(ResponseCode.DB_ERROR);//used for bugs testing
		switch (ChatClient.resFromServer.getCode()) {
		case OK:
			// response is fine
			setUpTable(ChatClient.resFromServer);
			break;
		default:
			handleResponseError();
			break;
		}

	}

	private void handleResponseError() {
		
		subscribersTableView.getItems().clear();
		System.out.println(subscribersTableView);
		System.out.println("response error: " + ChatClient.resFromServer.getDescription());
		Label errorLbl = new Label("Can't show subscribers..\n" + ChatClient.resFromServer.getDescription());
		errorLbl.setStyle("-fx-text-fill : #FF3547;-fx-font-weight: bold;-fx-font-family: Inconsolata:700; -fx-font-size: 25");
		subscribersTableView.setPlaceholder(errorLbl);
		

	}

	public void editSubscriber(Subscriber NewSub) {
		boolean isValidateFlag = true;
		if (!ValidatingTextField(InputsubscriberNumber, "subscruiber number", true, ErrorSubscruiberNumLabel)) {
			isValidateFlag = false;
		}
		if (!ValidatingTextField(InputcreditCardNumber, "credit card number", false, ErrorCardNumLabel)) {
			isValidateFlag = false;
		}
		if (!isValidateFlag) {
			return;
		}
		List<Object> lst = new ArrayList<>();
		lst.add(NewSub);
		System.out.println(NewSub.toString());
		Request request = new Request();
		request.setPath("/UpdateSubscriber");
		request.setMethod(Method.PUT);
		request.setBody(lst);
		ClientUI.chat.accept(request);
		ErrorCardNumLabel.setText("");
		ErrorSubscruiberNumLabel.setText("");
		InputsubscriberNumber.setStyle("-fx-text-box-border: #d9d7d7; -fx-focus-color: #d9d7d7;");
		InputcreditCardNumber.setStyle("-fx-text-box-border: #d9d7d7; -fx-focus-color: #d9d7d7;");
	}

	@FXML
	void ExitClientGUI(ActionEvent event) {
		StageSingleton.getInstance().getStage().close();
		System.exit(0);
	}

	private boolean ValidatingTextField(TextField textField, String errorSubject, boolean canBeNull, Label errorLabel) {
		if (canBeNull) {
			if (textField.getText().equals(""))// special check, sub number can be null.
				return true;
		}

		String str = textField.getText();
		if (errorSubject.equals("credit card number")) {
			if (str.length() != CreditCardLength) {
				errorLabel.setText("Error: " + errorSubject + " card must be 16 numbers..");
				textField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
				return false;
			}
		}
		if (!isOnlyDigits(str)) {
			errorLabel.setText("Error: " + errorSubject + " contains only numbers..");
			textField.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
			return false;
		}

		errorLabel.setText("");
		textField.setStyle("-fx-text-box-border: #6e6b6b; -fx-focus-color: #6e6b6b;");
		return true;
	}

	private boolean isOnlyDigits(String str) {
		for (char c : str.toCharArray()) {
			if (!Character.isDigit(c)) {
				return false;
			}
		}
		return true;
	}

}