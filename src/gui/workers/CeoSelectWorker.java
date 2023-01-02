package gui.workers;

import client.Ceo;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Regions;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gui controller for CEO selecting workers window (based on CeoGui.workerType)
 */
public class CeoSelectWorker {

    static Stage popupDialog;
    private static String chosenID;
    private static String chosenName;
    private static String chosenRegion;
    private TableView<WorkersData> workersTable;
    private TableColumn<WorkersData, String> marketWorkerIDCol;
    private TableColumn<WorkersData, String> marketWorkerNameCol;
    private TableColumn<WorkersData, String> marketWorkerRegionCol;
    private Button goToButton;
    private WorkersData selectedWorker;

    void loadMyMarketWorkers() {
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", CeoGui.controller.bgImage);

        // Replacing top border with title based on worker type
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:
                WorkerNodesUtils.setTitle("Delivery Operators", CeoGui.controller.topBorderVBox);
                break;
            case MarketingWorker:
                WorkerNodesUtils.setTitle("Marketing Workers", CeoGui.controller.topBorderVBox);
                break;
            case ServiceOperator:
                WorkerNodesUtils.setTitle("Service Operators", CeoGui.controller.topBorderVBox);
                break;
            default:
                WorkerNodesUtils.setTitle("Operational Workers", CeoGui.controller.topBorderVBox);
                break;
        }

        // Replacing center border
        ObservableList<Node> nodes = CeoGui.controller.centerBroderVbox.getChildren();

        // Vbox for the table
        VBox marketingWorkersTableVBox = new VBox();
        marketingWorkersTableVBox.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(marketingWorkersTableVBox, Priority.ALWAYS);
        marketingWorkersTableVBox.setSpacing(40);

        // Creating Labels for instructions
        Label instructionLabel = WorkerNodesUtils.getCenteredContentLabel("Pick a worker to view their work space");
        instructionLabel.setFont(new Font("System", 18));

        // Creating TableView tasksTable
        workersTable = WorkerNodesUtils.getTableView(WorkersData.class);

        // Creating columns
        marketWorkerIDCol = new TableColumn<>("ID");
        marketWorkerNameCol = new TableColumn<>("Name");
        marketWorkerRegionCol = new TableColumn<>("Region");

        // Adding columns to marketingWorkersTable
        workersTable.getColumns().addAll(marketWorkerIDCol, marketWorkerNameCol);
        // if we are on DELIVERY or MARKETING worker we add region
        if (CeoGui.chosenWorkerType.hasRegion())
            workersTable.getColumns().addAll(marketWorkerRegionCol);
        configureTableData();
        setTableData();

        // Adding Labels, tasksTable to accountsTableVBox
        marketingWorkersTableVBox.getChildren().addAll(instructionLabel, workersTable);
        nodes.addAll(marketingWorkersTableVBox);
        setOnCellClicked();

        // Creating 'Go to picked worker' button
        goToButton = new Button();
        goToButton.setPrefHeight(30);
        goToButton.setMaxHeight(goToButton.getPrefHeight());
        goToButton.setText("Pick worker");
        goToButton.setDisable(true);
        goToButton.setOnMouseClicked(event -> onGoToClick());
        CeoGui.controller.bottomBroderVbox.getChildren().add(goToButton);
    }

    private void configureTableData() {
        marketWorkerIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        marketWorkerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (CeoGui.chosenWorkerType.hasRegion())
            marketWorkerRegionCol.setCellValueFactory(new PropertyValueFactory<>("region"));
    }

    private void setTableData() { // todo: replace with server data
        List<WorkersData> workerData = new ArrayList<>();
        // todo: do a switch for CeoGui.workerType enum to go to specific DB from server
        if (CeoGui.chosenWorkerType.hasRegion()) {
            workerData.add(new WorkersData("318882800", "Elad", Regions.North));
            workerData.add(new WorkersData("123456789", "David", Regions.South));
            workerData.add(new WorkersData("987654321", "Snake", Regions.North));
            workerData.add(new WorkersData("818181810", "Liquid", Regions.UAE));
        } else {
            workerData.add(new WorkersData("318882800", "Ratchet"));
            workerData.add(new WorkersData("123456789", "Link"));
            workerData.add(new WorkersData("987654321", "Yu"));
            workerData.add(new WorkersData("818181810", "Gon"));
        }
        workersTable.getItems().addAll(workerData);
    }

    // only after clicking on a row in table
    // the goToButton will be visible and clickable
    private void setOnCellClicked() {
        workersTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                if (goToButton.isDisable())
                    goToButton.setDisable(false);
                selectedWorker = newSelection;
            }
        });
    }

    private void onGoToClick() {
        chosenID = selectedWorker.id;
        chosenName = selectedWorker.name;
        if (CeoGui.chosenWorkerType.hasRegion()) {
            chosenRegion = selectedWorker.region;
        }

        // choosing which work space we open
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:
                openWorkerPopup("/assets/workers/RegionalDeliveryHomePage_Default.fxml");
                //openWorkerPopup(); todo: sync with DB
                break;
            case MarketingWorker:
                //openWorkerPopup(); todo: sync with DB
                break;
            case ServiceOperator:
                //openWorkerPopup(); todo: sync with DB
                break;
            case OperationalWorker:
                openWorkerPopup("/assets/workers/OperationalWorkerHomePage_Default.fxml");
                break;
            default:
                break;
        }
    }

    private void openWorkerPopup(String fxmlPath) {
        popupDialog = new Stage();
        popupDialog.initModality(Modality.APPLICATION_MODAL);
        popupDialog.initOwner(Ceo.primaryStage);

        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            setInitValuesInWorkerPopup();
            anchorPane = loader.load();
            setControllerWorkerPopup(loader);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Scene dialogScene = new Scene(anchorPane);
        popupDialog.setScene(dialogScene);
        //setTitleForScene()
        popupDialog.setX(Ceo.primaryStage.getX() + 75);
        popupDialog.setY(Ceo.primaryStage.getY() + 75);
        popupDialog.setResizable(false);
        popupDialog.show();
    }

    // set at the current worker gui the name of CEO and notify he is logged on
    private void setInitValuesInWorkerPopup() {
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:
                // todo: sync with delivery worker
                RegionalDeliveryController.isCEOLogged = true;
                break;
            case ServiceOperator:
                // todo: sync with service
                break;
            case MarketingWorker:
                // todo: sync with marketing
                break;
            case OperationalWorker:
                OperationalWorkerGui.isCEOLogged = true;
                break;
            default:
                System.out.println("oh nooooo!");
        }
    }

    private void setControllerWorkerPopup(FXMLLoader loader) {
        // get the controller of the right worker
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:
                // todo: sync with delivery worker
                RegionalDeliveryController.controller = loader.getController();
                break;
            case ServiceOperator:
                // todo: sync with service
                break;
            case MarketingWorker:
                // todo: sync with marketing
                break;
            case OperationalWorker:
                OperationalWorkerGui.controller = loader.getController();
                break;

            default:
                System.out.println("oh nooooo!");
        }
    }

    // a class to hold the workers information
    public static class WorkersData {

        private final String id;
        private final String name;
        private final String region;

        // constructor for workers without a specific region
        // like service or operational workers
        public WorkersData(String id, String name) {
            this.id = id;
            this.name = name;
            this.region = null;
        }

        // constructor for workers with a specific region
        // marketing and delivery workers
        public WorkersData(String id, String name, Regions regionType) {
            this.id = id;
            this.name = name;
            this.region = regionType.name();
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getRegion() {
            return region;
        }
    }
}
