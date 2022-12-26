package gui.workers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.input.MouseEvent;
import utils.ReportPopupUtils;

import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Gui controller for presenting users report window (popup)
 */
public class UsersReportPopupGui implements Initializable {
    public static UsersReportPopupGui controller;
    public static int year;
    public static int month;
    public static ObservableList<userTableData> top3UserList = FXCollections.observableArrayList();

    @FXML
    private Button closeBtn;

    @FXML
    private Label titleUpper;

    @FXML
    private Label titleInfo;

    @FXML
    private BarChart<String, Integer> usersActivityLevelChart;

    @FXML
    private TableView<userTableData> top3ClientsTable;

    @FXML
    private TableColumn<userTableData, String> ClientCol;

    @FXML
    private TableColumn<userTableData, Integer> OrdersCol;

    @FXML
    private TableColumn<userTableData, Integer> IDCol;

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
        titleUpper.setText("Users Report | " + RegionalGui.region);
        ReportPopupUtils.setSubTitle(titleInfo, month, year);

        // setting usersActivityLevelChart
        XYChart.Series<String, Integer> userSeries = getSeries("Users activity level", 10, 50);
        usersActivityLevelChart.getData().addAll(userSeries);

        // setting values in table with tooltip
        for (XYChart.Series<String, Integer> series : usersActivityLevelChart.getData()) {
            for (XYChart.Data<String, Integer> item : series.getData()) {
                Tooltip.install(item.getNode(), new Tooltip("Users in current level: " + item.getYValue()));
            }
        }

        // modify style (color) of series lines
        usersActivityLevelChart.getData().get(0).getChart().setStyle("-fx-stroke: #a3b68d;");

        // setting pieChart for distribution between clients and subscribes
        int amountOfClients = 200;
        int amountOfSubscribres = 185;
        int clientsPrecentage = (int) Math.floor((100.0 / (amountOfClients + amountOfSubscribres)) * amountOfClients);
        int subsPrecentage = (int) Math.floor((100.0 / (amountOfClients + amountOfSubscribres)) * amountOfSubscribres);
        PieChart.Data clientsPrecentageData = new PieChart.Data("Clients", clientsPrecentage);
        PieChart.Data subsPrecentageData = new PieChart.Data("Subscribers", subsPrecentage);
        PieClients.getData().addAll(clientsPrecentageData, subsPrecentageData);
        PieClients.setLabelsVisible(false);

        // adding tooltip for data in piechart
        for (PieChart.Data series : PieClients.getData()) {
            series.getNode().setOnMousePressed((MouseEvent event) -> {
                System.out.println("x: " + event.getSceneX() + "y: " + event.getSceneY());
            });
            Tooltip.install(series.getNode(), new Tooltip(series.getName() + ": " + series.getPieValue() + "%"));
        }

        // setting top 3 users table
        IDCol.setCellValueFactory(new PropertyValueFactory<>("userID"));
        ClientCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
        OrdersCol.setCellValueFactory(new PropertyValueFactory<>("ordersAmount"));
        top3ClientsTable.setItems(top3UserList);
        top3ClientsTable.getItems().clear();

        userTableData userNo1 = new userTableData(812002611, "Jack Oswald White", 8181);
        top3ClientsTable.getItems().add(userNo1);
    }


    private static XYChart.Series<String, Integer> getSeries(String name, int xRange, int yRange) {
        XYChart.Series<String, Integer> series = new XYChart.Series<>();
        series.setName(name);

        for (int i = 1; i <= xRange; i++) {
            Integer yValue = new Random().nextInt(yRange + 1);
            String xValue = String.format("%s-%s", i * 100, (i + 1) * 100);
            series.getData().add(new XYChart.Data<>(xValue, yValue));
        }
        return series;
    }

    public static class userTableData {

        private final String userName;
        private final Integer userID;
        private final Integer ordersAmount;

        public userTableData(Integer UserID, String UserName, Integer OrdersAmount) {
            this.userName = UserName;
            this.userID = UserID;
            this.ordersAmount = OrdersAmount;
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

