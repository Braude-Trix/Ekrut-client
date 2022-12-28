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
import javafx.scene.input.MouseEvent;
import utils.ReportPopupUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Gui controller for presenting inventory report window (popup)
 */
public class OrdersReportPopupGui implements Initializable {
    public static OrdersReportPopupGui controller;
    public static int year;
    public static int month;
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
        closeBtn.setOnMouseClicked(event -> ReportPopupUtils.onCloseClicked());

        // setting title
        titleUpper.setText("Orders Report | " + RegionalGui.region);
        ReportPopupUtils.setSubTitle(titleInfo, month, year);

        // setting stacked bar chart
        List<String> machines = Arrays.asList("L 606", "M 203", "EF 606", "P 103", "K 404",
                "Cafeteria", "Greg", "Falafel");
        dailyOrdersStackedBar.getData().addAll(getMachinesSeries(machines));

        for (XYChart.Series<String, Integer> series : dailyOrdersStackedBar.getData()) {
            for (XYChart.Data<String, Integer> item : series.getData()) {
                item.getNode().setOnMousePressed((MouseEvent event) -> {
                    System.out.println("you clicked " + item.toString() + series.toString());
                });
                Tooltip.install(item.getNode(), new Tooltip(series.getName() + ": " + item.getYValue()));
            }
        }

        // setting pie chart
        int amountOfEk = 260;
        int amountOfLatePickup = 80;
        int ekPercentage = (int) Math.floor((100.0 / (amountOfEk + amountOfLatePickup)) * amountOfEk);
        int latePickupPercentage = (int) Math.ceil((100.0 / (amountOfEk + amountOfLatePickup)) * amountOfLatePickup);
        PieChart.Data ekPercentageData = new PieChart.Data("EK (Localy)", ekPercentage);
        PieChart.Data latePickupPercentageData = new PieChart.Data("Late-pickup (OL)", latePickupPercentage);
        monthlyOrdersPie.getData().addAll(ekPercentageData, latePickupPercentageData);
        monthlyOrdersPie.setLabelsVisible(false);

        for (PieChart.Data series : monthlyOrdersPie.getData()) {
            series.getNode().setOnMousePressed((MouseEvent event) -> {
                System.out.println("x: " + event.getSceneX() + "y: " + event.getSceneY());
            });
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

        for (int i = 1; i <= xRange; i++) {
            Integer yValue = new Random().nextInt(yRange + 1);
            series.getData().add(new XYChart.Data<>(String.valueOf(i), yValue));
            sum += yValue;
        }

        monthlyOrdersList.add(new OrdersAmount(name, sum));
        return series;
    }

    private List<XYChart.Series<String, Integer>> getMachinesSeries(List<String> names) {
        List<XYChart.Series<String, Integer>> seriesList = new ArrayList<>();
        for (String name : names) {
            seriesList.add(getSeries(name, 31, 100));
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
