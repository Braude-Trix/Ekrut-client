package gui.workers;

import client.Client;
import client.ClientUI;
import gui.LoginController;
import gui.SelectOptionWorkerOrCustomer;
import gui.StageSingleton;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import logic.Messages;
import models.InventoryFillTask;
import models.Method;
import models.Product;
import models.ProductInMachine;
import models.Regions;
import models.Request;
import models.ResponseCode;
import models.StatusInMachine;
import models.TaskStatus;
import models.Worker;
import utils.ColorsAndFonts;
import utils.Utils;
import utils.WorkerNodesUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import static utils.Utils.forcedExit;
import static utils.Utils.isBlank;

/**
 * Gui controller for presenting Operational Worker window
 */
public class OperationalWorkerGui implements Initializable {
    public static OperationalWorkerGui controller;
    public static Regions chosenRegion;
    public static String chosenMachine;
    public static boolean isCEOLogged = false;
    private Worker worker = (Worker) LoginController.user;
	public static Worker workerAccessByCeo = null;

    @FXML
    private ImageView bgImage;

    @FXML
    private Label userNameLabel;

    @FXML
    private Button myTasksBtn;

    @FXML
    private Button machineInventoryBtn;

    @FXML
    private Button logoutBtn;

    @FXML
    private VBox topBorderVBox;

    @FXML
    private VBox centerBroderVbox;

    @FXML
    private VBox bottomBroderVbox;

    @FXML
    private Label userRoleLabel;
    @FXML
    private Button backBtn;

    /**
     * Initializing Operational Worker window
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        if (isCEOLogged) {
            logoutBtn.setVisible(false);
            worker = workerAccessByCeo;
        } else {
            setBackBtnIfExist();
        }
    	// setting username
        WorkerNodesUtils.setUserName(userNameLabel, worker);
        WorkerNodesUtils.setRole(userRoleLabel, worker.getType());

        myTasksBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            myTasksBtn.setDisable(true);
            new MyTasks().loadMyTasks();
        });
        machineInventoryBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            machineInventoryBtn.setDisable(true);
            new MachineInventory().loadMachineInventory();
        });
        logoutBtn.setOnMouseClicked((event) -> {
            try {
                Utils.genericLogOut(getClass());
            } catch (Exception e) {
            }
        });
    }
    private void setBackBtnIfExist() {
		if (LoginController.customerAndWorker != null) {
			backBtn.setVisible(true);
		}

	}
    /**
	 * This method is the actual back button, shoots onAction and changes the stage to be the selection page.
	 * @param event - the current event when clicking.
	 */
	@FXML
	void back(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(SelectOptionWorkerOrCustomer.scene);
		stage.show();
	}

    private void enableAll() {
        myTasksBtn.setDisable(false);
        machineInventoryBtn.setDisable(false);
    }

    private void clearBorderPane() {
        topBorderVBox.getChildren().clear();
        centerBroderVbox.getChildren().clear();
        bottomBroderVbox.getChildren().clear();
    }

    private class MyTasks {
        private TableView<InventoryFillTask> openedTasksTable;
        private TableColumn<InventoryFillTask, String> dateColumn;
        private TableColumn<InventoryFillTask, String> regionColumn;
        private TableColumn<InventoryFillTask, String> machineIdColumn;
        private TableColumn<InventoryFillTask, String> machineNameColumn;
        private TableColumn<InventoryFillTask, String> statusColumn;
        private Button goToButton;
        private Button closeTaskButton;
        private InventoryFillTask selectedTask;

        private void loadMyTasks() {
            chosenRegion = null;
            chosenMachine = null;
            // Replacing background
            WorkerNodesUtils.setBackground("/assets/workers/InventoryReportMenu.jpg", bgImage);

            // Replacing top border
            WorkerNodesUtils.setTitle("My Opened Tasks", topBorderVBox);
            // Replacing center border
            ObservableList<Node> nodes = centerBroderVbox.getChildren();

            // Vbox for the tasks table
            VBox tasksTableVBox = new VBox();
            tasksTableVBox.setAlignment(Pos.TOP_CENTER);
            VBox.setVgrow(tasksTableVBox, Priority.ALWAYS);
            tasksTableVBox.setPadding(new Insets(20, 0, 5, 0));

            HBox titleHBox = new HBox();
            titleHBox.setPadding(new Insets(0, 32, 0, 26));
            titleHBox.setAlignment(Pos.CENTER);
            // Creating Labels for instructions
            Label aboveTableLabel = WorkerNodesUtils.getCenteredContentLabel("Choose a task to work on");

            Region regionBetweenSpacer = new Region();
            HBox.setHgrow(regionBetweenSpacer, Priority.ALWAYS);

            // Creating 'refresh' table button
            Button refreshButton = new Button("Refresh");
            refreshButton.setPrefHeight(30);
            refreshButton.setMaxHeight(refreshButton.getPrefHeight());
            refreshButton.setOnMouseClicked(event -> onRefreshClick());
            titleHBox.getChildren().addAll(aboveTableLabel, regionBetweenSpacer, refreshButton);

            // Creating TableView tasksTable
            openedTasksTable = WorkerNodesUtils.getTableView(InventoryFillTask.class);

            // Creating columns
            dateColumn = new TableColumn<>("Creation Date");
            regionColumn = new TableColumn<>("Region");
            machineIdColumn = new TableColumn<>("Machine ID");
            machineNameColumn = new TableColumn<>("Machine Name");
            statusColumn = new TableColumn<>("Status");
            // Adding columns to accountsTable
            openedTasksTable.getColumns().addAll(dateColumn, regionColumn, machineIdColumn,
                    machineNameColumn, statusColumn);

            // Adding Labels, tasksTable to accountsTableVBox
            tasksTableVBox.getChildren().addAll(titleHBox, openedTasksTable);
            nodes.addAll(tasksTableVBox);
            setOnCellClicked();

            // Creating 'Go to machine inventory' button
            goToButton = new Button("Go to machine inventory");
            goToButton.setPrefHeight(30);
            goToButton.setMaxHeight(goToButton.getPrefHeight());
            goToButton.setDisable(true);
            goToButton.setOnMouseClicked(event -> onGoToClick());

            // creating the 'close task' btn
            closeTaskButton = WorkerNodesUtils.createWide85Button("Close task");
            closeTaskButton.setVisible(false);
            closeTaskButton.setOnMouseClicked(event -> onCloseTaskClick());

            bottomBroderVbox.getChildren().addAll(goToButton, closeTaskButton);

            configureTableData();
            setTableData();
        }

        private void configureTableData() {
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
            regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
            machineIdColumn.setCellValueFactory(new PropertyValueFactory<>("machineId"));
            machineNameColumn.setCellValueFactory(new PropertyValueFactory<>("machineName"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        private void setTableData() {
            List<InventoryFillTask> tasksData = new ArrayList<>();
            requestOpenedTasks(tasksData);
            openedTasksTable.getItems().clear();
            openedTasksTable.getItems().addAll(tasksData);
        }

        private void onRefreshClick() {
            setTableData();
        }

        private void setOnCellClicked() {
            openedTasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    if (goToButton.isDisable())
                        goToButton.setDisable(false);
                    selectedTask = newSelection;

                    closeTaskButton.setVisible(selectedTask.getStatus() == TaskStatus.IN_PROGRESS);
                }
            });
        }

        private void onGoToClick() {
            chosenRegion = selectedTask.getRegion();
            chosenMachine = selectedTask.getMachineName();

            enableAll();
            clearBorderPane();
            machineInventoryBtn.setDisable(true);
            new MachineInventory().loadMachineInventory();
        }

        private void onCloseTaskClick() {
            if (bottomBroderVbox.getChildren().size() >= 3)
                bottomBroderVbox.getChildren().remove(2);
            Label msgLabel;
            selectedTask.setStatus(TaskStatus.CLOSED);
            Integer managerID;
            if(getRegionalIdByRegion(selectedTask.getRegion())) {
            	managerID = (Integer) Client.resFromServer.getBody().get(0);
            }
            else {
            	msgLabel = WorkerNodesUtils.getCenteredContentLabel(Client.resFromServer.getDescription());
            	bottomBroderVbox.getChildren().add(msgLabel);
            	return;
            }
            if (postNewTaskStatus(selectedTask)) { // if request is ok
                msgLabel = WorkerNodesUtils.getCenteredContentLabel("Task for machine " +
                        selectedTask.getMachineName() + " was closed successfully");
                openedTasksTable.getItems().remove(selectedTask);
                Messages.writeNewMsgToDB("Task for machine: " + selectedTask.getMachineName() + "\nis done by Operational worker: " +
                worker.getFirstName() + " " + worker.getLastName(), worker.getId(), managerID);

                goToButton.setDisable(true);
                closeTaskButton.setVisible(false);
                onRefreshClick();
            } else { // if some error
                msgLabel = WorkerNodesUtils.getCenteredContentLabel("Task for machine " +
                        selectedTask.getMachineName() + " couldn't close, Server error occurred");
            }
            bottomBroderVbox.getChildren().add(msgLabel);
        }
        
        private boolean getRegionalIdByRegion(Regions taskRegion) {
        	List<Object> getIdRegion = new ArrayList<>();
        	getIdRegion.add(taskRegion);
        	Request request = new Request();
        	request.setPath("/workers/getRegionalManagerIdByRegion");
        	request.setMethod(Method.GET);
        	request.setBody(getIdRegion);
        	ClientUI.chat.accept(request);
        	return Client.resFromServer.getCode() == ResponseCode.OK;
        }
        
        private boolean postNewTaskStatus(InventoryFillTask task) {
            List<Object> tasks = new ArrayList<>();
            tasks.add(task);
            Request request = new Request();
            request.setPath("/operationalWorker/setInventoryTask");
            request.setMethod(Method.PUT);
            request.setBody(tasks);
            ClientUI.chat.accept(request);
            return Client.resFromServer.getCode() == ResponseCode.OK;
        }
    }

    private class MachineInventory {
        private final String FORM_VALIDATION_IS_OK = "OK";
        private final int MACHINE_CAPACITY = 70;
        private TableView<ProductInMachineData> productsTable;
        private TableColumn<ProductInMachineData, ImageView> imageColumn;
        private TableColumn<ProductInMachineData, String> productIdColumn;
        private TableColumn<ProductInMachineData, String> productNameColumn;
        private TableColumn<ProductInMachineData, Integer> currentAmountColumn;
        private TableColumn<ProductInMachineData, TextField> newAmountColumn;
        private ComboBox<String> regionCombobox;
        private ComboBox<String> machineCombobox;
        private Button refreshBtn;
        private HBox instructionsHBox;
        private Button updateBtn;
        private List<Product> products = new ArrayList<>();
        private List<ProductInMachine> productsAmount = new ArrayList<>();
        private List<InventoryFillTask> tasksData;
        private int chosenMachineThreshold;
        private Label yellow_below_label;


        private void loadMachineInventory() {
            // Replacing background
            WorkerNodesUtils.setBackground("/assets/workers/InventoryReportMenu.jpg", bgImage);
            // Replacing top border
            WorkerNodesUtils.setTitle("Machine Inventory", topBorderVBox);

            // Replacing center border
            ObservableList<Node> nodes = centerBroderVbox.getChildren();

            // Vbox for the inventory table
            VBox inventoryTableVBox = new VBox();
            inventoryTableVBox.setAlignment(Pos.TOP_CENTER);
            VBox.setVgrow(inventoryTableVBox, Priority.ALWAYS);
            inventoryTableVBox.setPadding(new Insets(10, 0, 5, 0));
            inventoryTableVBox.setSpacing(10);

            // setting center selection of region and machine vbox
            VBox selectionVbox = new VBox();
            selectionVbox.setAlignment(Pos.CENTER);
            selectionVbox.setSpacing(15);

            HBox regionMachineHbox = new HBox();
            regionMachineHbox.setAlignment(Pos.CENTER);
            regionMachineHbox.setSpacing(20);

            VBox regionVbox = new VBox();
            regionVbox.setSpacing(5);
            regionVbox.setAlignment(Pos.CENTER);
            Label regionLabel = WorkerNodesUtils.getCenteredContentLabel("Choose region");
            regionCombobox = new ComboBox<>();
            regionCombobox.setPromptText("Region");
            regionCombobox.valueProperty().addListener((obs, oldItem, newItem) -> onRegionSelect(oldItem, newItem));
            regionVbox.getChildren().addAll(regionLabel, regionCombobox);

            VBox machineVbox = new VBox();
            machineVbox.setSpacing(5);
            machineVbox.setAlignment(Pos.CENTER);
            Label machineLabel = WorkerNodesUtils.getCenteredContentLabel("Choose machine");
            machineCombobox = new ComboBox<>();
            machineCombobox.setPromptText("Machine");
            machineCombobox.setDisable(true);
            machineCombobox.valueProperty().addListener((obs, oldItem, newItem) -> onMachineSelect(oldItem, newItem));
            machineVbox.getChildren().addAll(machineLabel, machineCombobox);

            loadAllRelevantRegions();
            regionMachineHbox.getChildren().addAll(regionVbox, machineVbox);

            // creating refresh btn for refreshing table according to selections
            refreshBtn = new Button("Refresh");
            refreshBtn.setDisable(true);
            refreshBtn.setOnMouseClicked(event -> onRefreshClick());

            selectionVbox.getChildren().addAll(regionMachineHbox, refreshBtn);

            // Creating TableView productsTable
            productsTable = WorkerNodesUtils.getTableView(ProductInMachineData.class);
            productsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            productsTable.setVisible(false);

            // Creating columns
            imageColumn = new TableColumn<>("");
            productIdColumn = new TableColumn<>("Product ID");
            productNameColumn = new TableColumn<>("Product Name");
            currentAmountColumn = new TableColumn<>("Current Amount");
            newAmountColumn = new TableColumn<>("New Amount");
            // Adding columns to accountsTable
            productsTable.getColumns().addAll(imageColumn, productIdColumn, productNameColumn,
                    currentAmountColumn, newAmountColumn);
            configureTableData();

            // color instructions
            instructionsHBox = new HBox();
            instructionsHBox.setVisible(false);
            instructionsHBox.setSpacing(5);
            instructionsHBox.setAlignment(Pos.TOP_LEFT);
            instructionsHBox.setPadding(new Insets(0, 0, 0, 22));
            Label red = new Label("Unavailable");
            yellow_below_label = new Label("Below threshold");
            Circle redCircle = new Circle(8, 8, 8);
            redCircle.setFill(javafx.scene.paint.Color.rgb(255,153,102));
            Circle yellowCircle = new Circle(8, 8, 8);
            yellowCircle.setFill(javafx.scene.paint.Color.rgb(255,204,0));
            instructionsHBox.getChildren().addAll(redCircle, red, yellowCircle, yellow_below_label);

            // Adding Labels, tasksTable, instructionsHBox to accountsTableVBox
            inventoryTableVBox.getChildren().addAll(selectionVbox, productsTable, instructionsHBox);
            nodes.addAll(inventoryTableVBox);

            // Creating 'update' for finishing filling inventory button
            updateBtn = WorkerNodesUtils.createWide85Button("Update");
            updateBtn.setVisible(false);
            updateBtn.setOnMouseClicked(event -> onUpdateClick());
            bottomBroderVbox.getChildren().add(updateBtn);

            setViewFromTaskView();
        }

        private void onRegionSelect(String oldValue, String newValue) {
            if (oldValue == null) { // first select
                machineCombobox.setDisable(false);
                setDefaultMachineComboboxValues();
            } else if (!oldValue.equals(newValue)) { // there was a change in machine
                setDefaultMachineComboboxValues();
            }
        }

        private void onMachineSelect(String oldValue, String newValue) {
            if (oldValue == null) { // first select
                refreshBtn.setDisable(false);
            }
        }

        private void onRefreshClick() {
            if (machineCombobox.getSelectionModel().getSelectedItem() == null) {
                machineCombobox.setStyle(ColorsAndFonts.ERROR_COMBO_BOX_COLOR);
            } else {
                resetErrorsInForm();
                chosenMachineThreshold = getMachineThreshold();
                updateBelowLabel();
                setTableData();
                productsTable.setVisible(true);
                instructionsHBox.setVisible(true);
                updateBtn.setVisible(true);
            }
        }

        private void setDefaultMachineComboboxValues() {
            List<String> machines = tasksData.stream()
                    .filter(inventoryFillTask -> inventoryFillTask.getRegion().toString().equals(regionCombobox.getValue()))
                    .map(InventoryFillTask::getMachineName)
                    .collect(Collectors.toList());
            machineCombobox.getItems().clear();
            machineCombobox.getItems().addAll(machines);
            machineCombobox.getSelectionModel().clearSelection();
        }

        private void resetErrorsInForm() {
            ObservableList<Node> bottomVbox = bottomBroderVbox.getChildren();
            if (bottomVbox.size() >= 2)
                bottomVbox.remove(1); // removing error label below Update btn

            machineCombobox.setStyle(ColorsAndFonts.OK_COMBO_BOX_COLOR);
        }

        /**
         * Method used for initializing the window if the window is requested by "My Opened Tasks" window
         */
        private void setViewFromTaskView() {
            if (chosenRegion != null && chosenMachine != null) {
                regionCombobox.getSelectionModel().select(chosenRegion.toString());
                machineCombobox.getSelectionModel().select(chosenMachine);
                onRefreshClick();
            }
        }

        private void loadAllRelevantRegions() {
            tasksData = new ArrayList<>();
            requestOpenedTasks(tasksData);
            Set<String> regions = tasksData.stream()
                    .map(InventoryFillTask::getRegion)
                    .map(Enum::toString)
                    .collect(Collectors.toSet());

            regionCombobox.getItems().clear();
            regionCombobox.getItems().addAll(regions);
        }

        private void configureTableData() {
            imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
            productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            currentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
            newAmountColumn.setCellValueFactory(new PropertyValueFactory<>("newAmount"));

            imageColumn.setPrefWidth(45);
            productNameColumn.setPrefWidth(200);
            currentAmountColumn.setPrefWidth(150);
        }

        private Integer getSelectedMachineId() {
            for (InventoryFillTask task : tasksData) {
                if (task.getMachineName().equals(machineCombobox.getValue()) &&
                        task.getRegion().toString().equals(regionCombobox.getValue())) {
                    return task.getMachineId();
                }
            }
            return null;
        }

        private InventoryFillTask getSelectedTaskIfStatusIsOpened() {
            List<InventoryFillTask> openedTasksData = tasksData.stream()
                    .filter(task -> task.getStatus().equals(TaskStatus.OPENED))
                    .collect(Collectors.toList());

            for (InventoryFillTask task : openedTasksData) {
                if (task.getMachineName().equals(machineCombobox.getValue()) &&
                        task.getRegion().toString().equals(regionCombobox.getValue())) {
                    return task;
                }
            }
            return null;
        }
        
        private ImageView receiveImageForProduct(Product product) {
        	if (product.getImage() == null)
        		return null;
        	Image image = new Image(new ByteArrayInputStream(product.getImage()));
            ImageView myImage = new ImageView();
            myImage.setFitWidth(imageColumn.getPrefWidth() - 15);
            myImage.setFitHeight(imageColumn.getPrefWidth() - 15);
            myImage.setImage(image);
        	return myImage;
        }

        private void setTableData() {
            productsTable.getItems().clear();
            Integer chosenMachineId = getSelectedMachineId();
            if (chosenMachineId != null) {
                getProductsInMachine(chosenMachineId.toString());
            }
            List<ProductInMachineData> productsData = new ArrayList<>();
            Integer currentAmount = null;
            for (Product product : products) {
                for (ProductInMachine productInMachine : productsAmount) {
                    if(productInMachine.getProductId().equals(product.getProductId())) {
                        currentAmount = productInMachine.getAmount();
                    }
                }
                ImageView currentProductImg = receiveImageForProduct(product);
                productsData.add(new ProductInMachineData(
                        currentProductImg, product.getProductId(), product.getName(), currentAmount));
            }
            setTableColorRows();
            productsTable.getItems().addAll(productsData);
        }

        private void setTableColorRows() {
            productsTable.setRowFactory(tv -> new TableRow<ProductInMachineData>() {
                @Override
                public void updateItem(ProductInMachineData item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item == null) {
                        setStyle("");
                    } else if (item.currentAmount == 0) {
                        setStyle("-fx-background-color: rgb(255,153,102);");
                    } else if (item.currentAmount < chosenMachineThreshold) {
                        setStyle("-fx-background-color: rgb(255,204,0);");
                    }
                }
            });
        }

        private void getProductsInMachine(String machineId) {
        	List<Object> productsInMac = new ArrayList<>();
        	productsInMac.add(machineId);
        	Request request = new Request();
        	request.setPath("/machines/requestMachineProductsData");
        	request.setMethod(Method.GET);
        	request.setBody(productsInMac);
        	ClientUI.chat.accept(request);

            if (Client.resFromServer.getCode() == ResponseCode.OK) {
                List<Object> body = Client.resFromServer.getBody();
                if (body != null)
                    products = body.stream()
                            .map(productObject -> (Product) productObject)
                            .collect(Collectors.toList());
            } else {
                return;
            }
        	
        	List<Object> productsInMacAmount = new ArrayList<>();
        	productsInMacAmount.add(machineId);
        	Request request1 = new Request();
        	request1.setPath("/machines/requestMachineProductsAmount");
        	request1.setMethod(Method.GET);
        	request1.setBody(productsInMacAmount);
        	ClientUI.chat.accept(request1);

            if (Client.resFromServer.getCode() == ResponseCode.OK) {
                List<Object> body = Client.resFromServer.getBody();
                if (body != null)
                    productsAmount = body.stream()
                            .map(productObject -> (ProductInMachine) productObject)
                            .collect(Collectors.toList());
            }
        }

        private int getMachineThreshold() {
            int threshold = -1;
            List<Object> paramList = new ArrayList<>();
            Request request = new Request();
            request.setPath("/getMachineThreshold");
            request.setMethod(Method.GET);
            paramList.add(getSelectedMachineId());
            request.setBody(paramList);
            ClientUI.chat.accept(request);// sending the request to the server.
            if (Client.resFromServer.getCode() == ResponseCode.OK) {
                threshold = (Integer) Client.resFromServer.getBody().get(0);
            } else {
                System.out.println("Some error while retrieving machine threshold");
            }
            return threshold;
        }
        
        private void onUpdateClick() {
            if (bottomBroderVbox.getChildren().size() >= 2)
                bottomBroderVbox.getChildren().remove(1);
            Label msgLabel = new Label();

            // if validations of input new amount is failing
            String validationResult = validateAmountIsLargerAndChanged();
            if (!validationResult.equals(FORM_VALIDATION_IS_OK)) {
                msgLabel = WorkerNodesUtils.getCenteredContentLabel(validationResult);
            } else if (!validateMachineCapacity()) {
                msgLabel = WorkerNodesUtils.getCenteredContentLabel("The new total amount is above the " +
                        "machine capacity.\n\t\t  Please decrease total amount");
            } else { // validations of input new amount is ok
                postFillInventory();

                if (Client.resFromServer.getCode() == ResponseCode.OK) {
                    msgLabel = WorkerNodesUtils.getCenteredContentLabel(Client.resFromServer.getDescription());
                    // updating task status to IN_PROGRESS
                    InventoryFillTask task = getSelectedTaskIfStatusIsOpened();
                    if (task != null) { // if task is not IN_PROGRESS
                        task.setStatus(TaskStatus.IN_PROGRESS);
                        if (!postNewTaskStatus(task)) // if setting was failed
                            msgLabel = WorkerNodesUtils.getErrorLabel(Client.resFromServer.getDescription());
                    }
                } else {
                    msgLabel = WorkerNodesUtils.getErrorLabel(Client.resFromServer.getDescription());
                }
                chosenMachineThreshold = getMachineThreshold();
                updateBelowLabel();
                setTableData();
            }
            bottomBroderVbox.getChildren().add(msgLabel);
        }

        private void updateBelowLabel() {
            yellow_below_label.setText(String.format("Below threshold (= %s)", chosenMachineThreshold));
        }

        private List<ProductInMachine> getOnlyChangedProductsInMachine() {
            List<ProductInMachine> newProductsInMachine = new ArrayList<>();
            List<ProductInMachineData> onlyChangedProducts = productsTable.getItems().stream()
                    .filter(productInMachineData ->
                            !Objects.equals(productInMachineData.currentAmount,
                                    Integer.valueOf(productInMachineData.newAmount.getText())))
                    .collect(Collectors.toList());

            for (ProductInMachineData item : onlyChangedProducts) {
                String machineId = Objects.requireNonNull(getSelectedMachineId()).toString();
                String productId = item.getId();
                StatusInMachine statusInMachine;
                int amount = Integer.parseInt(item.newAmount.getText());
                if (amount >= getMachineThreshold())
                    statusInMachine = StatusInMachine.Above;
                else if (amount == 0)
                    statusInMachine = StatusInMachine.Not_Available;
                else
                    statusInMachine = StatusInMachine.Below;
                ProductInMachine newProduct = new ProductInMachine(machineId, productId, statusInMachine, amount);
                newProductsInMachine.add(newProduct);
            }
            return newProductsInMachine;
        }

        private void postFillInventory() {
            List<Object> newProductsInMachine = new ArrayList<>(getOnlyChangedProductsInMachine());
            Request request = new Request();
            request.setPath("/operationalWorker/fillInventory");
            request.setMethod(Method.POST);
            request.setBody(newProductsInMachine);
            ClientUI.chat.accept(request);
        }

        private boolean postNewTaskStatus(InventoryFillTask task) {
            List<Object> tasks = new ArrayList<>();
            tasks.add(task);
            Request request = new Request();
            request.setPath("/operationalWorker/setInventoryTask");
            request.setMethod(Method.PUT);
            request.setBody(tasks);
            ClientUI.chat.accept(request);
            return Client.resFromServer.getCode() == ResponseCode.OK;
        }

        private String validateAmountIsLargerAndChanged() {
            boolean amountWasChanged = false;
            boolean isFormOk = true;
            Integer currentAmount;
            String newAmount;
            for (ProductInMachineData productInMachine : productsTable.getItems()) {
                currentAmount = productInMachine.currentAmount;
                newAmount = productInMachine.newAmount.getText();

                if (isBlank(newAmount)) {
                    productInMachine.getNewAmount().setStyle(ColorsAndFonts.ERROR_COMBO_BOX_COLOR);
                    isFormOk = false;
                    continue;
                }

                // checking amount if new amount is less
                if (Integer.parseInt(newAmount) < currentAmount) {
                    isFormOk = false;
                    productInMachine.getNewAmount().setStyle(ColorsAndFonts.ERROR_COMBO_BOX_COLOR);
                } else if (Integer.parseInt(newAmount) > currentAmount) {
                    amountWasChanged = true;
                    productInMachine.getNewAmount().setStyle(ColorsAndFonts.OK_COMBO_BOX_COLOR);
                }
            }
            if (isFormOk && amountWasChanged) {
                return FORM_VALIDATION_IS_OK;
            } else if (!isFormOk) {
                return "Error in input data cannot leave blank fields\n\tor decrease amount of product";
            }
            // only when if (!amountWasChanged) is true
            return "Error in input data, please change at least on amount of product";
        }

        private boolean validateMachineCapacity() {
            // checking if the new amounts are below the machine capacity
            int newAmount;
            int sumOfProducts = 0;
            for (ProductInMachineData productInMachine : productsTable.getItems()) {
                newAmount = Integer.parseInt(productInMachine.newAmount.getText());
                sumOfProducts += newAmount;
            }
            return sumOfProducts <= MACHINE_CAPACITY;
        }

        /**
         * Class that handles Product In Machine entity in table for fill inventory task
         */
        public class ProductInMachineData {
            private final ImageView image;
            private final String id;
            private final String name;
            private final Integer currentAmount;
            private final TextField newAmount;
            private final int MAX_NUMBERS_LEN = 3;

            public ProductInMachineData(ImageView image, String id, String name, Integer currentAmount) {
                this.image = image;
                this.id = id;
                this.name = name;
                this.currentAmount = currentAmount;
                this.newAmount = new TextField(String.valueOf(currentAmount));
                this.newAmount.setPrefWidth(70);

                // setting constraints on TextField
                forceFieldToNumeric();
                this.newAmount.setStyle(ColorsAndFonts.OK_COMBO_BOX_COLOR);
            }

            public ImageView getImage() {
                return image;
            }

            public String getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public Integer getCurrentAmount() {
                return currentAmount;
            }

            public TextField getNewAmount() {
                return newAmount;
            }

            private void forceFieldToNumeric() {
                // force the field to be numeric only
                newAmount.textProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, String newValue) {
                        if (!newValue.matches("\\d*")) {
                            newAmount.setText(newValue.replaceAll("[^\\d]", ""));
                        }
                        // newAmount holds only integers -> replacing trailing zeros
                        if (!isBlank(newAmount.getText())) {
                            newAmount.setText(Integer.valueOf(newAmount.getText()).toString());
                        }

                        // force field to have max 3 numbers
                        if (newAmount.getText().length() > MAX_NUMBERS_LEN) {
                            String s = newAmount.getText().substring(0, MAX_NUMBERS_LEN);
                            newAmount.setText(s);
                        }
                    }
                });
            }
        }
    }

    private void requestOpenedTasks(List<InventoryFillTask> tasks) {
        Request request = new Request();
        request.setPath("/operationalWorker/getOpenedTasks");
        request.setMethod(Method.GET);
        List<Object> assignedWorker = new ArrayList<>();
        assignedWorker.add(worker.getId());
        request.setBody(assignedWorker);
        ClientUI.chat.accept(request); // sending the request to the server.
        if (Client.resFromServer.getCode() == ResponseCode.OK) {
            List<Object> body = Client.resFromServer.getBody();
            if (body == null)
                body = new ArrayList<>();
            tasks.addAll(body.stream()
                    .map(taskObject -> (InventoryFillTask) taskObject)
                    .collect(Collectors.toList()));
        } else {
            Label msgLabel = WorkerNodesUtils.getErrorLabel(Client.resFromServer.getDescription());
            bottomBroderVbox.getChildren().add(msgLabel);
        }
    }

    /**
     * function that start the fxml of the current window
     * @param primaryStage - Singleton stage
     */
    public void start(Stage primaryStage) {
        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/workers/fxmls/OperationalWorkerHomePage_Default.fxml"));
            anchorPane = loader.load();
            OperationalWorkerGui.controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(anchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Operational Worker"); // set window title
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setOnCloseRequest(e -> {
            try {
                forcedExit();
            } catch (IOException ex) {
            }
        });
    }
}
