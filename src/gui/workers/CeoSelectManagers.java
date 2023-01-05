package gui.workers;

import client.Ceo;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Regions;
import models.Worker;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.util.List;

/**
 * Gui controller for CEO selecting managers window
 */
public class CeoSelectManagers {

    public static Stage popupDialog;
    private Button managerNorthBtn;
    private Button managerSouthBtn;
    private Button managerUAEBtn;
    private Button managerMarketingBtn;
    private static ManagerType managerType;
    private List<Worker> managerList;
    private List<Worker> MakretManagerList;
    private Worker selectedWorker;

    private enum ManagerType {
        NORTH, SOUTH, UAE, MARKET
    }

    void loadMyManagers() {
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", CeoGui.controller.bgImage);
        // Replacing top border
        WorkerNodesUtils.setTitle("Managers", CeoGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = CeoGui.controller.centerBroderVbox.getChildren();

        // Vbox for the managers buttons
        VBox managersBtnVBox = new VBox();
        managersBtnVBox.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(managersBtnVBox, Priority.ALWAYS);
        managersBtnVBox.setSpacing(75);

        // Creating Labels for instructions
        Label aboveButtonsLabel = WorkerNodesUtils.getCenteredContentLabel("Pick a manager to view their work space");
        aboveButtonsLabel.setFont(new Font("System", 18));

        // Creating 1st HBoxs for buttons
        HBox northAndSouthHBox = new HBox();
        northAndSouthHBox.setAlignment(Pos.TOP_CENTER);
        northAndSouthHBox.setSpacing(25);

        // Creating buttons for managers
        managerNorthBtn = WorkerNodesUtils.createWide200Button("Regional Manager North");
        managerNorthBtn.setOnMouseClicked(event -> {
            managerType = ManagerType.NORTH;
            goToRegionManager();
        });

        managerSouthBtn = WorkerNodesUtils.createWide200Button("Regional Manager South");
        managerSouthBtn.setOnMouseClicked(event -> {
            managerType = ManagerType.SOUTH;
            goToRegionManager();
        });

        // adding buttons to 1st HBox
        northAndSouthHBox.getChildren().addAll(managerNorthBtn, managerSouthBtn);

        // Creating 2nd HBoxs for buttons
        HBox uaeAndMarketingHBox = new HBox();
        uaeAndMarketingHBox.setAlignment(Pos.TOP_CENTER);
        uaeAndMarketingHBox.setSpacing(25);

        // Creating buttons for managers
        managerUAEBtn = WorkerNodesUtils.createWide200Button("Regional Manager UAE");
        managerUAEBtn.setOnMouseClicked(event -> {
            managerType = ManagerType.UAE;
            
            goToRegionManager();
        });

        managerMarketingBtn = WorkerNodesUtils.createWide200Button("Marketing Manager");
        //managerMarketingBtn.setOnMouseClicked(event -> onGoToMarket());
        managerMarketingBtn.setOnMouseClicked(event -> {
        	managerType = ManagerType.MARKET;
        	goToMarketingManager();
        });

        // adding buttons to 2nd HBox
        uaeAndMarketingHBox.getChildren().addAll(managerUAEBtn, managerMarketingBtn);

        // adding instructions and HBoxs to centerBroderVbox
        managersBtnVBox.getChildren().addAll(aboveButtonsLabel, northAndSouthHBox, uaeAndMarketingHBox);

        // add VBox to centerBroderVbox
        nodes.addAll(managersBtnVBox);
    }
    
    private void goToMarketingManager() {
    	openMangerPopup("/assets/workers/MarketingManagerWindow.fxml");
    }

    private void goToRegionManager() {
        openMangerPopup("/assets/workers/ManagerHomePage_Default.fxml");
    }

    private void openMangerPopup(String fxmlPath) {
        popupDialog = new Stage();
        popupDialog.initModality(Modality.APPLICATION_MODAL);
        popupDialog.initOwner(Ceo.primaryStage);

        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            setInitValuesInManagerPopup();
            anchorPane = loader.load();
            setControllerManagerPopup(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene dialogScene = new Scene(anchorPane);
        popupDialog.setScene(dialogScene);

        popupDialog.setX(Ceo.primaryStage.getX() + 75);
        popupDialog.setY(Ceo.primaryStage.getY() + 75);
        popupDialog.setResizable(false);
        popupDialog.show();
    }

    private void setInitValuesInManagerPopup() {
        if (managerType == ManagerType.MARKET) {
            MarketingManagerController.isCEOLogged = true;
            popupDialog.setTitle("CEO - Marketing Manager");
        } else {
            RegionalManagerGui.isCEOLogged = true;
            popupDialog.setTitle("CEO - Regional Manager");
        }

        switch (managerType) {
            case NORTH:
                RegionalManagerGui.region = Regions.North;
                break;
            case SOUTH:
                RegionalManagerGui.region = Regions.South;
                break;
            case UAE:
                RegionalManagerGui.region = Regions.UAE;
                break;
            case MARKET:
                break;
        }
    }

    private void setControllerManagerPopup(FXMLLoader loader) {
        // in case of any regional manger, get RegionalGui.controller
        if (managerType == ManagerType.MARKET) {
            MarketingManagerController.controller = loader.getController();
        } else
            RegionalManagerGui.controller = loader.getController();
    }
}
