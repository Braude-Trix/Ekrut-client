package gui.workers;

import client.RegionalManager;
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
import utils.ColorsAndFonts;
import utils.WorkerNodesUtils;

import java.io.IOException;
import java.time.Year;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import static gui.workers.RegionalGui.VALIDATION_ERROR_MSG;

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

    private enum ReportType {
        INVENTORY, ORDERS, USERS
    }

    void loadInventoryReport() {
        reportType = ReportType.INVENTORY;
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/InventoryReportMenu.jpg", RegionalGui.controller.bgImage);
        // Replacing top border
        WorkerNodesUtils.setTitle("Inventory Report", RegionalGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalGui.controller.centerBroderVbox.getChildren();

        VBox contentVBox = getContentVBox();

        // Vbox for choosing machine
        VBox machineSelectVBox = new VBox();
        machineSelectVBox.setAlignment(Pos.CENTER);
        machineSelectVBox.setSpacing(15);
        Label machineSelectLabel = new Label("Select the machine you wish to view report on");
        machineSelectLabel.setFont(ColorsAndFonts.CONTENT_FONT);

        // Create a combo box
        List<String> dummyForMachine = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday");
        machineComboBox = new ComboBox(FXCollections.observableList(dummyForMachine));
        machineComboBox.setPromptText("Choose Machine");
        machineSelectVBox.getChildren().addAll(machineSelectLabel, machineComboBox);

        VBox dateSelectVBox = getDateSelectVBox();

        contentVBox.getChildren().addAll(machineSelectVBox, dateSelectVBox);
        comboBoxList = Arrays.asList(machineComboBox, yearComboBox, monthComboBox);

        // View report button
        VBox buttonVBox = getViewReportVBox();

        nodes.addAll(contentVBox);
    }

    void loadOrdersReport() {
        reportType = ReportType.ORDERS;
        // Replacing background
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", RegionalGui.controller.bgImage);
        // Replacing top border
        WorkerNodesUtils.setTitle("Orders Report", RegionalGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalGui.controller.centerBroderVbox.getChildren();

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
        WorkerNodesUtils.setBackground("/assets/workers/OrdersReportMenu.jpg", RegionalGui.controller.bgImage); // todo: BADIHI HELP US...
        // Replacing top border
        WorkerNodesUtils.setTitle("Users Report", RegionalGui.controller.topBorderVBox);

        // Replacing center border
        ObservableList<Node> nodes = RegionalGui.controller.centerBroderVbox.getChildren();

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
        VBox bottomBroderVbox = RegionalGui.controller.bottomBroderVbox;
        viewButton = new Button("View Report");
        viewButton.setPrefSize(200, 30);
        bottomBroderVbox.getChildren().add(viewButton);

        viewButton.setOnMouseClicked((event -> {
            if (bottomBroderVbox.getChildren().size() > 1) { // removing old error label
                bottomBroderVbox.getChildren().remove(1);
            }
            if (!validateFormValue()) {
                Label errLabel = WorkerNodesUtils.getErrorLabel(VALIDATION_ERROR_MSG);
                bottomBroderVbox.getChildren().add(errLabel);
            } else { // all form data is validated
                switch (reportType) { // todo: query the DB for report here...
                    case INVENTORY:
                        openReportPopup("/assets/workers/InventoryReportPage.fxml");
                        break;
                    case ORDERS:
                        openReportPopup("/assets/workers/OrdersReportPage.fxml");
                        break;
                    case USERS:
                        openReportPopup("/assets/workers/UsersReportPage.fxml");
                        break;
                }
            }
        }));

        return bottomBroderVbox;
    }

    private void openReportPopup(String fxmlPath) {
        popupDialog = new Stage();
        popupDialog.initModality(Modality.APPLICATION_MODAL);
        popupDialog.initOwner(RegionalManager.primaryStage);

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
    }

    private void setInitValuesInReportsPopup() {
        switch (reportType) {
            case INVENTORY:
                InventoryReportPopupGui.machineName = machineComboBox.getValue();
                InventoryReportPopupGui.year = Integer.parseInt(yearComboBox.getValue());
                InventoryReportPopupGui.month = Integer.parseInt(monthComboBox.getValue());
                break;
            case ORDERS:
                OrdersReportPopupGui.year = Integer.parseInt(yearComboBox.getValue());
                OrdersReportPopupGui.month = Integer.parseInt(monthComboBox.getValue());
                break;
            case USERS:
                UsersReportPopupGui.year = Integer.parseInt(yearComboBox.getValue());
                UsersReportPopupGui.month = Integer.parseInt(monthComboBox.getValue());
                break;
        }
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
