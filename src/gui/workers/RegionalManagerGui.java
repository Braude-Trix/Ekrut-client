package gui.workers;

import client.Client;
import client.ClientUI;
import client.RegionalManager;
import gui.LoginController;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Machine;
import models.Method;
import models.Regions;
import models.Request;
import models.ResponseCode;
import models.User;
import models.Worker;
import models.WorkerType;
import utils.ColorsAndFonts;
import utils.Util;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static utils.Util.forcedExit;
import static utils.WorkerNodesUtils.getErrorLabel;
import static utils.WorkerNodesUtils.getSuccessLabel;

/**
 * Gui controller for presenting regional manger window
 */
public class RegionalManagerGui implements Initializable {
    public static RegionalManagerGui controller;
    public static Regions region = Regions.North;
    public static boolean isCEOLogged = false;
    private Worker worker = (Worker) LoginController.user;
	//public static Worker workerAccessByCeo = null;
    static final String VALIDATION_ERROR_MSG = "Missing input data, please check your selections";
    static final String NO_DATA_FOUND_ERROR_MSG = "No report was found for specified date\n\t\tPlease change input";

    @FXML
    ImageView bgImage;

    @FXML
    Label userNameLabel;

    @FXML
    Label userRoleLabel;

    @FXML
    Button inventoryReportBtn;

    @FXML
    Button ordersReportBtn;

    @FXML
    Button userReportBtn;

    @FXML
    Button machineConfBtn;

    @FXML
    Button pendingAccBtn;

    @FXML
    Button logoutBtn;

    @FXML
    VBox topBorderVBox;

    @FXML
    VBox centerBroderVbox;

    @FXML
    VBox bottomBroderVbox;

    /**
     * Initializing Regional manager window
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

		if (isCEOLogged) 
			logoutBtn.setVisible(false);
    	
        // setting username and region
        WorkerNodesUtils.setUserName(userNameLabel, worker);
        WorkerNodesUtils.setRole(userRoleLabel, worker.getRegion(), worker.getType());

        inventoryReportBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            inventoryReportBtn.setDisable(true);
            new SelectReportGui().loadInventoryReport();
        });
        ordersReportBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            ordersReportBtn.setDisable(true);
            new SelectReportGui().loadOrdersReport();
        });
        userReportBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            userReportBtn.setDisable(true);
            new SelectReportGui().loadUsersReport();
        });
        machineConfBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            machineConfBtn.setDisable(true);
            new MachineConfiguration().loadMachineConf();
        });
        pendingAccBtn.setOnMouseClicked((event) -> {
            enableAll();
            clearBorderPane();
            pendingAccBtn.setDisable(true);
            new PendingAccounts().loadPendingAccounts();
        });
        logoutBtn.setOnMouseClicked((event) -> {
            try {
                Util.genricLogOut(getClass());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void enableAll() {
        inventoryReportBtn.setDisable(false);
        ordersReportBtn.setDisable(false);
        userReportBtn.setDisable(false);
        machineConfBtn.setDisable(false);
        pendingAccBtn.setDisable(false);
    }

    private void clearBorderPane() {
        topBorderVBox.getChildren().clear();
        centerBroderVbox.getChildren().clear();
        bottomBroderVbox.getChildren().clear();
    }

    private class MachineConfiguration {
        private ComboBox<String> machineComboBox;
        private HBox configureHbox;
        private ComboBox<String> machineThresholdComboBox;
        private ComboBox<String> operationalWorkerComboBox;
        private Button saveThresholdButton;
        private Button callWorkerButton;
        private String initialThreshold;
        
        // hold in each place in list: {machineId, machineName, machineRegion, machingThreshold}
        private List<Machine> machinesSet;
        private Integer currentMachineId;
        private List<String> workerNames;
        private List<Worker> workerSet;

        private final String MACHINE_THRESHOLD_TITLE = "Set machine's threshold";
        private final String OPERATIONAL_WORKER_TITLE = "Choose operational worker";
        static final String VALIDATION_ERROR_MSG = "No input was changed, please change value before submitting";
        static final String SUCCESSES_MSG = "Your request was saved successfully!";
        static final String DB_ERROR_MSG = "It seems that there was a problem with DB!";


        private void loadMachineConf() {
            // Replacing background
            WorkerNodesUtils.setBackground("/assets/workers/InventoryReportMenu.jpg", bgImage);
            // Replacing top border
            WorkerNodesUtils.setTitle("Machine Configuration", topBorderVBox);

            // Replacing center border
            ObservableList<Node> nodes = centerBroderVbox.getChildren();

            // Vbox for choosing machine
            VBox machineSelectVBox = new VBox();
            machineSelectVBox.setAlignment(Pos.CENTER);
            machineSelectVBox.setSpacing(15);
            Label machineSelectLabel = new Label("Select the machine you wish to configure");
            machineSelectLabel.setFont(ColorsAndFonts.CONTENT_FONT);

            // Create a combo box
            machinesSet = new ArrayList<>();
            //List<String> dummyForMachine = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
            machineComboBox = new ComboBox<>();//FXCollections.observableList(dummyForMachine));
            machineComboBox.setPromptText("Choose Machine");
            machineSelectVBox.getChildren().addAll(machineSelectLabel, machineComboBox);

            // adding listener for changing from default value in machineComboBox
            machineComboBox.valueProperty().addListener((obs, oldItem, newItem) -> onMachineSelect(oldItem, newItem));

            // setting configureHbox for specific machine
            configureHbox = new HBox();
            configureHbox.setAlignment(Pos.CENTER);
            configureHbox.setSpacing(30);

            List<String> dummyForMachineThreshold = WorkerNodesUtils.getListInRange(1, 15);
            VBox thresholdVBox = getConfigurationVBox(MACHINE_THRESHOLD_TITLE, dummyForMachineThreshold,
                    "5", "Save");
            saveThresholdButton.setOnMouseClicked(this::onSaveThreshold);

            Separator separator = new Separator();
            separator.setOrientation(Orientation.VERTICAL);

            //List<String> dummyForWorkers = Arrays.asList("Avi", "Yossi", "Yuval", "Elad", "Liza");
            workerNames = new ArrayList<>();
            workerSet = new ArrayList<>();
            /*VBox operationalWorkerVBox = getConfigurationVBox(OPERATIONAL_WORKER_TITLE, dummyForWorkers,
                    "", "Call Worker");*/
            getOperationaWorkersList();
            VBox operationalWorkerVBox = getConfigurationVBox(OPERATIONAL_WORKER_TITLE, workerNames,
            "", "Call Worker");
            callWorkerButton.setOnMouseClicked((this::onCallWorker));

            configureHbox.getChildren().addAll(thresholdVBox, separator, operationalWorkerVBox);
            setMachinesNameComboBox(region);

            nodes.addAll(machineSelectVBox);
        }
        
        private void getOperationaWorkersList() {
        	List<Object> workerReq = new ArrayList<>();
        	workerReq.add(WorkerType.OperationalWorker);
        	Request request = new Request();
        	request.setPath("/workers/getWorkersByType");
        	request.setMethod(Method.GET);
        	request.setBody(workerReq);
        	ClientUI.chat.accept(request);
        	
        	switch(Client.resFromServer.getCode()) {
        	case OK:
        		//bottomBroderVbox.getChildren().add(WorkerNodesUtils.getSuccessLabel(Client.resFromServer.getDescription()));
        		updateWorkers((Client.resFromServer.getBody()));
        		break;
        	default:
        		bottomBroderVbox.getChildren().add(WorkerNodesUtils.getErrorLabel(Client.resFromServer.getDescription()));
        		break;
        		
        	}
        }
        
        private void updateWorkers(List<Object> listWorkers) {
        	workerNames.clear();
        	workerSet.clear();
        	if(listWorkers == null) {
        		return;
        	}
        	
        	for (Object worker : listWorkers) {
        		if(worker instanceof Worker) {
        			Worker currentWorker = (Worker) worker;
        			workerSet.add(currentWorker);
        			String workerName = currentWorker.getFirstName() + " " + currentWorker.getLastName();
        			workerNames.add(workerName);
        		}
        	}
        }

    	private void setMachinesNameComboBox(Regions region) {
    		List<Object> regionReq = new ArrayList<>();
    		regionReq.add(region);
    		Request request = new Request();
    		request.setPath("/machines/getMachine");
    		request.setMethod(Method.GET);
    		request.setBody(regionReq);
    		ClientUI.chat.accept(request);// sending the request to the server.

    		handleResponseGetMachines();
    	}

    	private void handleResponseGetMachines() {
    		//Client.resFromServer.setCode(ResponseCode.SERVER_ERROR);
    		switch (Client.resFromServer.getCode()) {
    		case OK:
    			updateMachines(Client.resFromServer.getBody());
    			break;
    		default:
    			bottomBroderVbox.getChildren().add(WorkerNodesUtils.getErrorLabel(Client.resFromServer.getDescription()));
    			break;
    		}
    	}

    	private void updateMachines(List<Object> listMachine) {
    		machinesSet.clear();
    		if (listMachine == null) {
    			machineComboBox.setDisable(true);
    			//errLabel = WorkerNodesUtils.getErrorLabel("There are no machines in this region at the moment");
    			//RegionalManagerGui.controller.bottomBroderVbox.getChildren().add(errLabel);
    			return;
    		}
    		ObservableList<String> options = FXCollections.observableArrayList();
    		for (Object machine : listMachine) {
    			if (machine instanceof Machine) {
    				Machine tempMachine = (Machine) machine;
    				machinesSet.add(tempMachine);
    				options.add(tempMachine.getName());
    			}
    		}
    		machineComboBox.getItems().addAll(options);
    		machineComboBox.setDisable(false);
    	}

        private VBox getConfigurationVBox(String title, List<String> comboboxValues, String initValue, String btnText) {
            VBox vBox = new VBox();
            vBox.setAlignment(Pos.CENTER);
            vBox.setSpacing(10);

            Label label = new Label(title);
            label.setFont(ColorsAndFonts.CONTENT_FONT);

            ComboBox<String> comboBox = new ComboBox(FXCollections.observableList(comboboxValues));
            // todo: add DB actual current threshold here
//            comboBox.getSelectionModel().select(initValue);
            comboBox.setVisibleRowCount(4);

            Button button = WorkerNodesUtils.createWide85Button(btnText);

            // saving state to class
            if (title.equals(MACHINE_THRESHOLD_TITLE)) {
                machineThresholdComboBox = comboBox;
                saveThresholdButton = button;

            } else {
                operationalWorkerComboBox = comboBox;
                callWorkerButton = button;
            }

            vBox.getChildren().addAll(label, comboBox, button);
            return vBox;
        }

        private void onMachineSelect(String oldValue, String newValue) {
            if (oldValue == null) {
                ObservableList<Node> centerBroder = centerBroderVbox.getChildren();
                centerBroder.add(configureHbox);
                for(Machine currentMachine : machinesSet)
                {
                	if(currentMachine.getName().equals(newValue)) {
                		initialThreshold = currentMachine.getThreshold();
                		currentMachineId = Integer.parseInt(currentMachine.getId());
                	}
                }
                setDefaultComboboxValues();
            } else if (!oldValue.equals(newValue)) { // there was a change in machine
                for(Machine currentMachine : machinesSet)
                {
                	if(currentMachine.getName().equals(newValue)) {
                		initialThreshold = currentMachine.getThreshold();
                		currentMachineId = Integer.parseInt(currentMachine.getId());
                	}
                }
            	setDefaultComboboxValues();
                resetErrorsInForm();
            }
        }

        private void setDefaultComboboxValues() {
            // call to db and get InitialThreshold of machineComboBox
            //initialThreshold = "3";
            machineThresholdComboBox.getSelectionModel().clearSelection();
            machineThresholdComboBox.getSelectionModel().select(initialThreshold);

            operationalWorkerComboBox.getSelectionModel().clearSelection();
        }

        private void onSaveThreshold(Event event) {
            ObservableList<Node> bottomVbox = bottomBroderVbox.getChildren();
            resetErrorsInForm();
            String selectedThreshold = machineThresholdComboBox.getValue();
            if (initialThreshold.equals(selectedThreshold)) {
                bottomVbox.add(getErrorLabel(VALIDATION_ERROR_MSG));
                machineThresholdComboBox.setStyle(ColorsAndFonts.ERROR_COMBO_BOX_COLOR);
            } else {
                // call to server to set threshold
            	setMachineThreshold(currentMachineId, Integer.parseInt(selectedThreshold));
                //bottomVbox.add(getSuccessLabel(SUCCESSES_MSG));
                initialThreshold = selectedThreshold;
            }
        }
        
    	private void setMachineThreshold(Integer machineId, Integer newThreshold) {
    		List<Object> thresholdReq = new ArrayList<>();
    		thresholdReq.add(machineId);
    		thresholdReq.add(newThreshold);
    		Request request = new Request();
    		request.setPath("/machines/setMachineThreshold");
    		request.setMethod(Method.PUT);
    		request.setBody(thresholdReq);
    		ClientUI.chat.accept(request);// sending the request to the server.
    		switch (Client.resFromServer.getCode()) {
    		case OK:
    			bottomBroderVbox.getChildren().add(getSuccessLabel(SUCCESSES_MSG));
    			break;
    		default:
    			bottomBroderVbox.getChildren().add(getErrorLabel(DB_ERROR_MSG));
    			break;
    		}	
    	}

        private void onCallWorker(Event event) {
            ObservableList<Node> bottomVbox = bottomBroderVbox.getChildren();
            resetErrorsInForm();

            String selectedWorker = operationalWorkerComboBox.getValue();
            if (selectedWorker == null) {
                bottomVbox.add(getErrorLabel(VALIDATION_ERROR_MSG));
                operationalWorkerComboBox.setStyle(ColorsAndFonts.ERROR_COMBO_BOX_COLOR);
            } else {
                // call to server to set new call for specified worker
                bottomVbox.add(getSuccessLabel(SUCCESSES_MSG));
            }
        }

        private void resetErrorsInForm() {
            ObservableList<Node> bottomVbox = bottomBroderVbox.getChildren();
            if (bottomVbox.size() >= 1)
                bottomVbox.remove(0);

            machineThresholdComboBox.setStyle(ColorsAndFonts.OK_COMBO_BOX_COLOR);
            operationalWorkerComboBox.setStyle(ColorsAndFonts.OK_COMBO_BOX_COLOR);
        }
    }

    private class PendingAccounts {
        private TableView<PendingAccountData> accountsTable;
        private TableColumn<PendingAccountData, String> nameColumn;
        private TableColumn<PendingAccountData, String> idColumn;
        private TableColumn<PendingAccountData, String> phoneColumn;
        private TableColumn<PendingAccountData, String> emailColumn;
        private TableColumn<PendingAccountData, PendingAccountData> confirmedColumn;
        private Button saveButton;
        private Button refreshButton;
        private final ObservableList<PendingAccountData> confirmedAccounts =
                FXCollections.observableList(new ArrayList<>());

        private void loadPendingAccounts() {
            // Replacing background
            WorkerNodesUtils.setBackground("/assets/workers/mainPage.jpg", bgImage);
            // Replacing top border
            WorkerNodesUtils.setTitle("Pending Accounts", topBorderVBox);
            // Replacing center border
            ObservableList<Node> nodes = centerBroderVbox.getChildren();

            // Vbox for the accounts table
            VBox accountsTableVBox = new VBox();
            accountsTableVBox.setAlignment(Pos.TOP_CENTER);
            VBox.setVgrow(accountsTableVBox, Priority.ALWAYS);
            accountsTableVBox.setPadding(new Insets(20, 0, 5, 0));

            // Creating Labels for instructions
            Label firstTableLabel = WorkerNodesUtils.getCenteredContentLabel("After confirming accounts");
            Label secondTableLabel = WorkerNodesUtils.getCenteredContentLabel("click the 'Save' button");

            // Creating TableView accountsTable
            accountsTable = WorkerNodesUtils.getTableView(PendingAccountData.class);

            // Creating columns
            nameColumn = new TableColumn<>("Full Name");
            idColumn = new TableColumn<>("ID");
            phoneColumn = new TableColumn<>("Phone NO.");
            emailColumn = new TableColumn<>("Email");
            confirmedColumn = new TableColumn<>("Confirmed");
            // Adding columns to accountsTable
            accountsTable.getColumns().addAll(nameColumn, idColumn, phoneColumn, emailColumn, confirmedColumn);

            // Adding Labels, accountsTable to accountsTableVBox
            accountsTableVBox.getChildren().addAll(firstTableLabel, secondTableLabel, accountsTable);
            nodes.addAll(accountsTableVBox);

            // Creating HBox for buttons
            HBox buttonsHBox = new HBox();
            buttonsHBox.setAlignment(Pos.CENTER);
            buttonsHBox.setSpacing(20);

            // Creating 'Save' and 'Refresh' buttons
            saveButton = WorkerNodesUtils.createWide85Button("Save");
            refreshButton = WorkerNodesUtils.createWide85Button("Refresh");
            saveButton.setDisable(true);

            saveButton.setOnMouseClicked(event -> onSave());
            refreshButton.setOnMouseClicked(event -> onRefresh());
            confirmedAccounts.addListener((ListChangeListener<? super PendingAccountData>) observable ->
                    saveButton.setDisable(confirmedAccounts.isEmpty()));

            // Adding the buttons to buttonsHBox
            buttonsHBox.getChildren().addAll(saveButton, refreshButton);
            bottomBroderVbox.getChildren().add(buttonsHBox);

            // table data configuration and setting
            configureTableData();
            setTableData();
        }

        private void configureTableData() {
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            confirmedColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue()));

            confirmedColumn.setCellFactory(param -> new TableCell<PendingAccountData, PendingAccountData>() {
                private final CheckBox confirmCheckBox = new CheckBox();

                @Override
                protected void updateItem(PendingAccountData accountData, boolean empty) {
                    super.updateItem(accountData, empty);

                    if (accountData == null) {
                        setGraphic(null);
                        return;
                    }

                    setGraphic(confirmCheckBox);
                    confirmCheckBox.setOnAction(event -> {
                        if (confirmCheckBox.isSelected())
                            confirmedAccounts.add(accountData);
                        else
                            confirmedAccounts.remove(accountData);
                    });
                }
            });
        }

        private void setTableData() {
            List<PendingAccountData> pendingAccountData = new ArrayList<>();
            List<User> pendingUsers = new ArrayList<>();
            requestPendingUsers(pendingUsers);

            for (User user : pendingUsers) {
                PendingAccountData accountData = new PendingAccountData(
                        user.getFirstName() + " " + user.getLastName(),
                        user.getId().toString(),
                        user.getPhoneNumber(),
                        user.getEmail());
                pendingAccountData.add(accountData);
            }
            accountsTable.setItems(FXCollections.observableList(pendingAccountData));
        }

        public class PendingAccountData {
            private final String name;
            private final String id;
            private final String phone;
            private final String email;

            public PendingAccountData(String name, String id, String phone, String email) {
                this.name = name;
                this.id = id;
                this.phone = phone;
                this.email = email;
            }

            public String getName() {
                return name;
            }

            public String getId() {
                return id;
            }

            public String getPhone() {
                return phone;
            }

            public String getEmail() {
                return email;
            }
        }

        private void onSave() {
            cleanMessageLabel();

            List<Integer> confirmedIds = confirmedAccounts.stream()
                    .map(PendingAccountData::getId)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());
            requestUpdatePendingUsers(confirmedIds);

            accountsTable.getItems().removeAll(confirmedAccounts);
            confirmedAccounts.clear();
            accountsTable.refresh();
            setTableData();
        }

        private void onRefresh() {
            accountsTable.refresh();
            confirmedAccounts.clear();
            cleanMessageLabel();
            setTableData();
        }

        private void addMessageLabel(String msg, boolean isError) {
            Label label;
            if (isError) {
                label = WorkerNodesUtils.getErrorLabel(msg);
            } else {
                label = WorkerNodesUtils.getSuccessLabel(msg);
            }
            bottomBroderVbox.getChildren().add(label);
        }

        private void cleanMessageLabel() {
            if (bottomBroderVbox.getChildren().size() > 1) {
                bottomBroderVbox.getChildren().remove(1);
            }
        }

        private void requestPendingUsers(List<User> userList) {
            Request request = new Request();
            request.setPath("users/allPendingUsers");
            request.setMethod(Method.GET);
            request.setBody(null);
            ClientUI.chat.accept(request); // sending the request to the server.
            if (Client.resFromServer.getCode() == ResponseCode.OK) {
                List<Object> body = Client.resFromServer.getBody();
                if (body == null)
                    body = new ArrayList<>();
                userList.addAll(body.stream()
                        .map(userObject -> (User) userObject)
                        .collect(Collectors.toList()));
            } else {
                addMessageLabel(Client.resFromServer.getDescription(), true);
            }
        }

        private void requestUpdatePendingUsers(List<Integer> confirmedIds) {
            List<Object> confirmedObjIds = new ArrayList<>(confirmedIds);
            Request request = new Request();
            request.setPath("users/upgradeToCostumer");
            request.setMethod(Method.POST);
            request.setBody(confirmedObjIds);
            ClientUI.chat.accept(request); // sending the request to the server.
            if (Client.resFromServer.getCode() == ResponseCode.OK) {
                addMessageLabel("The chosen pending accounts are confirmed", false);
            } else {
                addMessageLabel(Client.resFromServer.getDescription(), true);
            }
        }
    }

    /**
     * function that start the fxml of the current window
     * @param primaryStage - Singleton stage
     */
    public void start(Stage primaryStage) {
        RegionalManager.primaryStage = primaryStage;
        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/workers/ManagerHomePage_Default.fxml"));
            anchorPane = loader.load();
            RegionalManagerGui.controller = loader.getController();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        Scene scene = new Scene(anchorPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Regional Manager"); // set window title
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
