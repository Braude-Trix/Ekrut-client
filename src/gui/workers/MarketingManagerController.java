package gui.workers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.chrono.ChronoLocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
import models.TimeSale;
import models.TypeSale;
import models.Worker;
import utils.Util;

/**
 * This class represents the side of Marketing Managers in our program.
 * this class is the controller of the MarketingManagerWindow.fxml, it fills the sales template table with data from the server
 * and adds functionality to all it buttons. 
 *
 */
public class MarketingManagerController implements Initializable {
	/**
	 * previewSale is a static object with the type Sale, represents the current clicked sale and the one sale needed to be previewed in the sale preview section.
	 */
	public static Sale previewSale;

	@FXML
	private ComboBox<Regions> comboBoxRegions;

	@FXML
	private ComboBox<TimeSale> comboBoxHoursSale;

	@FXML
	private ComboBox<TypeSale> comboBoxType;

	@FXML
	private BorderPane creatingNewSale;

	@FXML
	private BorderPane mainBorderPane;

	@FXML
	private HBox hboxPercentage;

	@FXML
	private TextArea txtAreaDescription;

	@FXML
	private TextField txtNameSale;

	@FXML
	private TextField txtPercentage;

	@FXML
	private DatePicker EndDate;

	@FXML
	private DatePicker StartDate;

	@FXML
	private ImageView backGroundImage;

	@FXML
	private TableView<Sale> saleTemplatesTableView;
	@FXML
	private TableColumn<Sale, String> saleTemplateNameCol;
	@FXML
	private BorderPane plannedSalesBorderPane;

	@FXML
	private Label endDateErrorLabel;
	@FXML
	private Label nameErrorLabel;
	@FXML
	private Label regionErrorLabel;
	@FXML
	private Label startDateErrorLabel;

	@FXML
	private Label timeErrorLabel;
	@FXML
	private Label typeErrorLabel;
	@FXML
	private Label percentageErrorLabel;

	@FXML
	private Button useSaleTemplateMenuBtn;

	@FXML
	private Button createSaleTemplateMenuBtn;
	@FXML
	private Button refreshBtn;

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
	private TextArea saleDescriptionTxt;
	@FXML
	private VBox statusBoxInSalePreview;
	@FXML
	private VBox salePreviewSection;
	@FXML
	private HBox salePreviewHbox;
	@FXML
	private VBox salePreviewVbox;
	@FXML
	private Label salePreviewPlaceHolder;
	@FXML
	private Label usernameLabel;
	
	 @FXML
	 private Button backBtn;

	
    @FXML
    private Button logoutBtn;

    @FXML
    private HBox descriptionCounterHbox;

    @FXML
    private Label descriptionCounterLabel;

    @FXML
    private Label descriptionCounterLabel1;
    @FXML
    private ImageView refreshImage;


    public static MarketingManagerController controller;

	private Worker worker = (Worker) LoginController.user;

	private boolean selectedTypeWithPercentage = false;
	
	/**
	 * template sales ObserveableList, this is the list with all the template sales brought from db and showed in the template sales tableview.
	 */
	public static ObservableList<Sale> saleTemplateObserableList = FXCollections.observableArrayList();
	public static boolean isCEOLogged = false;

	/**
	 * This Method runs first, initializing the scene, sets form,table and buttons.
	 */
	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		if (isCEOLogged)
            logoutBtn.setVisible(false);
		else
			setBackBtnIfExist();

		setRegionComboBox();
		setHoursComboBox();
		setTypeSaleComboBox();
		setDatePicker();
		initSaleTemplateTable();
		setUsernameLabel();
		addButtonsToSaleTemplatesTable();
		saleDescriptionTxt.setStyle("-fx-control-inner-background: #d6dfe8; -fx-border-color:black");
		setTextFormatterForTextAreaDescription();

	}

private void setTextFormatterForTextAreaDescription() {
	descriptionCounterHbox.setVisible(true);
	
	TextFormatter<String> textFormatter = new TextFormatter<>(change -> 
    change.getControlNewText().length() <= 255 ? change : null);
	
	txtAreaDescription.setTextFormatter(textFormatter);
	//descriptionCounterLabel
	txtAreaDescription.textProperty().addListener((observable, oldValue, newValue) -> {
	    int count = newValue.length();
	    descriptionCounterLabel.setText(String.format("%d", count));
	    if(count>200) {
	    	descriptionCounterLabel.setStyle("-fx-text-fill: red;");
	    	descriptionCounterLabel1.setStyle("-fx-text-fill: red;");
	    }
	    else {
	    	descriptionCounterLabel.setStyle("-fx-text-fill: black;");
	    	descriptionCounterLabel1.setStyle("-fx-text-fill: black;");

	    }
	});
		
	}

	/**
	 * This method sets a scene to a given stage.
	 * 
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
	public void start(Stage primaryStage) throws Exception {
		Parent root = FXMLLoader.load(getClass().getResource("/assets/workers/fxmls/MarketingManagerWindow.fxml"));

		Scene scene = new Scene(root);
		scene.getStylesheets().add(getClass().getResource("/styles/table_style.css").toExternalForm());

		primaryStage.setTitle("Marketing Manager");
		primaryStage.setScene(scene);
		primaryStage.centerOnScreen();
		primaryStage.setResizable(false);
		primaryStage.show();
		primaryStage.setOnCloseRequest(e -> {
			try {
				Util.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});

	}

	private void setBackBtnIfExist() {
		if (LoginController.customerAndWorker != null) {
			backBtn.setVisible(true);
		}

	}
	
	/**
	 * This method is the actual back button, shoots onAction and changes the stage to be the selection page.
	 * @param event - the current event when clicking.
	 */
	@FXML
	void Back(ActionEvent event) {
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setScene(SelectOptionWorkerOrCustomer.scene);
		stage.show();
	}

	private void setUsernameLabel() {
		usernameLabel.setText("Hello " + worker.getFirstName() + " " + worker.getLastName());

	}

	/**
	 * This method initializing the template table by requesting sale templates from
	 * db.
	 */
	private void initSaleTemplateTable() {
		saleTemplateNameCol.setCellValueFactory(new PropertyValueFactory<Sale, String>("saleName"));

		requestSaleTemplates();
		handleReponseGetSales();
		saleTemplatesTableView.setItems(saleTemplateObserableList);

	}

	private void handleReponseGetSales() {
		switch (Client.resFromServer.getCode()) {
		case OK:
			updateSaleTemplates(Client.resFromServer.getBody());
			break;
		default:
			System.out.println(Client.resFromServer.getDescription());
			// setErrorPlaceHolder();
			break;
		}
	}

	private void handleReponsePutSales(int rowIndex) {
		switch (Client.resFromServer.getCode()) {
		case OK:
			saleTemplatesTableView.getItems().remove(rowIndex);
			break;
		default:
			System.out.println(Client.resFromServer.getDescription());
			// setErrorPlaceHolder();
			break;
		}
	}

	private void updateSaleTemplates(List<Object> listOfSalesFromDB) {
		saleTemplateObserableList.clear();
		if (listOfSalesFromDB == null) {
			return;
		}
		for (Object sale : listOfSalesFromDB) {
			if (sale instanceof Sale) {
				saleTemplateObserableList.add((Sale) sale);
			}
		}
	}

	/**
	 * This method adds "Initiate" buttons to the sale template table
	 */
	private void addButtonsToSaleTemplatesTable() {
		TableColumn<Sale, Void> colBtn = new TableColumn<Sale, Void>("Action");
		Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>> cellFactory = new Callback<TableColumn<Sale, Void>, TableCell<Sale, Void>>() {
			@Override
			public TableCell<Sale, Void> call(final TableColumn<Sale, Void> param) {
				final TableCell<Sale, Void> cell = new TableCell<Sale, Void>() {

					private final Button btn = new Button("Initiate");

					{
						btn.setOnAction((ActionEvent event) -> {
							// need to change status in db and in table
							Sale sale = getTableView().getItems().get(getIndex());
							// sale.setSaleStatus(SaleStatus.Ready);
							requestSaleInitiate(sale);
							handleReponsePutSales(getIndex());
							// initSaleTemplateTable();
							// getTableView().getItems().remove(getIndex());
						});
						btn.getStyleClass().add("initiateButton");
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

		saleTemplatesTableView.getColumns().add(colBtn);

	}

	/**
	 * This method navigates the client to the login page and logging him out. This
	 * method runs when the user clicked LogOut.
	 * 
	 * @param event, Description: the current event when the click happened.
	 * @throws Exception, Description: might throw exception.
	 */
	@FXML
	void LogOut(ActionEvent event) throws Exception {
		Util.genricLogOut(getClass());

	}

	/**
	 * This method changes the right side of the screen to the create sale template
	 * form This method runs when the client clicked the button(Create Sale
	 * Template) on the left menu
	 * 
	 * @param event, Description: the current event when the click happened.
	 */
	@FXML
	void creatingSale(ActionEvent event) {
		mainBorderPane.setVisible(false);
		plannedSalesBorderPane.setVisible(false);
		creatingNewSale.setVisible(true);
		backGroundImage.setImage(new Image("/assets/workers/WorkerBackGround.jpeg"));
		useSaleTemplateMenuBtn.setDisable(false);
		createSaleTemplateMenuBtn.setDisable(true);

	}

	/**
	 * This method changes the right side of the screen to the initiate sale screen
	 * This method runs when the client clicked the button(Use Sale Template) on the
	 * left menu
	 * 
	 * @param event, Description: the current event when the click happened.
	 */
	@FXML
	void initiateSaleBtnClick(ActionEvent event) {
		mainBorderPane.setVisible(false);
		creatingNewSale.setVisible(false);
		plannedSalesBorderPane.setVisible(true);
		backGroundImage.setImage(new Image("/assets/workers/WorkerBackGround.jpeg"));

		useSaleTemplateMenuBtn.setDisable(true);
		createSaleTemplateMenuBtn.setDisable(false);

	}

	/**
	 * This method creates new sale with form inputs. This method runs when the
	 * client submitted the form. This method navigates the client back to home
	 * page.
	 * 
	 * @param event, Description: the current event when the click happened.
	 */
	@FXML
	void clickCreateSale(ActionEvent event) {
		removeErrorStyle();
		if (isNotFilled()) {
			return;
		}
		Sale newSaleTemplate = createNewSale();
		saleTemplateObserableList.add(newSaleTemplate);
		saleTemplatesTableView.setItems(saleTemplateObserableList);
		requestAddingNewSaleTemplate(newSaleTemplate);
		// TODO: add newSaleTemplate to DB
		creatingNewSale.setVisible(false);
		//descriptionCounterHbox.setVisible(false);
		plannedSalesBorderPane.setVisible(true);
		useSaleTemplateMenuBtn.setDisable(true);
		createSaleTemplateMenuBtn.setDisable(false);
		initSaleTemplateTable();// refresh sample table
		saleTemplatesTableView.requestFocus();
		saleTemplatesTableView.getSelectionModel().selectLast();
		int numberOfRows = saleTemplatesTableView.getItems().size();
		saleTemplatesTableView.scrollTo(numberOfRows);
		previewSale = newSaleTemplate;
		handleSalePreview();
//		moveHomePage();
		clearTemplateForm();

	}


	/**
	 * This method refreshes the table by refilling it.
	 * @param event - current event when user clicks refresh
	 */
	@FXML
	void clickRefreshBtn(MouseEvent  event) {
		initSaleTemplateTable();
	}

	/**
	 * This method creates a new Request object, in order to add new sale template
	 * to DB.
	 * 
	 * @param sale, Description: recently created sale object, with sale template
	 *              form inputs.
	 */
	public void requestAddingNewSaleTemplate(Sale sale) {
		List<Object> body = new ArrayList<>();
		body.add(sale);
		Request request = new Request();
		request.setPath("/sales");
		request.setMethod(Method.POST);
		request.setBody(body);
		ClientUI.chat.accept(request);

	}

	
	/**
	 * This method requests the template sales data from the server.
	 * creates a request object with body={Region = All, Sale status - Template}
	 * Request Method - Get, Request Path - /sales
	 */
	public void requestSaleTemplates() {
		List<Object> body = new ArrayList<>();

		body.add(Regions.All.toString());
		body.add(SaleStatus.Template.toString());
		Request request = new Request();
		request.setPath("/sales");
		request.setMethod(Method.GET);
		request.setBody(body);
		ClientUI.chat.accept(request);

	}

	/**
	 * This method requests to change sale status (Template->Ready) from the server.
	 * creates a request object with body={Sale id, Sale status - Ready}
	 * Request Method - Put, Request Path - /sales
	 *
	 * @param sale - represents the current sale data where the button "Initiate" was clicked.
	 */
	public void requestSaleInitiate(Sale sale) {
		List<Object> body = new ArrayList<>();

		body.add(sale.getSaleOrderId());
		body.add(SaleStatus.Ready.toString());
		Request request = new Request();
		request.setPath("/sales");
		request.setMethod(Method.PUT);
		request.setBody(body);
		ClientUI.chat.accept(request);

	}

	/**
	 * This method creates new sale with form input This method is being called in
	 * clickCreateSale method.
	 * 
	 * @return Sale newSale object, with form input.
	 */
	private Sale createNewSale() {
		String percentage;
		// changing date format to be normal.
		String startDate = StartDate.getValue().format(DateTimeFormatter.ofPattern(models.StyleConstants.DATE_FORMAT));
		String endDate = EndDate.getValue().format(DateTimeFormatter.ofPattern(models.StyleConstants.DATE_FORMAT));

		percentage = fixNumberPrefix(txtPercentage.getText());
		Sale newSale = new Sale("1", startDate, endDate, comboBoxHoursSale.getValue(), txtNameSale.getText(),
				comboBoxRegions.getValue(), SaleStatus.Template, percentage, txtAreaDescription.getText(),
				comboBoxType.getValue());
		return newSale;
	}

	private String fixNumberPrefix(String percentageString) {
		if (percentageString != null && !percentageString.equals("")) {
			int percentageNum = Integer.parseInt(percentageString);
			percentageString = String.valueOf(percentageNum);
		}
		return percentageString;

	}

	/**
	 * This method clears the create new sale template form.
	 */
	private void clearTemplateForm() {
		txtNameSale.setText("");
		txtPercentage.setText("");
		comboBoxRegions.setValue(null);
		comboBoxHoursSale.setValue(null);
		comboBoxType.setValue(null);
		EndDate.setValue(null);
		EndDate.setDisable(true);
		StartDate.setValue(null);
		txtAreaDescription.setText("");

	}

	/**
	 * This method navigates the user back to home page
	 */
	void moveHomePage() {
		creatingNewSale.setVisible(false);
		mainBorderPane.setVisible(true);
	}

	/**
	 * This method sets the start datePicker in the create sale template form.
	 */
	private void setDatePicker() {
		StartDate.setDayCellFactory(picker -> new DateCell() {
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				LocalDate today = LocalDate.now();

				setDisable(empty || date.compareTo(today) < 0);
			}
		});
	}

	/**
	 * This method sets the end datePicker according to the start datePicker This
	 * method allows the client to choose an end date, starting from the start
	 * datePicker.
	 * 
	 * @param event, Description: the current event when the click happened.
	 */
	@FXML
	void SelectedDate(ActionEvent event) {
		ChronoLocalDate timeNow = LocalDate.from(ZonedDateTime.now());
		if (StartDate.getValue() != null) {
			EndDate.setDayCellFactory(picker -> new DateCell() {
				public void updateItem(LocalDate date, boolean empty) {
					super.updateItem(date, empty);
					LocalDate today; 
					if( StartDate.getValue()!=null) {
						today=StartDate.getValue();
					}
					else {
						today=LocalDate.from(ZonedDateTime.now());
					}

					setDisable(date!=null && today!=null && (empty || date.compareTo(today) < 0));
				}
			});
			if (StartDate.getValue() != null && StartDate.getValue().compareTo(timeNow) >= 0) {
				EndDate.setDisable(false);

			}
		}
	}

	/**
	 * This method sets the values inside the regions comboBox
	 */
	private void setRegionComboBox() {
		List<Regions> list = Arrays.asList(Regions.class.getEnumConstants());
		comboBoxRegions.getItems().addAll(list);
	}

	/**
	 * This method sets the values inside the hours comboBox
	 */
	private void setHoursComboBox() {
		List<TimeSale> list = Arrays.asList(TimeSale.class.getEnumConstants());
		comboBoxHoursSale.getItems().addAll(list);
	}

	/**
	 * This method sets the values inside the type comboBox
	 */
	private void setTypeSaleComboBox() {
		List<TypeSale> list = Arrays.asList(TypeSale.class.getEnumConstants());
		comboBoxType.getItems().addAll(list);
	}

	/**
	 * This method shows and hides the percentage textField, according to the value
	 * selected from Type comboBox
	 * 
	 * @param event, Description: the current event when the click happened.
	 */
	@FXML
	void selectedType(ActionEvent event) {
		TypeSale typeSelected = comboBoxType.getValue();
		if (typeSelected == TypeSale.PercentageDiscount || typeSelected == TypeSale.GetSecondOneAtDiscount) {
			hboxPercentage.setVisible(true);
			selectedTypeWithPercentage = true;
		} else {
			hboxPercentage.setVisible(false);
			selectedTypeWithPercentage = false;
		}
	}

	/**
	 * This method checks if the form isn't fully completed. This method checks each
	 * section of form, if one of them doesn't fit the needed pattern, returns
	 * false.
	 * 
	 * @return boolean false (if form isn't completed), boolean true(if form is
	 *         completed).
	 */
	private boolean isNotFilled() {
		boolean nameSaleIsBlank = isBlankTextField(txtNameSale);
		boolean percentageBoxNotFilledCorrectly = percentageIsOnlyNaturalNumbersUnder99();
		boolean comboBoxesIsNotFilled = isNotFilledComboBoxes();
		boolean datesIsNotFilled = isNotFilledDates();
		return nameSaleIsBlank || percentageBoxNotFilledCorrectly || comboBoxesIsNotFilled || datesIsNotFilled;

	}

	/**
	 * This method checks percentage textField validation.
	 * 
	 * @return boolean false (if percentage isn't a number between 0-99), boolean
	 *         true(if percentage is a number between 0-99).
	 */
	private boolean percentageIsOnlyNaturalNumbersUnder99() {
		if (selectedTypeWithPercentage) {
			if (!isBlankTextField(txtPercentage)) {
				if (txtPercentage.getText().matches("[0-9]+") && txtPercentage.getText().length() < 3) {
					return false;
				}
			}
			txtPercentage.getStyleClass().add("validation-error");
			percentageErrorLabel.setVisible(true);
			return true;// bad input when wanted percentage
		}

		return false;// bad input but type combobox marked no percentage, so return ok
	}

	// TODO:get this method from Utils.
	private boolean isBlankTextField(TextField textField) {
		if (textField.getText().matches("\\p{IsWhite_Space}*")) {
			textField.getStyleClass().add("validation-error");
			nameErrorLabel.setVisible(true);
			return true;
		}
		return false;
	}

	/**
	 * This method cleans form's error labels.
	 */
	private void removeErrorStyle() {
		txtNameSale.getStyleClass().remove("validation-error");
		txtPercentage.getStyleClass().remove("validation-error");
		comboBoxRegions.getStyleClass().remove("validation-error-comboBox");
		comboBoxHoursSale.getStyleClass().remove("validation-error-comboBox");
		comboBoxType.getStyleClass().remove("validation-error-comboBox");
		StartDate.getStyleClass().remove("validation-error");
		EndDate.getStyleClass().remove("validation-error");
		endDateErrorLabel.setVisible(false);
		nameErrorLabel.setVisible(false);
		regionErrorLabel.setVisible(false);
		startDateErrorLabel.setVisible(false);
		timeErrorLabel.setVisible(false);
		typeErrorLabel.setVisible(false);
		percentageErrorLabel.setVisible(false);

//    	errorLabel.setText("");
	}

	/**
	 * This method checks if datepickers are filled correctly(in an available way).
	 * 
	 * @return boolean false(if datepickers are filled incorrectly), boolean true(if
	 *         datepickers are filled correctly).
	 */
	private boolean isNotFilledDates() {

		ChronoLocalDate timeNow = LocalDate.from(ZonedDateTime.now());

		if ((StartDate.getValue() != null && EndDate.getValue() != null)
				&& ((StartDate.getValue().compareTo(timeNow) < 0) && (EndDate.getValue().compareTo(timeNow) < 0))) {
			return false;
		}
		if (StartDate.getValue() == null) {
			StartDate.getStyleClass().add("validation-error");
			startDateErrorLabel.setVisible(true);
		}
		if (EndDate.getValue() == null) {
			endDateErrorLabel.setVisible(true);
			EndDate.getStyleClass().add("validation-error");
		}
		if (StartDate.getValue() != null && StartDate.getValue().compareTo(timeNow) < 0) {
			startDateErrorLabel.setVisible(true);
			StartDate.getStyleClass().add("validation-error");
		}

		if (StartDate.getValue() != null && EndDate.getValue() != null
				&& EndDate.getValue().compareTo(StartDate.getValue()) >= 0) {
			return false;
		}
		if (StartDate.getValue() == null && EndDate.getValue() != null && EndDate.getValue().compareTo(timeNow) >= 0) {
			return true;
		}

		if ((EndDate.getValue() != null && EndDate.getValue().compareTo(timeNow) < 0)
				|| (EndDate.getValue() != null && EndDate.getValue().compareTo(StartDate.getValue()) < 0)) {

			endDateErrorLabel.setVisible(true);
			EndDate.getStyleClass().add("validation-error");
		}
		return true;

	}

	/**
	 * This method checks if sale template form's comboBoxes are filled correctly.
	 * 
	 * @return boolean false(if comboBoxes are filled incorrectly), boolean true(if
	 *         comboBoxes are filled correctly).
	 */
	private boolean isNotFilledComboBoxes() {
		if ((comboBoxRegions.getValue() != null) && (comboBoxHoursSale.getValue() != null) && (comboBoxType != null)) {
			return false;
		}
		if (comboBoxRegions.getValue() == null) {
			regionErrorLabel.setVisible(true);
			comboBoxRegions.getStyleClass().add("validation-error-comboBox");
		}
		if (comboBoxHoursSale.getValue() == null) {
			timeErrorLabel.setVisible(true);
			comboBoxHoursSale.getStyleClass().add("validation-error-comboBox");
		}
		if (comboBoxType.getValue() == null) {
			typeErrorLabel.setVisible(true);
			comboBoxType.getStyleClass().add("validation-error-comboBox");
		}
		return true;
	}

	/**
	 * This method calls "openModal()" method whenever the user clicked a sale row
	 * in the table. This method shows the user data about the sale.
	 * 
	 * @param event, Description: the current event when the click happened.
	 */
	@FXML
	void templateSalesTableRowClicked(MouseEvent event) {
		try {
			if ((saleTemplatesTableView.getSelectionModel().getSelectedItem() != null)
					&& (saleTemplatesTableView.getSelectionModel().getSelectedIndex() != -1)) {

				Sale sale = saleTemplatesTableView.getSelectionModel().getSelectedItem();
				previewSale = sale;
				handleSalePreview();
				// showModalWindow();
				saleTemplatesTableView.getSelectionModel().clearSelection();
			}
		} catch (NullPointerException e) {
//					catches null pointer exceptions whenever a user clicks the header row.
		}
	}

	/**
	 * This method opens new modal with the clicked sale preview, shows the user
	 * data about the sale template.
	 */

	private void handleSalePreview() {
//		previewSale
		if (previewSale != null) {
			// salePreviewSection.setVisible(true);
			for (Node node : salePreviewHbox.getChildren()) {
				node.setEffect(null);
			}
			for (Node node : salePreviewSection.getChildren()) {
				node.setEffect(null);
			}
			salePreviewPlaceHolder.setVisible(false);
			if (previewSale.getSaleRegion().equals(Regions.All)) {
				regionLabel.setText(previewSale.getSaleRegion().toString() + " Regions.");
			} else {
				regionLabel.setText(previewSale.getSaleRegion().toString());
			}

			saleDescriptionTxt.setText(previewSale.getSaleDiscription());
			saleEndDateLabel.setText(previewSale.getSaleEndDate());
			saleNameLabel.setText(previewSale.getSaleName());
			saleStartDateLabel.setText(previewSale.getSaleStartDate());
			saleTimeLabel.setText(previewSale.getSaleTime().toString());
			saleTypeLabel.setText(previewSale.getSaleType().toString());
			if (saleTypeLabel.getText() == TypeSale.PercentageDiscount.toString()
					|| saleTypeLabel.getText() == TypeSale.GetSecondOneAtDiscount.toString()) {
				saleTypeLabel.setText(saleTypeLabel.getText() + ":  " + previewSale.getSalePercentage() + "%");
			}
			saleStatusLabel.setText(previewSale.getSaleStatus().toString());
			decorateStatus(saleStatusLabel);

		}

	}

	private void decorateStatus(Label saleStatusLabel) {
		switch (previewSale.getSaleStatus()) {
		case Template:

			statusBoxInSalePreview.setStyle("-fx-background-color: #898f89;");
			break;
		case Ready:

			statusBoxInSalePreview.setStyle("-fx-background-color: #b042f5;");

			break;
		case Running:

			statusBoxInSalePreview.setStyle("-fx-background-color: #0ba115;");

			break;
		case Outdated:

			statusBoxInSalePreview.setStyle("-fx-background-color: #eb7ca3;");

			break;

		default:
			break;
		}

	}

}
