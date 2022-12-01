package gui;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import logic.Subscriber;
import logic.SubscriberController;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SubscribersController implements Initializable {

    @FXML
    private TableView<Subscriber> subscribers;

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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    	firstNameCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("firstName"));
    	lastNameCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("lastName"));
    	idCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("id"));
    	phoneNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("phoneNumber"));
    	emailAddressCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("emailAddress"));
    	creditCardNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("creditCardNumber"));
    	subscriberNumberCol.setCellValueFactory(new PropertyValueFactory<Subscriber, String>("subscriberNumber"));
        setupTable();
    }
    

//    private boolean ValidatingTextField() {
//		String strID = InputcreditCardNumber.getText();
//		if (strID.length() != 16) {
//			ErrorLabel.setText("You must enter 16 numbers");
//			InputcreditCardNumber.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
//
//			return false;
//		} else {
//
//			for (char c : strID.toCharArray()) {
//				if (!Character.isDigit(c)) {
//					ErrorLabel.setText("Error: id contains only numbers..");
//					InputcreditCardNumber.setStyle("-fx-text-box-border: #B22222; -fx-focus-color: #B22222;");
//					return false;
//				}
//			}
//		}
//		ErrorLabel.setText("");
//		
//		InputcreditCardNumber.setStyle("-fx-text-box-border: #6e6b6b; -fx-focus-color: #6e6b6b;");
//		return true;
//	}

    @FXML
    void submit(ActionEvent event) {
        ObservableList<Subscriber> currentTableData = subscribers.getItems();
        String currentSubId = Inputid.getText();

        for (Subscriber subscriber : currentTableData) {
            if(subscriber.getId().equals(currentSubId)) {
            	subscriber.setFirstName(InputfirstName.getText());
            	subscriber.setLastName(InputlastName.getText());
            	subscriber.setId(Inputid.getText());
            	subscriber.setPhoneNumber(InputphoneNumber.getText());
            	subscriber.setEmailAddress(InputemailAddress.getText());
            	
            	
            	subscriber.setCreditCardNumber(InputcreditCardNumber.getText());
            	subscriber.setSubscriberNumber(InputsubscriberNumber.getText());
        
            	//send to server
            	SubscriberController.editSubscriber(subscriber);
                subscribers.setItems(currentTableData);
                subscribers.refresh();
                break;
            }
        }
    }

    @FXML
    void rowClicked(MouseEvent event) {
    	Subscriber clickedSub = subscribers.getSelectionModel().getSelectedItem();

    	InputfirstName.setText(String.valueOf(clickedSub.getFirstName()));
    	InputlastName.setText(String.valueOf(clickedSub.getLastName()));
    	Inputid.setText(String.valueOf(clickedSub.getId()));
    	InputphoneNumber.setText(String.valueOf(clickedSub.getPhoneNumber()));
    	InputemailAddress.setText(String.valueOf(clickedSub.getEmailAddress()));
    	InputcreditCardNumber.setText(String.valueOf(clickedSub.getCreditCardNumber()));
    	InputsubscriberNumber.setText(String.valueOf(clickedSub.getSubscriberNumber()));
    
    }

    private void setupTable(){
		List<Subscriber> arr = null;
		try {
			arr = SubscriberController.getAllSubscribers();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for(Subscriber sub : arr)
			subscribers.getItems().add(sub);
		
    }


}