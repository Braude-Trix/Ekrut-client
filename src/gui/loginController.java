package gui;



import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class loginController {
	public static OLController OLcon;
	public static EKController EKcon;
	public static Scene scene;
	
    @FXML
    private Button loginBtn;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtUsername;
    
    @FXML
    void moveHomePage(ActionEvent event) throws Exception {
		Stage stage = StageSingleton.getInstance().getStage();
		OLcon = new OLController();	
		OLcon.start(stage);
//		EKcon = new EKController();
//		EKcon.start(stage);
    }
    
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/login.fxml"));
				
		Scene scene = new Scene(root);
		this.scene = scene;
		scene.getStylesheets().add(getClass().getResource("/styles/loginForm.css").toExternalForm());
		primaryStage.setTitle("Login");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
	}
}
