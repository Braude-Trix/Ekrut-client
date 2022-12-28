package gui.workers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
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
public class InventoryReportPopupGui implements Initializable {
    public static InventoryReportPopupGui controller;
    public static String machineName;
    public static int year;
    public static int month;

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

        XYChart.Series<String, Integer> belowSeries = getSeries("Below threshold", 31, 40);
        XYChart.Series<String, Integer> unavailableSeries = decreaseSeriesBy(belowSeries, "Unavailable", 0.4);
        productsUnderChart.getData().addAll(belowSeries, unavailableSeries);

        for (XYChart.Series<String, Integer> series : productsUnderChart.getData()) {
            for (XYChart.Data<String, Integer> item : series.getData()) {
                Tooltip.install(item.getNode(), new Tooltip(series.getName() + ": " + item.getYValue()));
            }
        }

        // daily report - loads first day of month
        List<String> snacks = Arrays.asList("Doritos", "Bamba", "Trix", "Magnoom", "Bisli",
                "Waffel", "TimTam", "Popcorn", "Peanuts", "Apropo");
        dailyInventory.getData().addAll(getSnacksReSeries(snacks));
        setDailyInventoryTitle(1);

        for (XYChart.Series<Integer, String> series : dailyInventory.getData()) {
            for (XYChart.Data<Integer, String> item : series.getData()) {
                item.getNode().setOnMousePressed((MouseEvent event) -> {
                    System.out.println("you clicked " + item.toString() + series.toString());
                });
                Tooltip.install(item.getNode(), new Tooltip(series.getName() + ": " + item.getXValue()));
            }
        }
        // setting slider size
        daySlider.setMax(ReportPopupUtils.getDaysOfMonth(month, year));

        refreshBtn.setOnMouseClicked(event -> onRefreshClicked());
    }

    private static XYChart.Series<String, Integer> getSeries(String name, int xRange, int yRange) {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName(name);

        for (int i = 1; i <= xRange; i++) {
            Integer yValue = new Random().nextInt(yRange + 1);
            series.getData().add(new XYChart.Data<>(String.valueOf(i), yValue));
        }

        return series;
    }

    private static XYChart.Series<String, Integer> decreaseSeriesBy(XYChart.Series<String, Integer> series,
                                                                    String name,
                                                                    Double upToPresent) {
        XYChart.Series<String, Integer> decSeries = new XYChart.Series<>();
        decSeries.setName(name);

        for (int i = 0; i < series.getData().size(); i++) {
            Integer yValue = (int) Math.floor(series.getData().get(i).getYValue() * upToPresent);
            decSeries.getData().add(new XYChart.Data<>(series.getData().get(i).getXValue(), yValue));
        }

        return decSeries;
    }

    private static List<XYChart.Series<String, Integer>> getSnacksSeries(List<String> names) {
        List<XYChart.Series<String, Integer>> seriesList = new ArrayList<>();
        for (String name : names) {
            seriesList.add(getSeries(name, 1, 50));
        }
        return seriesList;
    }

// ** reversed bar ** //


    private static XYChart.Series<Integer, String> getReSeries(String name, int xRange, int yRange) {
        XYChart.Series<Integer, String> series = new XYChart.Series<>();
        series.setName(name);

        for (int i = 1; i <= yRange; i++) {
            Integer xValue = new Random().nextInt(xRange + 1);
            series.getData().add(new XYChart.Data<>(xValue, ""));
        }
        return series;
    }


    private static List<XYChart.Series<Integer, String>> getSnacksReSeries(List<String> names) {
        List<XYChart.Series<Integer, String>> seriesList = new ArrayList<>();
        for (String name : names) {
            seriesList.add(getReSeries(name, 50, 1));
        }
        return seriesList;
    }

    private void onRefreshClicked() {
        System.out.println("Refresh was clicked" + " slider with the value: " + daySlider.getValue());
        setDailyInventoryTitle(Double.valueOf(daySlider.getValue()).intValue());
    }

    private void setDailyInventoryTitle(int day) {
        dailyInventory.setTitle("Daily Inventory - day: " + day);
    }
}
