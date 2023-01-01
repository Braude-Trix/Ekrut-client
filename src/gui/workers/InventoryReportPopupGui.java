package gui.workers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
import models.InventoryReport;
import utils.ReportPopupUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Gui controller for presenting inventory report window (popup)
 */
public class InventoryReportPopupGui implements Initializable {
    public static InventoryReportPopupGui controller;
    public static String machineName;
    public static int year;
    public static int month;
    public static InventoryReport inventoryReportData;
    private List<List<XYChart.Series<Integer, String>>> allDaysInventory;
    private int presentedDay;

    @FXML
    private Button closeBtn;

    @FXML
    private Label titleInfo;

    @FXML
    private BarChart<Integer, String> dailyInventory;

    @FXML
    private LineChart<String, Integer> productsUnderChart;

    @FXML
    private Slider daySlider;

    @FXML
    private Button refreshBtn;

    /**
     * Initializing inventory report window
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
        ReportPopupUtils.setSubTitle(titleInfo, machineName, month, year);

        // Using passed InventoryReport data object
        XYChart.Series<String, Integer> belowSeries = getBelowSeries();
        XYChart.Series<String, Integer> unavailableSeries = getUnavailableSeries();
        productsUnderChart.getData().addAll(belowSeries, unavailableSeries);

        for (XYChart.Series<String, Integer> series : productsUnderChart.getData()) {
            for (XYChart.Data<String, Integer> item : series.getData()) {
                Tooltip.install(item.getNode(), new Tooltip(series.getName() + ": " + item.getYValue()));
            }
        }

        allDaysInventory = getDailyInventory();
        initDailyInventory();

        // setting slider size
        daySlider.setMax(ReportPopupUtils.getDaysOfMonth(month, year));
        daySlider.setOnMouseReleased(event -> onMouseReleaseSlider());

        refreshBtn.setDisable(true);
        refreshBtn.setOnMouseClicked(event -> Platform.runLater(this::onRefreshClicked));
    }

    private static XYChart.Series<String, Integer> getBelowSeries() {
        List<Integer> belowThresholdAmount = inventoryReportData.getBelowThresholdAmount();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Below threshold");

        for (int i = 0; i < belowThresholdAmount.size(); i++) {
            Integer yValue = belowThresholdAmount.get(i);
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), yValue));
        }

        return series;
    }

    private static XYChart.Series<String, Integer> getUnavailableSeries() {
        List<Integer> unavailableAmount = inventoryReportData.getUnavailableAmount();
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName("Unavailable");

        for (int i = 0; i < unavailableAmount.size(); i++) {
            Integer yValue = unavailableAmount.get(i);
            series.getData().add(new XYChart.Data<>(String.valueOf(i + 1), yValue));
        }

        return series;
    }

    private static List<List<XYChart.Series<Integer, String>>> getDailyInventory() {
        List<Map<String, Integer>> dailyInventory = inventoryReportData.getDailyInventory();
        // list of all days -> list of XYChart.Series (product and its value)
        List<List<XYChart.Series<Integer, String>>> allDaysSeriesList = new ArrayList<>();
        int indexOfDay = 0;
        for (Map<String, Integer> productsOfDay : dailyInventory) { // iterating over all days
            allDaysSeriesList.add(new ArrayList<>());
            for (Map.Entry<String, Integer> entry : productsOfDay.entrySet()) { // iterating over all products
                XYChart.Series<Integer, String> seriesForProduct = new XYChart.Series<>();
                seriesForProduct.setName(entry.getKey());
                seriesForProduct.getData().add(new XYChart.Data<>(entry.getValue(), ""));
                allDaysSeriesList.get(indexOfDay).add(seriesForProduct);
            }
            indexOfDay++;
        }
        return allDaysSeriesList;
    }

    private void initDailyInventory() {
        if (allDaysInventory.size() >= 1) {
            dailyInventory.getData().addAll(allDaysInventory.get(0));
            setDailyInventoryTitle(1);
            installDaysTooltip();
        }
    }

    private void installDaysTooltip() {
        for (XYChart.Series<Integer, String> series : dailyInventory.getData()) {
            for (XYChart.Data<Integer, String> item : series.getData()) {
                Tooltip.install(item.getNode(), new Tooltip(series.getName() + ": " + item.getXValue()));
            }
        }
    }

    private void onMouseReleaseSlider() {
        int day = Double.valueOf(daySlider.getValue()).intValue();
        refreshBtn.setDisable(day == presentedDay);
    }

    private void onRefreshClicked() {
        int day = Double.valueOf(daySlider.getValue()).intValue();
        if (day == presentedDay) {
            return;
        }
        List<XYChart.Series<Integer, String>> newDayInventory;
        if (allDaysInventory.size() >= day)
            newDayInventory = allDaysInventory.get(day - 1);
        else
            newDayInventory = new ArrayList<>();

        ObservableList<XYChart.Series<Integer, String>> observableList = FXCollections.observableList(newDayInventory);
        dailyInventory.setData(observableList);
        installDaysTooltip();
        setDailyInventoryTitle(day);
        refreshBtn.setDisable(true);
    }

    private void setDailyInventoryTitle(int day) {
        dailyInventory.setTitle("Daily Inventory - day: " + day);
        presentedDay = day;
    }
}
