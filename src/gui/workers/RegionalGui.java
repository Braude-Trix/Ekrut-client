package gui.workers;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import utils.ColorsAndFonts;
import utils.WorkerNodesUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import static utils.WorkerNodesUtils.getErrorLabel;
import static utils.WorkerNodesUtils.getSuccessLabel;

/**
 * Gui controller for presenting regional manger window
 */
public class RegionalGui implements Initializable {
    public static RegionalGui controller;
    public static String region = "North"; // todo: replace this with db
    public static String userName = "Ben"; // todo: replace this with db
    public static boolean isCEOLogged = false;
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
        // setting username and region
        WorkerNodesUtils.setUserName(userNameLabel, userName);
        WorkerNodesUtils.setRegion(userRoleLabel, region);

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
        logoutBtn.setOnMouseClicked((event) -> System.out.println(event.getSource().toString()));

        if (isCEOLogged)
            logoutBtn.setVisible(false);
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

        private final String MACHINE_THRESHOLD_TITLE = "Set machine's threshold";
        private final String OPERATIONAL_WORKER_TITLE = "Choose operational worker";
        static final String VALIDATION_ERROR_MSG = "No input was changed, please change value before submitting";
        static final String SUCCESSES_MSG = "Your request was saved successfully!";


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
            List<String> dummyForMachine = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
            machineComboBox = new ComboBox(FXCollections.observableList(dummyForMachine));
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

            List<String> dummyForWorkers = Arrays.asList("Avi", "Yossi", "Yuval", "Elad", "Liza");
            VBox operationalWorkerVBox = getConfigurationVBox(OPERATIONAL_WORKER_TITLE, dummyForWorkers,
                    "", "Call Worker");
            callWorkerButton.setOnMouseClicked((this::onCallWorker));

            configureHbox.getChildren().addAll(thresholdVBox, separator, operationalWorkerVBox);

            nodes.addAll(machineSelectVBox);
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
                setDefaultComboboxValues();
            } else if (!oldValue.equals(newValue)) { // there was a change in machine
                setDefaultComboboxValues();
                resetErrorsInForm();
            }
        }

        private void setDefaultComboboxValues() {
            // call to db and get InitialThreshold of machineComboBox
            initialThreshold = "3";
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
                bottomVbox.add(getSuccessLabel(SUCCESSES_MSG));
                initialThreshold = selectedThreshold;
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
        private final List<PendingAccountData> confirmedAccounts = new ArrayList<>();

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
            configureTableData();
            setTableData();

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

            saveButton.setOnMouseClicked(event -> onSave());
            refreshButton.setOnMouseClicked(event -> onRefresh());

            // Adding the buttons to buttonsHBox
            buttonsHBox.getChildren().addAll(saveButton, refreshButton);
            bottomBroderVbox.getChildren().add(buttonsHBox);
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

        private void setTableData() { // todo: replace with server data
            List<PendingAccountData> pendingAccountData = new ArrayList<>();
            pendingAccountData.add(new PendingAccountData("yossi", "1234", "87594343", "avi@gmail.com"));
            pendingAccountData.add(new PendingAccountData("yossi1", "12345", "87594344", "avi1@gmail.com"));
            pendingAccountData.add(new PendingAccountData("yossi2", "12346", "87594345", "avi2@gmail.com"));
            pendingAccountData.add(new PendingAccountData("yossi3", "12347", "87594346", "avi3@gmail.com"));
            accountsTable.getItems().addAll(pendingAccountData);
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
            // todo: make a server call and request for change accounts to be approved
            accountsTable.getItems().removeAll(confirmedAccounts);
            accountsTable.refresh();
        }

        private void onRefresh() {
            // todo: make a server call and request for new pending accounts to be approved
            accountsTable.refresh();
            confirmedAccounts.clear();
        }
    }
}