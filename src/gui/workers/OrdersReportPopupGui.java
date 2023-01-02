package gui.workers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import models.OrdersReport;
import utils.ReportPopupUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Gui controller for presenting inventory report window (popup)
 */
public class OrdersReportPopupGui implements Initializable {
    public static OrdersReportPopupGui controller;
    public static int year;
    public static int month;
    public static OrdersReport ordersReportData;
    private static ObservableList<OrdersAmount> monthlyOrdersList = FXCollections.observableArrayList();

    @FXML
    private Button closeBtn;

    @FXML
    private Label titleUpper;

    @FXML
    private Label titleInfo;

    @FXML
    private NumberAxis dailyOrders;

    @FXML
    private StackedBarChart<String, Integer> dailyOrdersStackedBar;

    @FXML
    private PieChart monthlyOrdersPie;

    @FXML
    private TableView<OrdersAmount> monthlyOrdersTable;

    @FXML
    private TableColumn<OrdersAmount, String> machineNameCol;

    @FXML
    private TableColumn<OrdersAmount, Integer> ordersCol;

    /**
     * Initializing orders report window
     *
     * @param location  The location used to resolve relative paths for the root object, or
     *                  {@code null} if the location is not known.
     * @param resources The resources used to localize the root object, or {@code null} if
     *                  the root object was not localized.
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        monthlyOrdersList.clear();
        closeBtn.setOnMouseClicked(event -> ReportPopupUtils.onCloseClicked());

        // setting title
        titleUpper.setText("Orders Report | " + ordersReportData.getRegion());
        ReportPopupUtils.setSubTitle(titleInfo, month, year);


        // setting stacked bar chart
        List<String> machines = new ArrayList<>();
        for (Map.Entry<String, Integer> machine : ordersReportData.getMonthlyOrders().entrySet()) {
            machines.add(machine.getKey());
        }
        dailyOrdersStackedBar.getData().addAll(getMachinesSeries(machines, ordersReportData.getMonthlyOrders()));

        // adding tooltip for slots in left graph
        for (XYChart.Series<String, Integer> series : dailyOrdersStackedBar.getData()) {
            for (XYChart.Data<String, Integer> item : series.getData()) {
                Tooltip.install(item.getNode(), new Tooltip(series.getName() + ": " + item.getYValue()));
            }
        }

        // setting pie chart of percentage
        double ekPercentage = ordersReportData.getEkOrdersPercentage();
        double latePickupPercentage = ordersReportData.getLatePickupOrdersPercentage();
        PieChart.Data ekPercentageData = new PieChart.Data("EK (Locally)", ekPercentage);
        PieChart.Data latePickupPercentageData = new PieChart.Data("Late-pickup (OL)", latePickupPercentage);
        monthlyOrdersPie.getData().addAll(ekPercentageData, latePickupPercentageData);
        monthlyOrdersPie.setLabelsVisible(false);

        // adding tooltip for pie chart
        for (PieChart.Data series : monthlyOrdersPie.getData()) {
            Tooltip.install(series.getNode(), new Tooltip(series.getName() + ": " + series.getPieValue() + "%"));
        }

        // setting monthly orders table
        machineNameCol.setCellValueFactory(new PropertyValueFactory<>("machineName"));
        ordersCol.setCellValueFactory(new PropertyValueFactory<>("ordersNum"));
        monthlyOrdersTable.setItems(monthlyOrdersList);
    }

    private XYChart.Series<String, Integer> getSeries(String name, int xRange, int yRange) {
        // calculating sum of monthly orders for table
        int sum = 0;

        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName(name);

        // for each day from 1 to xRange we set each day with some amount of yRange
        for (int i = 1; i <= xRange; i++) {
            Integer yValue = 0;
            // getting the amount of ekOrders for each day
            Map<String, Integer> ekDay = ordersReportData.getEkOrders().get(i - 1);
            yValue += ekDay.get(name);

            // getting the amount of lateOrders for each day
            Map<String, Integer> lateDay = ordersReportData.getLatePickupOrders().get(i - 1);
            yValue += lateDay.get(name);

            //Integer yValue = new Random().nextInt(yRange + 1);
            series.getData().add(new XYChart.Data<>(String.valueOf(i), yValue));
            sum += yValue;
        }

        monthlyOrdersList.add(new OrdersAmount(name, sum));
        return series;
    }

    private List<XYChart.Series<String, Integer>> getMachinesSeries(List<String> names, Map<String, Integer> monthlyOrders) {
        List<XYChart.Series<String, Integer>> seriesList = new ArrayList<>();
        // for each machine with monthlyOrders amount we check send a machines name and amount
        for (Map.Entry<String, Integer> entry : monthlyOrders.entrySet()) {
            String machineName = entry.getKey();
            Integer amountMonth = entry.getValue();
            if (names.contains(machineName)) {
                seriesList.add(getSeries(machineName, ordersReportData.getEkOrders().size(), amountMonth));
            }
        }
        return seriesList;
    }

    public static class OrdersAmount {
        public String machineName;
        public Integer ordersNum;

        public OrdersAmount(String machineName, Integer ordersNum) {
            this.machineName = machineName;
            this.ordersNum = ordersNum;
        }

        public String getMachineName() {
            return machineName;
        }

        public Integer getOrdersNum() {
            return ordersNum;
        }
    }
}
