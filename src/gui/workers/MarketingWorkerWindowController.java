package gui.workers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import client.Client;
import client.ClientUI;
import gui.LoginController;
import gui.SelectOptionWorkerOrCustomer;
import gui.StageSingleton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import models.Method;
import models.Regions;
import models.Request;
import models.Sale;
import models.SaleStatus;
import models.Worker;
import utils.Util;

public class MarketingWorkerWindowController implements Initializable {

	public static Sale previewSale;

	@FXML
	private Button createNewSaleBtn;

	@FXML
	private TableColumn<Sale, String> readyForStartSaleNameCol;

	@FXML
	private TableView<Sale> readyForStartTableView;

	@FXML
	private TableColumn<Sale, String> runningSaleNameCol;

	@FXML
	private TableColumn<Sale, String> runningSalesInfoCol;

	@FXML
	private TableView<Sale> runningSalesTableView;

	@FXML
	private VBox runningSalesVbox;

	@FXML
	private TableColumn<Sale, String> waitingApprovalSaleNameCol;

	@FXML
	private TableView<Sale> waitingForManagerApprovalTableView;

	@FXML
	private Button logOutBtn;

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	private BorderPane readyForStartBorderPane;

	@FXML
	private BorderPane runningSalesBorderPane;

	@FXML
	private Button readySalesBtn;

	@FXML
	private Button runningSalesBtn;
	@FXML
	private ImageView backGroundImage;

	@FXML
	private AnchorPane anchorPaneScreen;

	@FXML
	private TableColumn<Sale, String> runningSaleStartDateCol;

	@FXML
	private TableColumn<Sale, String> runningSaleEndDateCol;

	@FXML
	private TableColumn<Sale, String> readyForStartStartDateCol;

	@FXML
	private TableColumn<Sale, String> readyForStartEndDateCol;

	@FXML
	private Label regionLabel;
	@FXML
	private Label saleDescriptionLabel;
	@FXML
	private Tooltip saleDescriptionToolTip;

	@FXML
	private Label saleEndDateLabel;

	@FXML
	private Label saleNameLabel;

	@FXML
	private Label saleStartDateLabel;

	@FXML
	private Label saleTimeLabel;

	@FXML
	private Label saleTypeLabel;

	@FXML
	private Label saleStatusLabel;
	@FXML
	private Label regionLabel1;
	@FXML
	private Label saleDescriptionLabel1;
	@FXML
	private Tooltip saleDescriptionToolTip1;

	@FXML
	private Label saleEndDateLabel1;

	@FXML
	private Label saleNameLabel1;

	@FXML
	private Label saleStartDateLabel1;

	@FXML
	private Label saleTimeLabel1;

	@FXML
	private Label saleTypeLabel1;
	@FXML
	private Label saleStatusLabel1;

	@FXML
	private TextArea saleDescriptionTxt;
	@FXML
	private TextArea saleDescriptionTxt1;
	@FXML
	private VBox salePreviewSection;
	@FXML
	private VBox salePreviewSection1;
	@FXML
	private VBox statusBoxInSalePreview;

	@FXML
	private VBox statusBoxInSalePreview1;
	@FXML
	private Button refreshBtnReadyTable;

	@FXML
	private Button refreshBtnRunningTable;
	@FXML
	private HBox previewSaleHbox;
	@FXML
	private HBox previewSaleHbox1;

	@FXML
	private VBox previewSaleVbox;
	@FXML
	private Label salePreviewPlaceHolder;
	@FXML
	private Label salePreviewPlaceHolder1;
	@FXML
	private Label usernameLabel;
	@FXML
	private Label usernameRegionLabel;

	@FXML
	private ImageView backBtn;

	public static ObservableList<Sale> runningSales = FXCollections.observableArrayList();
	public static ObservableList<Sale> readySales = FXCollections.observableArrayList();
	private Worker worker = (Worker) LoginController.user;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		initRunningSalesTable();
		initReadyForStartSalesTable();
		addButtonsToReadyToStartTable();
		initButtons();
		setUsernameLabels();
		setBackBtnIfExists();

		saleDescriptionTxt.setStyle("-fx-control-inner-background: #d6dfe8; -fx-border-color:black");
		saleDescriptionTxt1.setStyle("-fx-control-inner-background: #d6dfe8; -fx-border-color:black");

		// handleSalePreview();

	}

	/**
	 * This method sets a scene to a given stage.
	 * 
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {

		Parent root = FXMLLoader.load(getClass().getResource("/assets/workers/MarketingWorkerWindow.fxml"));
		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/MarketingWorkerCss.css").toExternalForm());
		scene.getStylesheets().add(getClass().getResource("/styles/table_style.css").toExternalForm());

		primaryStage.setTitle("Marketing Worker");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setMinHeight(primaryStage.getHeight());
		primaryStage.setMinWidth(primaryStage.getWidth());
		primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});

	}

	private void setBackBtnIfExists() {
		if (LoginController.customerAndWorker != null) {
			backBtn.setVisible(true);
		}

	}

	@FXML
	void Back(MouseEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(SelectOptionWorkerOrCustomer.scene);
		stage.show();
	}

	private void setUsernameLabels() {
		usernameLabel.setText("Hello " + worker.getFirstName() + " " + worker.getLastName());
		usernameRegionLabel.setText(worker.getRegion().toString());
	}

	private void handleSalePreview(String table) {
//		previewSale
		if (previewSale != null) {
			if (table == "runningTable") {
				for (Node node : previewSaleHbox.getChildren()) {
					node.setEffect(null);
				}
				for (Node node : salePreviewSection.getChildren()) {
					node.setEffect(null);
				}
				salePreviewPlaceHolder.setVisible(false);

				if (previewSale.getSaleRegion().equals(Regions.All)) {
					regionLabel.setText(previewSale.getSaleRegion().toString() + " regions.");
				} else {
					regionLabel.setText(previewSale.getSaleRegion().toString());
				}
				saleDescriptionTxt.setText(previewSale.getSaleDiscription());
				saleEndDateLabel.setText(previewSale.getSaleEndDate());
				saleNameLabel.setText(previewSale.getSaleName());
				saleStartDateLabel.setText(previewSale.getSaleStartDate());
				saleTimeLabel.setText(previewSale.getSaleTime().toString());
				saleTypeLabel.setText(previewSale.getSaleType().toString());
				saleStatusLabel.setText(previewSale.getSaleStatus().toString());
				decorateStatus(saleStatusLabel, statusBoxInSalePreview);

			} else if (table == "readyTable") {
				// salePreviewSection1.setVisible(true);
				for (Node node : previewSaleHbox1.getChildren()) {
					node.setEffect(null);
				}
				for (Node node : salePreviewSection1.getChildren()) {
					node.setEffect(null);
				}
				salePreviewPlaceHolder1.setVisible(false);
				if (previewSale.getSaleRegion().equals(Regions.All)) {
					regionLabel1.setText(previewSale.getSaleRegion().toString() + " Regions.");
				} else {
					regionLabel1.setText(previewSale.getSaleRegion().toString());
				}
				saleDescriptionTxt1.setText(previewSale.getSaleDiscription());
				saleEndDateLabel1.setText(previewSale.getSaleEndDate());
				saleNameLabel1.setText(previewSale.getSaleName());
				saleStartDateLabel1.setText(previewSale.getSaleStartDate());
				saleTimeLabel1.setText(previewSale.getSaleTime().toString());
				saleTypeLabel1.setText(previewSale.getSaleType().toString());
				saleStatusLabel1.setText(previewSale.getSaleStatus().toString());
				decorateStatus(saleStatusLabel, statusBoxInSalePreview1);
			}

		}

	}

	private void decorateStatus(Label saleStatusLabel, VBox vbox) {
		switch (previewSale.getSaleStatus()) {
		case Template:
			vbox.setStyle("-fx-background-color: #898f89;");
			break;
		case Ready:
			vbox.setStyle("-fx-background-color: #89cf86;");

			break;
		case Running:
			vbox.setStyle("-fx-background-color: #4A86CB");

			break;
		case Outdated:
			vbox.setStyle("-fx-background-color: #eb7ca3;");

			break;

		default:
			break;
		}

	}

	@SuppressWarnings("unchecked")
	private void initButtons() {
		runningSalesBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event event) {
				mainBorderPane.setVisible(false);
				runningSalesBorderPane.setVisible(true);
				readyForStartBorderPane.setVisible(false);
				backGroundImage.setImage(new Image("/assets/workers/WorkerBackGround.jpeg"));
				runningSalesBtn.setDisable(true);
				readySalesBtn.setDisable(false);
			}

		});

		readySalesBtn.setOnAction(new EventHandler() {

			@Override
			public void handle(Event event) {
				mainBorderPane.setVisible(false);
				runningSalesBorderPane.setVisible(false);
				readyForStartBorderPane.setVisible(true);
				backGroundImage.setImage(new Image("/assets/workers/WorkerBackGround.jpeg"));
				runningSalesBtn.setDisable(false);
				readySalesBtn.setDisable(true);

			}
		});

	}

	private void initReadyForStartSalesTable() {
		readyForStartSaleNameCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleName"));
		readyForStartEndDateCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleEndDate"));
		readyForStartStartDateCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleStartDate"));
		requestReadySales();
		handleReponseGetReadySales();
		readyForStartTableView.setItems(readySales);

	}

	private void initRunningSalesTable() {
		runningSaleNameCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleName"));
		runningSaleStartDateCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleStartDate"));
		runningSaleEndDateCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleEndDate"));
		runningSalesInfoCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleType"));

		requestRunningSales();
		handleReponseGetRunningSales();
		runningSalesTableView.setItems(runningSales);

	}

	public void requestRunningSales() {
		List<Object> body = new ArrayList<>();
		// TODO: set region to be worker's region.
		Regions currentWorkerRegion = worker.getRegion();
		body.add(currentWorkerRegion.toString());
		body.add(SaleStatus.Running.toString());
		Request request = new Request();
		request.setPath("/sales");
		request.setMethod(Method.GET);
		request.setBody(body);
		ClientUI.chat.accept(request);
	}

	private void handleReponseGetRunningSales() {
		switch (Client.resFromServer.getCode()) {
		case OK:
			updateRunningSales(Client.resFromServer.getBody());
			break;
		default:
			System.out.println("error");
			// setErrorPlaceHolder();
			break;
		}
	}

	private void updateRunningSales(List<Object> listOfSalesFromDB) {
		runningSales.clear();
		if (listOfSalesFromDB == null) {
			return;
		}
		for (Object sale : listOfSalesFromDB) {
			if (sale instanceof Sale) {
				runningSales.add((Sale) sale);
			}
		}
	}

	public void requestReadySales() {
		List<Object> body = new ArrayList<>();
		Regions currentWorkerRegion = worker.getRegion();
		body.add(currentWorkerRegion.toString());
		body.add(SaleStatus.Ready.toString());
		Request request = new Request();
		request.setPath("/sales");
		request.setMethod(Method.GET);
		request.setBody(body);
		ClientUI.chat.accept(request);
	}

	private void handleReponseGetReadySales() {
		switch (Client.resFromServer.getCode()) {
		case OK:
			updateReadySales(Client.resFromServer.getBody());
			break;
		default:
			System.out.println("error");
			break;
		}
	}

	private void updateReadySales(List<Object> listOfSalesFromDB) {
		readySales.clear();
		if (listOfSalesFromDB == null) {
			return;
		}
		for (Object sale : listOfSalesFromDB) {
			if (sale instanceof Sale) {
				readySales.add((Sale) sale);
			}
		}
	}

	private void addButtonsToReadyToStartTable() {
		TableColumn<Sale, Void> colBtn = new TableColumn<Sale, Void>("Action");
		Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>> cellFactory = new Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>>() {
			@Override
			public TableCell<Sale, Void> call(final TableColumn<Sale, Void> param) {
				final TableCell<Sale, Void> cell = new TableCell<Sale, Void>() {

					private final Button btn = new Button("Start Sale");

					{
						btn.setOnAction((ActionEvent event) -> {
							// need to change status in db and in table
							Sale toStartSale = getTableView().getItems().get(getIndex());
//							toStartSale.setSaleStatus(SaleStatus.Running);
//							getTableView().getItems().remove(getIndex());
//							runningSales.add(toStartSale);
							requestSaleStart(toStartSale);
							handleReponsePutReadySales(getIndex(), toStartSale);
						});
						btn.getStyleClass().add("readyToStartButton");
					}

					@Override
					public void updateItem(Void item, boolean empty) {
						super.updateItem(item, empty);
						if (empty) {
							setGraphic(null);
						} else {
							setGraphic(btn);
						}
					}
				};
				return cell;
			}
		};
		colBtn.setCellFactory(cellFactory);

		readyForStartTableView.getColumns().add(colBtn);

	}

	public void requestSaleStart(Sale sale) {
		List<Object> body = new ArrayList<>();

		body.add(sale.getSaleOrderId());
		body.add(SaleStatus.Running.toString());
		Request request = new Request();
		request.setPath("/sales");
		request.setMethod(Method.PUT);
		request.setBody(body);
		ClientUI.chat.accept(request);

	}

	private void handleReponsePutReadySales(int rowIndex, Sale sale) {
		switch (Client.resFromServer.getCode()) {
		case OK:
			readyForStartTableView.getItems().remove(rowIndex);
			sale.setSaleStatus(SaleStatus.Running);// changing the sale status also locally in order to save db call.
			runningSales.add(sale);
			runningSalesTableView.setItems(runningSales);
			break;
		default:
			System.out.println("error");
			// setErrorPlaceHolder();
			break;
		}
	}

	public Sale getSalePreview() {
		return previewSale;
	}

	@FXML
	void runningSaleTableRowClicked(MouseEvent event) {
		try {
			if ((runningSalesTableView.getSelectionModel().getSelectedItem() != null)
					&& (runningSalesTableView.getSelectionModel().getSelectedIndex() != -1)) {

				Sale sale = runningSalesTableView.getSelectionModel().getSelectedItem();
				previewSale = sale;
				handleSalePreview("runningTable");

//				showModalWindow();
				runningSalesTableView.getSelectionModel().clearSelection();
			}

//

		} catch (NullPointerException e) {
//				catches null pointer exceptions whenever a user clicks the header row.
		}
	}

	@FXML
	void clickRefreshBtn(ActionEvent event) {
		initReadyForStartSalesTable();
		initRunningSalesTable();
	}

	@FXML
	void readyForStartSaleTableRowClicked(MouseEvent event) {
		try {
			if ((readyForStartTableView.getSelectionModel().getSelectedItem() != null)
					&& (readyForStartTableView.getSelectionModel().getSelectedIndex() != -1)) {
				Sale sale = readyForStartTableView.getSelectionModel().getSelectedItem();
				previewSale = sale;
				handleSalePreview("readyTable");
				// showModalWindow();
				readyForStartTableView.getSelectionModel().clearSelection();

			}
//
		} catch (NullPointerException e) {
//				catches null pointer exceptions whenever a user clicks the header row.
		}
	}

	@FXML
	void logOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());

	}

}