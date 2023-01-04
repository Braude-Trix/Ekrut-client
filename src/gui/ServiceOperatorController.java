package gui;

import client.Client;
import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Method;
import models.Request;
import models.User;
import utils.Util;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * @author Badihi
 * This class resposable on upgrading users to client or subscriber by the serivce operator
 */
public class ServiceOperatorController implements Initializable {

    public static Scene scene;
    @FXML
    private VBox UserData;

    @FXML
    private Label NotOnlyDigits;

    @FXML
    private Label CouldntFineUser;

    @FXML
    private TextField IDField;

    @FXML
    private ImageView bgImage;

    @FXML
    private Button checkIdButton;

    @FXML
    private Button confirmDeliveriesBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private Button pendingDeliveriesBtn;

    @FXML
    private Label userNameLabel;

    @FXML
    private Label userRank;


    /**
     * @author Badihi
     * This function load the main screen
     */

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {

        loadScreen();
    }
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/assets/ServiceOperator.fxml"));

        Scene scene = new Scene(root);
        this.scene = scene;
       // scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
        primaryStage.setTitle("Service Operator");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setOnCloseRequest(e -> {
            try {
                Util.forcedExit();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        });
    }


    private String getWorkerName()
    {
        return "Mark Tzukerberg";
    }

    private String getWorkerRank()
    {
        return "CEO OF FACEBOOK";
    }



    private void loadScreen()
    {
        userNameLabel.setText(getWorkerName());
        userRank.setText(getWorkerRank());

        checkIdButton.setOnMouseClicked(event -> IDFieldInserted());
    }

    private boolean isPending(Integer id)
    {
        List<Object> userIdList = new ArrayList<>();
        userIdList.add(id);
        Request request = new Request();
        request.setPath("/checkIfUserPending");
        request.setMethod(Method.GET);
        request.setBody(userIdList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> status = Client.resFromServer.getBody();
                if(status.get(0).equals(false))
                {
                    return false;
                }

                return true;
            default:
                System.out.println("Some error occurred");
        }
        return false;


    }

    private void upgradeToClient(User user,Button button)
    {

        List<Object> userIdList = new ArrayList<>();
        userIdList.add(user.getId());
        Request request = new Request();
        request.setPath("/upgradeUserToClient");
        request.setMethod(Method.POST);
        request.setBody(userIdList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                button.setText("Pending To Upgrade");
                successPop(user,"Request Submitted Successfully");
                button.setDisable(true);
                List<Object> users = Client.resFromServer.getBody();
                return;

            default:
                System.out.println("Some error occurred");
        }



    }
    private void successPop(User user,String message) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        ImageView image = new ImageView(new Image("assets/checked.png"));
        image.setFitHeight(100);
        image.setFitWidth(100);
        alert.setGraphic(image);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getScene().getStylesheets().add("styles/SuccessPopUp.css");
        stage.setWidth(40);
        stage.setHeight(40);
        alert.show();
    }

    private void upgradeToSub(User user,Button button)
    {


        List<Object> userIdList = new ArrayList<>();
        userIdList.add(user.getId());
        Request request = new Request();
        request.setPath("/upgradeClientToSubscriber");
        request.setMethod(Method.PUT);
        request.setBody(userIdList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                button.setText("Already Subscriber");
                successPop(user,"Success!\n"+ user.getFirstName()+" "+user.getLastName()+" upgraded to subscriber");
                button.setDisable(true);
                List<Object> users = Client.resFromServer.getBody();
                return;

            default:
                System.out.println("Some error occurred");
        }

    }

    private List<Object> findUserStatus(Integer userId){
        List<Object> userIdList = new ArrayList<>();
        userIdList.add(userId);
        Request request = new Request();
            request.setPath("/requestUserStatus");
            request.setMethod(Method.GET);
            request.setBody(userIdList);
            ClientUI.chat.accept(request);// sending the request to the server.
            switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> status = Client.resFromServer.getBody();

                return status;
            default:
                System.out.println("Some error occurred");
        }
            return null;
}

    private List<Object> requestUsersWithTheirStatus() {
        Request request = new Request();
        request.setPath("/requestUsers");
        request.setMethod(Method.GET);
        request.setBody(null);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> users = Client.resFromServer.getBody();

                return users;
            default:
                System.out.println("Some error occurred");
        }
        return null;
    }


    
    private List<User> getAllUsers()
    {
        List<User> users = new ArrayList<>();
        List<Object> importedUsers = requestUsersWithTheirStatus();
        for(Object user : importedUsers)
            users.add((User)user);
        return users;
        
    }

    private User getUser(int ID)
    {
        List<User> users = getAllUsers();
        for(User user : users)
        {
            if(user.getId() == ID)
            {
                return user;
            }
        }
        return null;

    }
    private User getUserAfterValidations()
    {
        UserData.getChildren().clear();
        User user;
        int ID = 0;
        String strID = IDField.getText();
        NotOnlyDigits.setVisible(false);
        CouldntFineUser.setVisible(false);
       if(strID.length() > "1111111111".length())
       {
           CouldntFineUser.setVisible(true);
           return null;
       }

        try {
            ID = Integer.parseInt(strID);
        }
        catch (Exception e)
        {
            NotOnlyDigits.setVisible(true);
            return null;
        }
        user = getUser(ID);
        if(user == null)
        {
            CouldntFineUser.setVisible(true);
            return null;
        }
        return user;

    }
    private void presentUser()
    {

    }

    private void IDFieldInserted()
    {
        User user = getUserAfterValidations();
        if(user == null)
        {
            return;
        }
        List<Object> userStatus = findUserStatus(user.getId());
        String status = (String)userStatus.get(0);
        UserData.getChildren().add(new Label("ID: " + user.getId()));
        UserData.getChildren().add(new Label(" "));
        UserData.getChildren().add(new Label("First Name: " + user.getFirstName()));
        UserData.getChildren().add(new Label(" "));
        UserData.getChildren().add(new Label("Last Name: " +user.getLastName()));
        UserData.getChildren().add(new Label(" "));
        UserData.getChildren().add(new Label("Email: "+user.getEmail()));
        UserData.getChildren().add(new Label(" "));
        UserData.getChildren().add(new Label("Phone Number: "+user.getPhoneNumber()));
        UserData.getChildren().add(new Label(" "));
        UserData.getChildren().add(new Label("Credit Card: "+user.getCreditCardNumber()));
        UserData.getChildren().add(new Label(" "));


        if(status.equals("User"))
        {
            Button userButton = new Button("Upgrade To Client");
            boolean pending = isPending(user.getId());
            if(pending)
            {
                userButton.setText("Pending To Upgrade");
                userButton.setDisable(true);
                UserData.getChildren().add(userButton);
                return;
            }

            UserData.getChildren().add(userButton);
            userButton.setOnMouseClicked(event -> upgradeToClient(user,userButton));
        }
        else if(status.equals("Client"))
        {
            Button clientButton = new Button("Upgrade To Subscriber");
            UserData.getChildren().add(clientButton);
            clientButton.setOnMouseClicked(event -> upgradeToSub(user,clientButton));
        }
        else{
            Button subButton = new Button("Already Subscriber");
            UserData.getChildren().add(subButton);
            subButton.setDisable(true);
        }
    }

}