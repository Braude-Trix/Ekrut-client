package gui.workers;

import client.Client;
import client.ClientUI;
import client.OperationalWorker;
import gui.LoginController;
import gui.NewOrderController.ProductInMachineMonitor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Machine;
import models.Method;
import models.Order;
import models.Product;
import models.ProductInMachine;
import models.ProductInOrder;
import models.Request;
import models.StyleConstants;
import models.StylePaths;
import models.Worker;
import utils.ColorsAndFonts;
import utils.Util;
import utils.WorkerNodesUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static utils.Util.forcedExit;
import static utils.Utils.isBlank;

/**
 * Gui controller for presenting Operational Worker window
 */
public class OperationalWorkerGui implements Initializable {
    public static OperationalWorkerGui controller;
    public static String chosenRegion;
    public static String chosenMachine;
    public static boolean isCEOLogged = false;
    private Worker worker = (Worker) LoginController.user;

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
                Util.genricLogOut(getClass());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        if (isCEOLogged)
            logoutBtn.setVisible(false);
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
        private TableView<MyTaskData> openedTasksTable;
        private TableColumn<MyTaskData, String> dateColumn;
        private TableColumn<MyTaskData, String> regionColumn;
        private TableColumn<MyTaskData, String> machineIdColumn;
        private TableColumn<MyTaskData, String> machineNameColumn;
        private TableColumn<MyTaskData, String> statusColumn;
        private Button goToButton;
        private Button closeTaskButton;
        private MyTaskData selectedTask;

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

            // Creating Labels for instructions
            Label aboveTableLabel = WorkerNodesUtils.getCenteredContentLabel("choose a task to work on");

            // Creating TableView tasksTable
            openedTasksTable = WorkerNodesUtils.getTableView(MyTaskData.class);

            // Creating columns
            dateColumn = new TableColumn<>("Creation Date");
            regionColumn = new TableColumn<>("Region");
            machineIdColumn = new TableColumn<>("Machine ID");
            machineNameColumn = new TableColumn<>("Machine Name");
            statusColumn = new TableColumn<>("Status");
            // Adding columns to accountsTable
            openedTasksTable.getColumns().addAll(dateColumn, regionColumn, machineIdColumn,
                    machineNameColumn, statusColumn);
            configureTableData();
            setTableData();

            // Adding Labels, tasksTable to accountsTableVBox
            tasksTableVBox.getChildren().addAll(aboveTableLabel, openedTasksTable);
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
        }

        private void configureTableData() {
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            regionColumn.setCellValueFactory(new PropertyValueFactory<>("region"));
            machineIdColumn.setCellValueFactory(new PropertyValueFactory<>("machineId"));
            machineNameColumn.setCellValueFactory(new PropertyValueFactory<>("machineName"));
            statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        }

        private void setTableData() { // todo: replace with server data
            List<MyTaskData> tasksData = new ArrayList<>();

            tasksData.add(new MyTaskData("10/11/2222", "North", "1234", "L 606", true));
            tasksData.add(new MyTaskData("11/11/2222", "North", "3333", "M 2909", false));
            tasksData.add(new MyTaskData("12/11/2222", "UAE", "4444", "EF 301", false));
            tasksData.add(new MyTaskData("13/11/2222", "South", "5555", "P 404", true));
            openedTasksTable.getItems().addAll(tasksData);
        }

        private void setOnCellClicked() {
            openedTasksTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    if (goToButton.isDisable())
                        goToButton.setDisable(false);
                    selectedTask = newSelection;

                    closeTaskButton.setVisible(selectedTask.getIsInProgress());
                }
            });
        }

        private void onGoToClick() {
            chosenRegion = selectedTask.region;
            chosenMachine = selectedTask.machineName;

            enableAll();
            clearBorderPane();
            machineInventoryBtn.setDisable(true);
            new MachineInventory().loadMachineInventory();
        }

        private void onCloseTaskClick() {
            if (bottomBroderVbox.getChildren().size() >= 3)
                bottomBroderVbox.getChildren().remove(2);
            Label msgLabel;
            // todo: call to server and change status to closed
            // if request is ok
            boolean responseStatus = true;
            if (responseStatus) {
                msgLabel = WorkerNodesUtils.getCenteredContentLabel("Task for machine " +
                        selectedTask.getMachineName() + " was closed successfully");
                openedTasksTable.getItems().remove(selectedTask);
            } else { // if some error
                msgLabel = WorkerNodesUtils.getCenteredContentLabel("Task for machine " +
                        selectedTask.getMachineName() + " couldn't close, Server error occurred");
            }
            bottomBroderVbox.getChildren().add(msgLabel);
        }

        public class MyTaskData {
            private final String date;
            private final String region;
            private final String machineId;
            private final String machineName;
            private final String status;
            private final boolean isInProgress;

            public MyTaskData(String date, String region, String machineId, String machineName, boolean isInProgress) {
                this.date = date;
                this.region = region;
                this.machineId = machineId;
                this.machineName = machineName;
                this.isInProgress = isInProgress;
                status = isInProgress ? "In Progress" : "Open";
            }

            public String getDate() {
                return date;
            }

            public String getRegion() {
                return region;
            }

            public String getMachineId() {
                return machineId;
            }

            public String getMachineName() {
                return machineName;
            }

            public String getStatus() {
                return status;
            }

            public boolean getIsInProgress() {
                return isInProgress;
            }
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
        private Button updateBtn;
        //private VBox selectionVbox;
        
        private List<Object> products;
        private List<Object> productsAmount;
        

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

            loadAllRelevantMachinesAndRegions();
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

            // Adding Labels, tasksTable to accountsTableVBox
            inventoryTableVBox.getChildren().addAll(selectionVbox, productsTable);
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
                setTableData();
                productsTable.setVisible(true);
                updateBtn.setVisible(true);
            }

        }

        private void setDefaultMachineComboboxValues() {
            // call to db and get list of machines with opened tasks in selected region
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
                regionCombobox.getSelectionModel().select(chosenRegion);
                machineCombobox.getSelectionModel().select(chosenMachine);
                onRefreshClick();
            }
        }

        private void loadAllRelevantMachinesAndRegions() {
            // todo: DB values here
            List<String> dummyForRegion = Arrays.asList("North", "South", "UAE");
            regionCombobox.getItems().clear();
            regionCombobox.getItems().addAll(dummyForRegion);
            List<String> dummyForMachine = Arrays.asList("L 606", "M 2909", "EF 301", "P 404", "P 405");
            machineCombobox.getItems().clear();
            machineCombobox.getItems().addAll(dummyForMachine);
        }

        private void configureTableData() {
            imageColumn.setCellValueFactory(new PropertyValueFactory<>("image"));
            productIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            productNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            currentAmountColumn.setCellValueFactory(new PropertyValueFactory<>("currentAmount"));
            newAmountColumn.setCellValueFactory(new PropertyValueFactory<>("newAmount"));

            imageColumn.setPrefWidth(45);
            productNameColumn.setPrefWidth(150);
            currentAmountColumn.setPrefWidth(150);
        }
        
        private ImageView recieveImageForProduct(Product product)
        {
        	if (product.getImage() == null)
        		return null;
        	Image image = new Image(new ByteArrayInputStream(product.getImage()));
            ImageView myImage = new ImageView();
            myImage.setImage(image);
        	return myImage;
        }

        private void setTableData() { // todo: replace with server data
            productsTable.getItems().clear();
            products = new ArrayList<>();
            productsAmount = new ArrayList<>();
            getProductsInMachine("1");
            List<ProductInMachineData> productsData = new ArrayList<>();
            Integer currentAmount = null;
            for(Object product : products) {
            	if (product instanceof Product) {
            		for (Object productInMachine : productsAmount) {
        				if(((ProductInMachine) productInMachine).getProductId().equals(((Product) product).getProductId())) {
        					currentAmount = ((ProductInMachine) productInMachine).getAmount();
        				}
            		}
            		Product currentProduct = (Product) product;
            		ImageView curentProductImg = recieveImageForProduct(currentProduct);
            		productsData.add(new ProductInMachineData(curentProductImg, currentProduct.getProductId(),
            				currentProduct.getName(),currentAmount));
            	}
            }
            productsTable.getItems().addAll(productsData);
        }
        
        private void getProductsInMachine(String machineId) {
        	List<Object> productsInMac = new ArrayList<>();
        	productsInMac.add(machineId);
        	Request request = new Request();
        	request.setPath("/machines/requestMachineProductsData");
        	request.setMethod(Method.GET);
        	request.setBody(productsInMac);
        	ClientUI.chat.accept(request);
        	
        	switch(Client.resFromServer.getCode()) {
        	case OK:
        		products = Client.resFromServer.getBody();
        		break;
        	default:
        		products = null;
        		productsAmount = null;
        		return;
        	}
        	
        	List<Object> productsInMacAmount = new ArrayList<>();
        	productsInMacAmount.add(machineId);
        	Request request1 = new Request();
        	request1.setPath("/machines/requestMachineProductsAmount");
        	request1.setMethod(Method.GET);
        	request1.setBody(productsInMacAmount);
        	ClientUI.chat.accept(request1);
        	
        	switch(Client.resFromServer.getCode()) {
        	case OK:
        		productsAmount = Client.resFromServer.getBody();
        		break;
        	default:
        		break;
        	}
        }
        
        private void onUpdateClick() {
            if (bottomBroderVbox.getChildren().size() >= 2)
                bottomBroderVbox.getChildren().remove(1);
            Label msgLabel;

            // if validations of input new amount is failing
            String validationResult = validateAmountIsLargerAndChanged();
            if (!validationResult.equals(FORM_VALIDATION_IS_OK)) {
                msgLabel = WorkerNodesUtils.getCenteredContentLabel(validationResult);
            } else if (!validateMachineCapacity()) {
                msgLabel = WorkerNodesUtils.getCenteredContentLabel("The new total amount is above the " +
                        "machine capacity.\n\t\t  Please decrease total amount");
            } else { // validations of input new amount is ok
                // if request is ok
                boolean responseStatus = true;
                if (responseStatus) {
                    msgLabel = WorkerNodesUtils.getCenteredContentLabel("Machine inventory was updated successfully");
                } else { // if some error
                    msgLabel = WorkerNodesUtils.getCenteredContentLabel("Machine inventory couldn't update, Server error occurred");
                }
            }
            bottomBroderVbox.getChildren().add(msgLabel);
            // todo: change status of task form opened to in progress to enable in tasks form finishing the task
            // todo: reload new table content
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

    /**
     * function that start the fxml of the current window
     * @param primaryStage - Singleton stage
     */
    public void start(Stage primaryStage) {
        OperationalWorker.primaryStage = primaryStage;
        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/workers/OperationalWorkerHomePage_Default.fxml"));
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
                throw new RuntimeException(ex);
            }
        });
    }
}
