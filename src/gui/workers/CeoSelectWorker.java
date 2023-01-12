package gui.workers;

import client.Client;
import client.ClientUI;
import gui.StageSingleton;
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
import models.Method;
import models.Request;
import models.Worker;
import models.WorkerType;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Gui controller for CEO selecting workers window (based on CeoGui.workerType)
 */
public class CeoSelectWorker {

    public static Stage popupDialog;
    private TableView<WorkersData> workersTable;
    private TableColumn<WorkersData, String> marketWorkerIDCol;
    private TableColumn<WorkersData, String> marketWorkerNameCol;
    private TableColumn<WorkersData, String> marketWorkerRegionCol;
    private Button goToButton;
    private WorkersData selectedWorker;
    
    private List<Worker> workerSet;

    void loadMyWorkers() {
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
        workerSet = new ArrayList<>();
        workerSet = getWorkersList(CeoGui.chosenWorkerType);
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
    
    private List<Worker> getWorkersList(WorkerType workerType) {
    	List<Worker> workerslist = new ArrayList<>();
    	
    	List<Object> workerReq = new ArrayList<>();
    	workerReq.add(workerType);
    	Request request = new Request();
    	request.setPath("/workers/getWorkersByType");
    	request.setMethod(Method.GET);
    	request.setBody(workerReq);
    	ClientUI.chat.accept(request);
    	
    	switch(Client.resFromServer.getCode()) {
    	case OK:
    		List<Object> myWorkers = Client.resFromServer.getBody();
    		if (myWorkers == null)
    			return null;
        	for (Object worker : myWorkers) {
        		if(worker instanceof Worker) {
        			Worker currentWorker = (Worker) worker;
        			workerslist.add(currentWorker);
        		}
        	}
        	return workerslist;
    	default:
    		return null;
    		
    	}
    }

    private void configureTableData() {
        marketWorkerIDCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        marketWorkerNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        if (CeoGui.chosenWorkerType.hasRegion())
            marketWorkerRegionCol.setCellValueFactory(new PropertyValueFactory<>("region"));
    }

    private void setTableData() { 
        List<WorkersData> workerData = new ArrayList<>();
        for(Worker worker : workerSet) {
    		workerData.add(new WorkersData(worker));
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

        // choosing which work space we open
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:
                openWorkerPopup("/assets/workers/fxmls/RegionalDeliveryHomePage_Default.fxml");
                break;
            case MarketingWorker:
            	openWorkerPopup("/assets/workers/fxmls/MarketingWorkerWindow.fxml");
                break;

            case ServiceOperator:
            	openWorkerPopup("/assets/workers/fxmls/ServiceOperator.fxml");
                break;

            case OperationalWorker:
                openWorkerPopup("/assets/workers/fxmls/OperationalWorkerHomePage_Default.fxml");
                break;
            default:
                break;
        }
    }

    private void openWorkerPopup(String fxmlPath) {
        Stage primaryStage = StageSingleton.getInstance().getStage();
        popupDialog = new Stage();
        popupDialog.initModality(Modality.APPLICATION_MODAL);
        popupDialog.initOwner(primaryStage);

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

        popupDialog.setX(primaryStage.getX() + 75);
        popupDialog.setY(primaryStage.getY() + 75);
        popupDialog.setResizable(false);
        popupDialog.setHeight(primaryStage.getHeight());
        popupDialog.setWidth(primaryStage.getWidth());
        popupDialog.show();
    }

    // set at the current worker gui the name of CEO and notify he is logged on
    private void setInitValuesInWorkerPopup() {
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:

                RegionalDeliveryController.isCEOLogged = true;
                RegionalDeliveryController.workerAccessByCeo = selectedWorker.worker;
                popupDialog.setTitle("CEO - Delivery operator");
                break;
            case ServiceOperator:

            	ServiceOperatorController.isCEOLogged = true;
            	ServiceOperatorController.workerAccessByCeo = selectedWorker.worker;
            	popupDialog.setTitle("CEO - Service operator");
                break;
            case MarketingWorker:

            	MarketingWorkerWindowController.isCEOLogged = true;
            	MarketingWorkerWindowController.workerAccessByCeo = selectedWorker.worker;
            	popupDialog.setTitle("CEO - Marketing worker");
                break;
            case OperationalWorker:
                OperationalWorkerGui.isCEOLogged = true;
                OperationalWorkerGui.workerAccessByCeo = selectedWorker.worker;
                popupDialog.setTitle("CEO - Operational worker");
                break;
            default:
                System.out.println("workerType was not defined");
        }
    }

    private void setControllerWorkerPopup(FXMLLoader loader) {
        // get the controller of the right worker
        switch (CeoGui.chosenWorkerType) {
            case RegionalDelivery:
                RegionalDeliveryController.controller = loader.getController();
                break;
            case ServiceOperator:
                break;
            case MarketingWorker:
            	MarketingWorkerWindowController.controller = loader.getController();
                break;
            case OperationalWorker:
                OperationalWorkerGui.controller = loader.getController();
                break;

            default:
                System.out.println("workerType was not defined");
        }
    }

    // a class to hold the workers information
    public static class WorkersData {
    	
    	private final Worker worker;
        private final String id;
        private final String name;
        private final String region;

        // constructor for workers without a specific region
        // like service or operational workers
        
        public WorkersData(Worker worker) {
        	this.worker = worker;
        	id = worker.getId().toString();
        	name = worker.getFirstName() + " " + worker.getLastName();
        	if(worker.getRegion() != null)
        		region = worker.getRegion().name();
        	else
        		region = null;
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
        
        public Worker getWorker() {
        	return worker;
        }
    }
}
