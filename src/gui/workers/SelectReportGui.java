package gui.workers;

import client.IClient;
import client.ClientWrapper;
import gui.StageSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.InventoryReport;
import models.Machine;
import models.Method;
import models.OrdersReport;
import models.Regions;
import models.ReportType;
import models.Request;
import models.ResponseCode;
import models.SavedReportRequest;
import models.UsersReport;
import utils.ColorsAndFonts;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

import static gui.workers.RegionalManagerGui.VALIDATION_ERROR_MSG;

/**
 * Gui controller for presenting different types of reports for regional manger window
 */
public class SelectReportGui {
    public static Stage popupDialog;
    private static ReportType reportType;
    private IComboBox machineComboBox;
    private IComboBox yearComboBox;
    private IComboBox monthComboBox;
    private List<ComboBox<String>> comboBoxList;
    private Button viewButton;
    private Label errLabel;
    private List<Machine> machinesSet;
    private Object reportFromServer;
    private IClient clientInterface;
    private IReportPopupOpen popupOpen;

    public SelectReportGui() {
        clientInterface = new ClientWrapper();
        popupOpen = new ReportPopupOpen();
        machineComboBox = new comboBoxWrapper();
        yearComboBox = new comboBoxWrapper();
        monthComboBox = new comboBoxWrapper();
    }

    public SelectReportGui(IClient iclient, IReportPopupOpen iReportPopupOpen,
                           IComboBox machineCB, IComboBox yearCB, IComboBox monthCB) {
        clientInterface = iclient;
        popupOpen = iReportPopupOpen;
        machineComboBox = machineCB;
        yearComboBox = yearCB;
        monthComboBox = monthCB;
    }

    void loadInventoryReport() {
        reportType = ReportType.INVENTORY;
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/InventoryReportMenu.jpg", RegionalManagerGui.controller.bgImage);
        // Replacing top border
        WorkerNodesUtils.setTitle("Inventory Report", RegionalManagerGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalManagerGui.controller.centerBroderVbox.getChildren();

        VBox contentVBox = getContentVBox();

        // Vbox for choosing machine
        VBox machineSelectVBox = new VBox();
        machineSelectVBox.setAlignment(Pos.CENTER);
        machineSelectVBox.setSpacing(15);
        Label machineSelectLabel = new Label("Select the machine you wish to view report on");
        machineSelectLabel.setFont(ColorsAndFonts.CONTENT_FONT);

        // Create a combo box
        machinesSet = new ArrayList<>();
        setMachinesNameComboBox(RegionalManagerGui.region);
        machineComboBox.getComboBox().setPromptText("Choose Machine");
        machineSelectVBox.getChildren().addAll(machineSelectLabel, machineComboBox.getComboBox());

        VBox dateSelectVBox = getDateSelectVBox();

        contentVBox.getChildren().addAll(machineSelectVBox, dateSelectVBox);
        comboBoxList = Arrays.asList(
                machineComboBox.getComboBox(), yearComboBox.getComboBox(), monthComboBox.getComboBox());

        // View report button
        VBox buttonVBox = getViewReportVBox();

        nodes.addAll(contentVBox);
    }
    
	private void setMachinesNameComboBox(Regions region) {
		List<Object> regionReq = new ArrayList<>();
		regionReq.add(region);
		Request request = new Request();
		request.setPath("/machines/getMachine");
		request.setMethod(Method.GET);
		request.setBody(regionReq);
        clientInterface.setRequestForServer(request);// sending the request to the server.

		handleResponseGetMachines();
	}

	private void handleResponseGetMachines() {
		switch (clientInterface.getResFromServer().getCode()) {
		case OK:
			updateMachines((clientInterface.getResFromServer().getBody()));
			break;
		default:
			errLabel = WorkerNodesUtils.getErrorLabel(clientInterface.getResFromServer().getDescription());
			break;
		}
	}

	private void updateMachines(List<Object> listMachine) {
		machinesSet.clear();
		if (listMachine == null) {
			machineComboBox.getComboBox().setDisable(true);
			errLabel = WorkerNodesUtils.getErrorLabel("There are no machines in this region at the moment");
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
		machineComboBox.getComboBox().getItems().addAll(options);
		machineComboBox.getComboBox().setDisable(false);
	}

    void loadOrdersReport() {
        reportType = ReportType.ORDERS;
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", RegionalManagerGui.controller.bgImage);
        // Replacing top border
        WorkerNodesUtils.setTitle("Orders Report", RegionalManagerGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalManagerGui.controller.centerBroderVbox.getChildren();

        VBox contentVBox = getContentVBox();
        VBox dateSelectVBox = getDateSelectVBox();

        contentVBox.getChildren().add(dateSelectVBox);
        comboBoxList = Arrays.asList(yearComboBox.getComboBox(), monthComboBox.getComboBox());

        // View report button
        VBox buttonVBox = getViewReportVBox();

        nodes.addAll(contentVBox);
    }

    void loadUsersReport() {
        reportType = ReportType.USERS;
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", RegionalManagerGui.controller.bgImage);
        // Replacing top border
        WorkerNodesUtils.setTitle("Users Report", RegionalManagerGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalManagerGui.controller.centerBroderVbox.getChildren();

        VBox contentVBox = getContentVBox();
        VBox dateSelectVBox = getDateSelectVBox();

        contentVBox.getChildren().add(dateSelectVBox);
        comboBoxList = Arrays.asList(yearComboBox.getComboBox(), monthComboBox.getComboBox());

        // View report button
        VBox buttonVBox = getViewReportVBox();

        nodes.addAll(contentVBox);
    }

    private VBox getDateSelectVBox() {
        VBox dateSelectVBox = new VBox();
        dateSelectVBox.setAlignment(Pos.CENTER);
        dateSelectVBox.setSpacing(15);
        Label dateSelectLabel = new Label("Select specific month and year");
        dateSelectLabel.setFont(ColorsAndFonts.CONTENT_FONT);

        // Create a date combo boxes (HBOX)
        HBox dateHBox = new HBox();
        dateHBox.setAlignment(Pos.CENTER);
        dateHBox.setSpacing(25);
        List<String> dummyForMonth = WorkerNodesUtils.getListInRange(1, 12);
        monthComboBox.getComboBox().getItems().setAll(FXCollections.observableList(dummyForMonth));
        monthComboBox.getComboBox().setPromptText("Month");
        List<String> dummyForYear = WorkerNodesUtils.getListInRange(2018, Year.now().getValue());
        yearComboBox.getComboBox().getItems().setAll(FXCollections.observableList(dummyForYear));
        monthComboBox.getComboBox().setPromptText("Year");
        monthComboBox.getComboBox().getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            setMaxMonthWhenCurrentYear(Integer.parseInt(newValue));
        });

        dateHBox.getChildren().addAll(monthComboBox.getComboBox(), yearComboBox.getComboBox());

        dateSelectVBox.getChildren().addAll(dateSelectLabel, dateHBox);
        return dateSelectVBox;
    }

    private VBox getViewReportVBox() {
        VBox bottomBroderVbox = RegionalManagerGui.controller.bottomBroderVbox;
        viewButton = new Button("View Report");
        viewButton.setPrefSize(200, 30);
        bottomBroderVbox.getChildren().add(viewButton);
        //bottomBroderVbox.getChildren().add(1, errLabel);

        viewButton.setOnMouseClicked((event -> {
            if (bottomBroderVbox.getChildren().size() > 1) { // removing old error label
                bottomBroderVbox.getChildren().remove(1);
            }
            if (!validateFormValue()) {
				errLabel = WorkerNodesUtils.getErrorLabel(VALIDATION_ERROR_MSG);
                bottomBroderVbox.getChildren().add(errLabel);
            } else { // all form data is validated
                boolean isLoaded = loadReportPopup();
                if (!isLoaded) {
                    errLabel = WorkerNodesUtils.getErrorLabel(clientInterface.getResFromServer().getDescription());
                    bottomBroderVbox.getChildren().add(errLabel);
                }
            }
        }));
        return bottomBroderVbox;
    }

    private boolean loadReportPopup() {
        int year = Integer.parseInt(yearComboBox.getSelected());
        int month = Integer.parseInt(monthComboBox.getSelected());
        Regions region = RegionalManagerGui.region;
        Integer machineId = null; // if not inventory report
        if (reportType == ReportType.INVENTORY) {
            String machineName = machineComboBox.getSelected();
            machineId = Integer.valueOf(machinesSet.stream()
                    .filter(machine -> Objects.equals(machine.getName(), machineName))
                    .map(Machine::getId)
                    .findFirst().get());
        }
        SavedReportRequest savedReport = new SavedReportRequest(year, month, reportType, region, machineId);
        requestReportFromServer(savedReport);

        if (clientInterface.getResFromServer().getCode() != ResponseCode.OK) {
            return false;
        }
        reportFromServer = clientInterface.getResFromServer().getBody().get(0);
        setInitValuesInReportsPopup();

        switch (reportType) {
            case INVENTORY:
                popupOpen.openReportPopup("/assets/workers/fxmls/InventoryReportPage.fxml");
                break;
            case ORDERS:
                popupOpen.openReportPopup("/assets/workers/fxmls/OrdersReportPage.fxml");
                break;
            case USERS:
                popupOpen.openReportPopup("/assets/workers/fxmls/UsersReportPage.fxml");
                break;
        }
        return true;
    }

    class comboBoxWrapper implements IComboBox {
        private ComboBox<String> comboBox;
        public comboBoxWrapper() {
            comboBox = new ComboBox<>();
        }
        @Override
        public ComboBox<String> getComboBox() {
            return comboBox;
        }

        @Override
        public String getSelected() {
            return comboBox.getSelectionModel().getSelectedItem();
        }
    }

    class ReportPopupOpen implements IReportPopupOpen {
        @Override
        public void openReportPopup(String fxmlPath) {
            Stage primaryStage = StageSingleton.getInstance().getStage();
            popupDialog = new Stage();
            popupDialog.initModality(Modality.APPLICATION_MODAL);
            popupDialog.initStyle(StageStyle.UNDECORATED);
            popupDialog.initOwner(primaryStage);

            AnchorPane anchorPane;
            try {
                FXMLLoader loader = new FXMLLoader();
                loader.setLocation(getClass().getResource(fxmlPath));
                anchorPane = loader.load();
                setControllerInReportsPopup(loader);
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            Scene dialogScene = new Scene(anchorPane);
            dialogScene.getStylesheets().add("styles/charts.css");
            popupDialog.setScene(dialogScene);
            popupDialog.setResizable(false);
            popupDialog.show();
            WorkerNodesUtils.setStageMovable(popupDialog);
        }
    }

    private void setInitValuesInReportsPopup() {
        switch (reportType) {
            case INVENTORY:
                InventoryReportPopupGui.machineName = machineComboBox.getSelected();
                InventoryReportPopupGui.year = Integer.parseInt(yearComboBox.getSelected());
                InventoryReportPopupGui.month = Integer.parseInt(monthComboBox.getSelected());
                InventoryReportPopupGui.inventoryReportData = (InventoryReport) reportFromServer;
                break;
            case ORDERS:
                OrdersReportPopupGui.year = Integer.parseInt(yearComboBox.getSelected());
                OrdersReportPopupGui.month = Integer.parseInt(monthComboBox.getSelected());
                OrdersReportPopupGui.ordersReportData = (OrdersReport) reportFromServer;
                break;
            case USERS:
                UsersReportPopupGui.year = Integer.parseInt(yearComboBox.getSelected());
                UsersReportPopupGui.month = Integer.parseInt(monthComboBox.getSelected());
                UsersReportPopupGui.usersReportData = (UsersReport) reportFromServer;
                break;
        }
    }

    private Map<String, Integer> getOrders() {
        Map<String, Integer> orders = new HashMap<>();
        int top = new Random().nextInt(15 + 1);
        for (int i = 0; i < top; i++) {
            orders.put(String.valueOf(new Random().nextInt(1000000 + 1)), new Random().nextInt(20 + 1));
        }
        return orders;
    }
    private Map<String, Integer> getOrders1() {
        Map<String, Integer> orders = new HashMap<>();
        int top = new Random().nextInt(25 + 1);
        for (int i = 0; i < top; i++) {
            orders.put(String.valueOf(new Random().nextInt(1000000 + 1)), new Random().nextInt(20 + 1));
        }
        return orders;
    }

    private void setControllerInReportsPopup(FXMLLoader loader) {
        switch (reportType) {
            case INVENTORY:
                InventoryReportPopupGui.controller = loader.getController();
                break;
            case ORDERS:
                OrdersReportPopupGui.controller = loader.getController();
                break;
            case USERS:
                UsersReportPopupGui.controller = loader.getController();
                break;
        }
    }

    private boolean validateFormValue() {
        boolean isValid = true;
        for (ComboBox<String> comboBox : comboBoxList) {
            if (comboBox != null && comboBox.getValue() == null) {
                comboBox.setStyle(ColorsAndFonts.ERROR_COMBO_BOX_COLOR);
                isValid = false;
            } else if (comboBox != null) {
                comboBox.setStyle(ColorsAndFonts.OK_COMBO_BOX_COLOR);
            }
        }
        return isValid;
    }

    private void setMaxMonthWhenCurrentYear(int selectedYear) {
        String oldSelectedMonth = monthComboBox.getComboBox().getValue();
        monthComboBox.getComboBox().getItems().clear();
        if (selectedYear == Year.now().getValue()) { // if current year
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            monthComboBox.getComboBox().getItems().addAll(WorkerNodesUtils.getListInRange(1, currentMonth));
        } else {
            monthComboBox.getComboBox().getItems().addAll(WorkerNodesUtils.getListInRange(1, 12));
        }

        // restoring user last selection
        if (monthComboBox.getComboBox().getItems().contains(oldSelectedMonth)) {
            monthComboBox.getComboBox().getSelectionModel().select(oldSelectedMonth);
        }
    }

    private static VBox getContentVBox() {
        VBox contentVBox = new VBox();
        contentVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(contentVBox, Priority.ALWAYS);
        contentVBox.setSpacing(20);
        return contentVBox;
    }

    private void requestReportFromServer(SavedReportRequest reportRequest) {
        List<Object> paramList = new ArrayList<>();
        paramList.add(reportRequest);

        Request request = new Request();
        request.setPath("/reports");
        request.setMethod(Method.GET);
        request.setBody(paramList);
        clientInterface.setRequestForServer(request);
    }
}
