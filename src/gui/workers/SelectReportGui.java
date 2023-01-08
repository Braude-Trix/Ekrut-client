package gui.workers;

import client.Client;
import client.ClientUI;
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
import models.Request;
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
import java.util.Random;

import static gui.workers.RegionalManagerGui.VALIDATION_ERROR_MSG;

/**
 * Gui controller for presenting different types of reports for regional manger window
 */
public class SelectReportGui {
    public static Stage popupDialog;
    private static ReportType reportType;
    private ComboBox<String> machineComboBox;
    private ComboBox<String> yearComboBox;
    private ComboBox<String> monthComboBox;
    private List<ComboBox<String>> comboBoxList;
    private Button viewButton;
    private Label errLabel;
    private List<Machine> machinesSet;

    private enum ReportType {
        INVENTORY, ORDERS, USERS
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
        //List<String> dummyForMachine = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        machineComboBox = new ComboBox();//FXCollections.observableList(dummyForMachine));
        setMachinesNameComboBox(RegionalManagerGui.region);
        machineComboBox.setPromptText("Choose Machine");
        machineSelectVBox.getChildren().addAll(machineSelectLabel, machineComboBox);

        VBox dateSelectVBox = getDateSelectVBox();

        contentVBox.getChildren().addAll(machineSelectVBox, dateSelectVBox);
        comboBoxList = Arrays.asList(machineComboBox, yearComboBox, monthComboBox);

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
		ClientUI.chat.accept(request);// sending the request to the server.

		handleResponseGetMachines();
	}

	private void handleResponseGetMachines() {
		//Client.resFromServer.setCode(ResponseCode.SERVER_ERROR);
		switch (Client.resFromServer.getCode()) {
		case OK:
			updateMachines((Client.resFromServer.getBody()));
			break;
		default:
			errLabel = WorkerNodesUtils.getErrorLabel(Client.resFromServer.getDescription());
			//RegionalManagerGui.controller.bottomBroderVbox.getChildren().add(errLabel);
			break;
		}
	}

	private void updateMachines(List<Object> listMachine) {
		machinesSet.clear();
		if (listMachine == null) {
			machineComboBox.setDisable(true);
			errLabel = WorkerNodesUtils.getErrorLabel("There are no machines in this region at the moment");
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
        comboBoxList = Arrays.asList(yearComboBox, monthComboBox);

        // View report button
        VBox buttonVBox = getViewReportVBox();

        nodes.addAll(contentVBox);
    }

    void loadUsersReport() {
        reportType = ReportType.USERS;
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", RegionalManagerGui.controller.bgImage); // todo: BADIHI HELP US...
        // Replacing top border
        WorkerNodesUtils.setTitle("Users Report", RegionalManagerGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalManagerGui.controller.centerBroderVbox.getChildren();

        VBox contentVBox = getContentVBox();
        VBox dateSelectVBox = getDateSelectVBox();

        contentVBox.getChildren().add(dateSelectVBox);
        comboBoxList = Arrays.asList(yearComboBox, monthComboBox);

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
        monthComboBox = new ComboBox(FXCollections.observableList(dummyForMonth));
        monthComboBox.setPromptText("Month");
        List<String> dummyForYear = WorkerNodesUtils.getListInRange(2018, Year.now().getValue());
        yearComboBox = new ComboBox(FXCollections.observableList(dummyForYear));
        yearComboBox.setPromptText("Year");
        yearComboBox.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            setMaxMonthWhenCurrentYear(Integer.parseInt(newValue));
        });

        dateHBox.getChildren().addAll(monthComboBox, yearComboBox);

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
                switch (reportType) { // todo: query the DB for report here...
                    case INVENTORY:
                        openReportPopup("/assets/workers/fxmls/InventoryReportPage.fxml");
                        break;
                    case ORDERS:
                        openReportPopup("/assets/workers/fxmls/OrdersReportPage.fxml");
                        break;
                    case USERS:
                        openReportPopup("/assets/workers/fxmls/UsersReportPage.fxml");
                        break;
                }
            }
        }));

        return bottomBroderVbox;
    }

    private void openReportPopup(String fxmlPath) {
        Stage primaryStage = StageSingleton.getInstance().getStage();
        popupDialog = new Stage();
        popupDialog.initModality(Modality.APPLICATION_MODAL);
        popupDialog.initStyle(StageStyle.UNDECORATED);
        popupDialog.initOwner(primaryStage);

        AnchorPane anchorPane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(fxmlPath));
            setInitValuesInReportsPopup();
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

    private void setInitValuesInReportsPopup() {
        switch (reportType) {
            case INVENTORY:
                InventoryReportPopupGui.machineName = machineComboBox.getValue();
                InventoryReportPopupGui.year = Integer.parseInt(yearComboBox.getValue());
                InventoryReportPopupGui.month = Integer.parseInt(monthComboBox.getValue());
                InventoryReportPopupGui.inventoryReportData = getDummyInventoryReport();
                break;
            case ORDERS:
                OrdersReportPopupGui.year = Integer.parseInt(yearComboBox.getValue());
                OrdersReportPopupGui.month = Integer.parseInt(monthComboBox.getValue());
                OrdersReportPopupGui.ordersReportData = getDummyOrdersReport();
                break;
            case USERS:
                UsersReportPopupGui.year = Integer.parseInt(yearComboBox.getValue());
                UsersReportPopupGui.month = Integer.parseInt(monthComboBox.getValue());
                UsersReportPopupGui.usersReportData = getDummyUsersReport();
                break;
        }
    }

    private InventoryReport getDummyInventoryReport() {
        List<Map<String, Integer>> dailyInventory = new ArrayList<>();
        for (int i = 1; i <= 30; i++) {
            Map<String, Integer> productInventory = new HashMap<>();
            productInventory.put("aaa" + i, new Random().nextInt(10 + 1));
            productInventory.put("bbb" + i, new Random().nextInt(10 + 1));
            productInventory.put("ccc" + i, new Random().nextInt(10 + 1));
            dailyInventory.add(productInventory);
        }

        List<Integer> belowThresholdAmount = new ArrayList<>();
        List<Integer> unavailableAmount = new ArrayList<>();

        for (int i = 1; i <= 30; i++) {
            int yValue = new Random().nextInt(40 + 1);
            belowThresholdAmount.add(yValue);
            unavailableAmount.add((int) (yValue * 0.3));
        }

        return new InventoryReport(machineComboBox.getValue(), monthComboBox.getValue(), yearComboBox.getValue(),
                dailyInventory, belowThresholdAmount, unavailableAmount);
    }

    private OrdersReport getDummyOrdersReport() {
        List<Map<String, Integer>> ekOrders = new ArrayList<>();
        List<Map<String, Integer>> latePickupOrders = new ArrayList<>();
        /*for (int i=1; i<=15; i++) {
            Map<String, Integer> ekOrdersDaily = new HashMap<>();
            ekOrdersDaily.put("L 606", 0);
            ekOrdersDaily.put("M 101", 0);
            ekOrdersDaily.put("EM 302" , 0);
            ekOrders.add(ekOrdersDaily);
        }*/
        
        for (int i=1; i<=30; i++) {
            Map<String, Integer> ekOrdersDaily = new HashMap<>();
            ekOrdersDaily.put("L 606", new Random().nextInt(30 + 1));
            ekOrdersDaily.put("M 101", new Random().nextInt(30 + 1));
            ekOrdersDaily.put("EM 302" , new Random().nextInt(30 + 1));
            ekOrders.add(ekOrdersDaily);
        }

       /* for (int i=1; i<=15; i++) {
            Map<String, Integer> latePickupOrdersDaily = new HashMap<>();
            latePickupOrdersDaily.put("L 606",0);
            latePickupOrdersDaily.put("M 101", 0);
            latePickupOrdersDaily.put("EM 302", 0);
            latePickupOrders.add(latePickupOrdersDaily);
        }*/
        
        for (int i=1; i<=30; i++) {
            Map<String, Integer> latePickupOrdersDaily = new HashMap<>();
            latePickupOrdersDaily.put("L 606", new Random().nextInt(15 + 1));
            latePickupOrdersDaily.put("M 101", new Random().nextInt(15 + 1));
            latePickupOrdersDaily.put("EM 302", new Random().nextInt(15 + 1));
            latePickupOrders.add(latePickupOrdersDaily);
        }
        
        return new OrdersReport(RegionalManagerGui.region.toString(), monthComboBox.getValue(), yearComboBox.getValue(),
                ekOrders, latePickupOrders);
    }

    private UsersReport getDummyUsersReport() {
        Map<String, Integer> clientsOrders = getOrders();
        Map<String, Integer> subscribersOrders = getOrders1();
        List<String> top3ClientNames = Arrays.asList("Yuval", "Fisher", "Avihay");

        return new UsersReport(RegionalManagerGui.region.toString(), monthComboBox.getValue(), yearComboBox.getValue(),
                clientsOrders, subscribersOrders, top3ClientNames);
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
        String oldSelectedMonth = monthComboBox.getValue();
        monthComboBox.getItems().clear();
        if (selectedYear == Year.now().getValue()) { // if current year
            int currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
            monthComboBox.getItems().addAll(WorkerNodesUtils.getListInRange(1, currentMonth));
        } else {
            monthComboBox.getItems().addAll(WorkerNodesUtils.getListInRange(1, 12));
        }

        // restoring user last selection
        if (monthComboBox.getItems().contains(oldSelectedMonth)) {
            monthComboBox.getSelectionModel().select(oldSelectedMonth);
        }
    }

    private static VBox getContentVBox() {
        VBox contentVBox = new VBox();
        contentVBox.setAlignment(Pos.CENTER);
        VBox.setVgrow(contentVBox, Priority.ALWAYS);
        contentVBox.setSpacing(20);
        return contentVBox;
    }
}
