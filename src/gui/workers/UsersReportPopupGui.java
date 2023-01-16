package gui.workers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import models.UsersReport;
import utils.ReportPopupUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Gui controller for presenting users report window (popup)
 */
public class UsersReportPopupGui implements Initializable {
    public static UsersReportPopupGui controller;
    public static int year;
    public static int month;
    public static UsersReport usersReportData;

    @FXML
    private Button closeBtn;

    @FXML
    private Label titleUpper;

    @FXML
    private Label titleInfo;

    @FXML
    private BarChart<String, Integer> usersActivityLevelChart;

    @FXML
    private TableView<UserTableData> top3ClientsTable;

    @FXML
    private TableColumn<UserTableData, String> ClientCol;

    @FXML
    private TableColumn<UserTableData, Integer> OrdersCol;

    @FXML
    private TableColumn<UserTableData, Integer> IDCol;

    @FXML
    private PieChart PieClients;


    /**
     * Initializing Users report window
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // setting close button
        closeBtn.setOnMouseClicked(event -> ReportPopupUtils.onCloseClicked());

        // setting title
        titleUpper.setText("Users Report | " + RegionalManagerGui.region);
        ReportPopupUtils.setSubTitle(titleInfo, month, year);

        // setting usersActivityLevelChart
        usersActivityLevelChart.getData().addAll(getUsersActivitySeries());

        // setting values in table with tooltip
        for (XYChart.Series<String, Integer> series : usersActivityLevelChart.getData()) {
            for (XYChart.Data<String, Integer> item : series.getData()) {
                Tooltip.install(item.getNode(), new Tooltip("Users in current level: " + item.getYValue()));
            }
        }

        // modify style (color) of series lines
        usersActivityLevelChart.getData().get(0).getChart().setStyle("-fx-stroke: #a3b68d;");

        // setting pieChart for distribution between clients and subscribes
        PieChart.Data clientsPercentageData = new PieChart.Data("Clients", usersReportData.getClientsPercentage());
        PieChart.Data subsPercentageData = new PieChart.Data("Subscribers", usersReportData.getSubscribersPercentage());
        PieClients.getData().addAll(clientsPercentageData, subsPercentageData);
        PieClients.setLabelsVisible(false);

        // adding tooltip for data in pie-chart
        for (PieChart.Data series : PieClients.getData()) {
            Tooltip.install(series.getNode(), new Tooltip(series.getName() + ": " + series.getPieValue() + "%"));
        }

        // setting top 3 users table
        IDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        ClientCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        OrdersCol.setCellValueFactory(new PropertyValueFactory<>("ordersAmount"));
        top3ClientsTable.getItems().clear();
        top3ClientsTable.getItems().setAll(getTop3Users());
    }

    private static XYChart.Series<String, Integer> getUsersActivitySeries() {
        Map<String, Integer> usersActivityDistribution = usersReportData.getUsersActivityDistribution();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Users activity level");

        for (Map.Entry<String, Integer> entry : usersActivityDistribution.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        return series;
    }

    private static List<UserTableData> getTop3Users() {
        if (usersReportData.getTop3UserIdAndAmount().isEmpty()) return new ArrayList<>();
        List<UserTableData> tableData = new ArrayList<>();
        int userIndex = 0;
        for (Map.Entry<String, Integer> userIdAndAmountEntry : usersReportData.getTop3UserIdAndAmount().entrySet()) {
            String userName = usersReportData.getTop3ClientNames().get(userIndex++);
            String userId = userIdAndAmountEntry.getKey();
            Integer amount = userIdAndAmountEntry.getValue();

            tableData.add(new UserTableData(Integer.parseInt(userId), userName, amount));
        }
        return tableData;
    }

    /**
     * Class that handles Users orders amount data entity in table for Users report
     */
    public static class UserTableData {

        private final Integer userID;
        private final String userName;
        private final Integer ordersAmount;

        public UserTableData(Integer userID, String userName, Integer ordersAmount) {
            this.userID = userID;
            this.userName = userName;
            this.ordersAmount = ordersAmount;
        }

        public String getUserName() {
            return userName;
        }

        public Integer getUserID() {
            return userID;
        }

        public Integer getOrdersAmount() {
            return ordersAmount;
        }
    }
}

