package gui;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.math.RoundingMode;

import client.Client;
import client.ClientUI;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.*;
import utils.StyleConstants;
import utils.StylePaths;
import utils.Utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;

/**
 * @author badihi
 * This class describes the functionality of the new order creation, add products to cart and continue for payment.
 */

public class NewOrderController implements Initializable {

    static private ObservableList<Sale> readySales = FXCollections.observableArrayList();

    static private Regions oldRegion=null;


    /**
     * This variable responsible to check if there is an active sale
     */
    static public boolean aliveSale = false;

    static Order previousOrder;


    /**
     * This object monitor if there is new order that replaced old one
     */

    public static boolean NewOrderReplaced = false;

    static User user = LoginController.user;//new Customer("Yuval", "Zohar", 318128841, "asdfjj2@gmail.com", "05234822234", "asdfk", "asdf",false, "00",CustomerType.Client,"3", Regions.South);
    //static User user = new User();

    @FXML
    private Label NameLabel;

    @FXML
    private ListView<VBox> ProductsList;

    @FXML
    private Label originalPrice;

    @FXML
    private ListView<HBox> CartListShop;

    @FXML
    private Label outOfStock;

    @FXML
    private Label EmptyCartAlert;

    @FXML
    private Button ContinueButton;

    @FXML
    private Button CancelOrderButton;

    @FXML
    private Label TotalOrderPrice;


    @FXML
    private Label Saletype;

    @FXML
    private Label ClearCart;
    public static boolean isBackClicked = false;

    @FXML
    private ImageView saleImage;

    @FXML
    private Label saleLabel;


    @FXML
    private Text textForSale;

    @FXML
    private ImageView cloudForSale;
    @FXML
    private ImageView imageToPresent;

    @FXML
    private Label discountDisplayed;

    @FXML
    void logOutClicked(ActionEvent event) {
        try {
            NewOrderReplaced = true;
            Utils.genericLogOut(getClass());
        } catch (Exception e) {
        }
    }

    @FXML
    void backBtnClicked(MouseEvent event) throws Exception {
        NewOrderReplaced = true;
        BillWindowController.restoreOrder = null;
        oldRegion = null;
        aliveSale = false;

        Stage stage = StageSingleton.getInstance().getStage();

        if (LoginController.order.getPickUpMethod() == PickUpMethod.delivery)
        {
            stage.setScene(DeliveryFormController.scene);

        }
        else if(LoginController.order.getPickUpMethod() == PickUpMethod.selfPickUp)
        {
            new EKController().start(stage);
        }

        else
        {
            stage.setScene(PickupController.scene);
        }
        if (UserInstallationController.configuration.equals("EK")) {
            playCancelOrderVoice();
        }
    }



    @FXML
    void CancelOrderClicked(ActionEvent event) throws IOException {
        NewOrderReplaced = true;
        BillWindowController.restoreOrder = null;
        Parent root;
        Stage primaryStage = StageSingleton.getInstance().getStage();
        if (LoginController.order.getPickUpMethod() == PickUpMethod.delivery || LoginController.order.getPickUpMethod() == PickUpMethod.latePickUp)
            root = FXMLLoader.load(getClass().getResource("/assets/fxmls/OLMain.fxml"));
        else{
            root = FXMLLoader.load(getClass().getResource("/assets/fxmls/EKMain.fxml"));
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
        primaryStage.setTitle("EKrut Main");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        if (UserInstallationController.configuration.equals("EK")) {
            playCancelOrderVoice();
        }
    }

    private void playCancelOrderVoice() {
        String audioFile = Objects.requireNonNull(getClass().getResource("/assets/sounds/cancel.mp3")).toString();
        Media media = new Media(audioFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.totalDurationProperty().addListener((observable, oldDuration, newDuration) -> {
            if (newDuration.greaterThan(Duration.ONE)) {
                Platform.runLater(mediaPlayer::play);
            }
        });
    }


    List<ProductInMachineMonitor> allProductsMonitors = new ArrayList<>();
    private double firstTimeMultiplier;

    List<Object> MaxAmountsList;


    private Double findOriginalPrice(Order order)
    {
        Double price = 0.0;
        List<ProductInOrder> products = order.getProductsInOrder();
        for(ProductInOrder prod : products)
        {
            price += prod.getAmount()*prod.getProduct().getPrice();
        }
        return price;
    }


    private Regions getRegion(Order order)
    {
        if(UserInstallationController.configuration.equals("OL")) {
            if (order instanceof PickupOrder) {
                return PickupController.regionForMach;

            } else if (order instanceof DeliveryOrder) {
                return ((DeliveryOrder) order).getRegion();
            } else {
                System.out.println("Error from getRegion");
                return null;
            }
        }
        else
        {
            return Regions.valueOf(UserInstallationController.machine.getRegion());
        }


    }

    private void requestReadySales(Order order) {
        List<Object> body = new ArrayList<>();
        Regions regionForSale = getRegion(order);

        body.add(regionForSale.toString());
        body.add(SaleStatus.Running.toString());
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
                System.out.println(Client.resFromServer.getDescription());
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
             //   aliveSale = true;

            }
        }
    }

    //badihi
    private List<Object> requestMachineProducts(String machineId) {
        if(MaxAmountsList != null)
        {
            return MaxAmountsList;
        }
        List<Object> machineIdList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/requestMachineProducts");
        request.setMethod(Method.GET);
        machineIdList.add(machineId);
        request.setBody(machineIdList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> result = Client.resFromServer.getBody();
                MaxAmountsList = result;
                return result;
            default:
                System.out.println(Client.resFromServer.getDescription());
        }
        return machineIdList;
    }
    //badihi
    private List<Object> requestProducts() {
        Request request = new Request();
        request.setPath("/requestProducts");
        request.setMethod(Method.GET);
        request.setBody(null);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> products = Client.resFromServer.getBody();
                return products;
            default:
                System.out.println(Client.resFromServer.getDescription());
        }
        return null;
    }

    //badihi
    private List<Object> requestCompletedOrders(Integer userId) {
        List<Object> userIdList = new ArrayList<>();
        userIdList.add(userId);
        Request request = new Request();
        request.setPath("/requestCompletedOrders");
        request.setMethod(Method.GET);
        request.setBody(userIdList);
        ClientUI.chat.accept(request);// sending the request to the server.
        switch (Client.resFromServer.getCode()) {
            case OK:
                List<Object> compOrders = Client.resFromServer.getBody();
                return compOrders;
            default:
                System.out.println(Client.resFromServer.getDescription());
        }
        return null;
    }

    private int getMaxAmountOfProductInMachineFromDB(Order order, String productId) {
        List<Object> objectedProdInMachine = requestMachineProducts(order.getMachineId());
        for(Object prodInMachine : objectedProdInMachine) {
            if(prodInMachine instanceof ProductInMachine) {
                if(((ProductInMachine)prodInMachine).getProductId().equals(productId))
                {
                    return ((ProductInMachine)prodInMachine).getAmount();
                }
            }
        }
        return 0;
    }

    private Order receiveOrderFromPreviousPage() {
        Order order = LoginController.order;
        return order;
    }

    private void showTooltip(Sale sale) throws InterruptedException {
        textForSale.setText(sale.getSaleName()+"\n\n"+sale.getSaleDiscription());
        cloudForSale.setVisible(true);
        textForSale.setVisible(true);
        discountDisplayed.setVisible(true);
        ProductsList.setOpacity(0.4);
        //sleep(300);
    }
    private void hideTooltip()
    {
        cloudForSale.setVisible(false);
        textForSale.setVisible(false);
        discountDisplayed.setVisible(false);
        ProductsList.setOpacity(1);

    }

    private void setLabelandImage(Sale sale)
    {
        saleImage.setVisible(true);
        Saletype.setVisible(true);
        saleLabel.setVisible(true);

//        saleLabel.setOnMouseEntered(event -> showTooltip(sale));
//        saleLabel.setOnMouseExited(event -> hideTooltip());
        imageToPresent.setOnMouseEntered(event -> {
            try {
                showTooltip(sale);
            } catch (InterruptedException e) {
            }
        });
        imageToPresent.setOnMouseExited(event -> hideTooltip());
//        saleImage.setOnMouseEntered(event -> showTooltip(sale));
//        saleImage.setOnMouseExited(event -> hideTooltip());

    }
    private Double findDiscount(int amount, Double pricePerItem,Sale sale)
    {
        setLabelandImage(sale);
        Double price = 0.0;
        if(sale.getSaleType().equals(TypeSale.Sale1Plus1))
        {
            Saletype.setText("1+1");
            for(int i=0;i<amount;i++)
            {
                if(i%2 == 0)
                {
                    price += pricePerItem;
                }
            }
            return price*firstTimeMultiplier;
        }
        else if(sale.getSaleType().equals(TypeSale.Sale2Plus1))
        {
            Saletype.setText("2+1");
            for(int i=0;i<amount;i++)
            {
                if(i%3 == 0 || i%3 == 1)
                {
                    price += pricePerItem;
                }
            }
            return price*firstTimeMultiplier;
        }
        else if(sale.getSaleType().equals(TypeSale.Sale2Plus2))
        {
            Saletype.setText("2+2");
            for(int i=0;i<amount;i++)
            {
                if(i%4 == 0 || i%4 == 1)
                {
                    price += pricePerItem;
                }
            }
            return price*firstTimeMultiplier;
        }
        else if(sale.getSaleType().equals(TypeSale.PercentageDiscount))
        {

            Saletype.setText(sale.getSalePercentage() + "%");
            Double discount = Double.parseDouble(sale.getSalePercentage())/100;
            return amount*pricePerItem*firstTimeMultiplier*discount;
        }
        return null;



    }
    private Double calculatePriceWithout(int amount, Double pricePerItem) {
        return amount * pricePerItem * firstTimeMultiplier;
    }

    private boolean getIfItsSaleTime(Sale sale)
    {
        TimeSale timeSale = sale.getSaleTime();
        LocalTime currentTime = LocalTime.now();
        switch (timeSale) {
            case AllDay:
                return true;
            case Morning:
                return currentTime.isAfter(LocalTime.of(6,0)) && currentTime.isBefore(LocalTime.of(12,0));
            case Noon:
                return currentTime.isAfter(LocalTime.of(12,0)) && currentTime.isBefore(LocalTime.of(16,0));
            case Afternoon:
                return currentTime.isAfter(LocalTime.of(16,0)) && currentTime.isBefore(LocalTime.of(20,0));
            case Evening:
                return currentTime.isAfter(LocalTime.of(20,0)) && currentTime.isBefore(LocalTime.of(23,59));
            case Night:
                return currentTime.isAfter(LocalTime.of(0,0)) && currentTime.isBefore(LocalTime.of(6,0));
            default:
                return false;
        }
    }
    
    private Double calculatePriceAfterDiscount(int amount, Double pricePerItem)
    {
        Sale activeSale;
        boolean isSaleTime;
        for(Sale currentSale : readySales) {
            if (currentSale != null) {
                activeSale = currentSale;
                String dateString = currentSale.getSaleStartDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(dateString, formatter);
                LocalDate today = LocalDate.now();

                if (NewOrderController.user instanceof Customer) {
                    CustomerType customerType = ((Customer) NewOrderController.user).getType();
                    if (customerType == CustomerType.Subscriber) {
                        isSaleTime = getIfItsSaleTime(currentSale);
                        if (!date.isAfter(today) && isSaleTime) {
                            aliveSale = true;
                            return findDiscount(amount, pricePerItem, activeSale);
                        }
                    }
                }
            }
        }
        return amount*pricePerItem*firstTimeMultiplier;
    }

    private List<Product> getAllProductsFromDB(Order order) {
        List<Object> objectedProducts = requestProducts();
        List<Product> newProductsList = new ArrayList<>();
        List<Object> objectedProdInMachine = requestMachineProducts(order.getMachineId());
        if(objectedProducts == null)
        {
            return null;
        }
        for(Object product : objectedProducts) {
            if (product instanceof Product) {
                for (Object prodInMachine : objectedProdInMachine) {
                    if (prodInMachine instanceof ProductInMachine) {
                        if (((ProductInMachine) prodInMachine).getProductId().equals(((Product) product).getProductId())) {
                            newProductsList.add((Product) product);
                        }
                    }
                }
            }
        }
        ObservableList<Product> products = FXCollections.observableArrayList();
        products.addAll(newProductsList);
        EmptyCartAlert.setVisible(false);
        return products;
    }

    //badihi
    private class ProductInMachineMonitor {
        Product product;
        int amountSelected;
        Button AddToCartButton;
        Label counter;
        ImageView plusImage;
        ImageView minusImage;
        int productMaxAmount;
        Order order;

        //badihi
        private ProductInMachineMonitor(Product product, Button AddToCartButton,
                                       Label counter, ImageView minusImage, ImageView plusImage, Order order) {
            this.order = order;
            this.product = product;
            amountSelected = 1;
            this.AddToCartButton = AddToCartButton;
            this.counter = counter;
            this.minusImage = minusImage;
            this.plusImage = plusImage;
            this.productMaxAmount = getMaxAmountOfProductInMachine();

        }

        //badihi
        private String getMonitorMainProductID() {
            return product.getProductId();
        }

        private int getProductAmountInOrder() {
            String productId;
            for (ProductInOrder prod : order.getProductsInOrder()) {
                productId = prod.getProduct().getProductId();
                if (productId.equals(product.getProductId())) {
                    return prod.getAmount();
                }
            }
            return 0;
        }

        private int getMaxAmountOfProductInMachine() {
            int MaxAmountInDB = getMaxAmountOfProductInMachineFromDB(order,product.getProductId());
            int AmountAlreadyInOrder = getProductAmountInOrder();
            return MaxAmountInDB - AmountAlreadyInOrder;
        }

        //badihi
        private void increaseAmount() {
            EmptyCartAlert.setVisible(false);
            try {
                if (amountSelected >= productMaxAmount) { // Throw label
                    plusImage.setOpacity(StyleConstants.LOW_OPACITY);
                    return;
                }
                minusImage.setOpacity(1);
                plusImage.setOpacity(1);
                amountSelected++;
                if (amountSelected >= productMaxAmount) {
                    plusImage.setOpacity(StyleConstants.LOW_OPACITY);
                }
                AddToCartButton.setText(StyleConstants.ADD_TO_CART_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, calculatePriceAfterDiscount(amountSelected,product.getPrice())) + StyleConstants.CURRENCY_SYMBOL);
                counter.setText("  " + amountSelected + "  ");
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        //badihi
        private void decreaseAmount() {
            try {
                if (amountSelected <= 1) { // Throw label
                    minusImage.setOpacity(StyleConstants.LOW_OPACITY);
                    return;
                }
                minusImage.setOpacity(1);
                plusImage.setOpacity(1);
                amountSelected--;
                if (amountSelected <= 1) { // Throw label
                    minusImage.setOpacity(StyleConstants.LOW_OPACITY);
                }
                AddToCartButton.setText(StyleConstants.ADD_TO_CART_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, calculatePriceAfterDiscount(amountSelected,product.getPrice())) + StyleConstants.CURRENCY_SYMBOL);
                counter.setText("  " + amountSelected + "  ");
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //badihi
        private void AddToCart() {
            minusImage.setOpacity(StyleConstants.LOW_OPACITY);
            productMaxAmount -= amountSelected;
            if (productMaxAmount <= 0) {
                counter.setText(StyleConstants.INIT_AMOUNT_OF_PRODUCTS_TO_ZERO);
                AddToCartButton.setText(StyleConstants.OUT_OF_STOCK_LABEL);
                plusImage.setOpacity(StyleConstants.LOW_OPACITY);
                AddToCartButton.setOpacity(StyleConstants.LOW_OPACITY);
                AddToCartButton.disabledProperty();
                AddProductInOrderToOrder(product, amountSelected);
                amountSelected = 0;

            } else {
                counter.setText(StyleConstants.INIT_AMOUNT_OF_PRODUCTS_TO_ONE);
                //roundTo2Digit(calculatePriceAfterDiscount(prod.getAmount(),prod.getProduct().getPrice()))
                // AddToCartButton.setText(StyleConstants.ADD_TO_CART_LABEL + product.getPrice() + StyleConstants.CURRENCY_SYMBOL);
                AddToCartButton.setText(StyleConstants.ADD_TO_CART_LABEL + roundTo2Digit(calculatePriceAfterDiscount(1,product.getPrice())) + StyleConstants.CURRENCY_SYMBOL);
                AddProductInOrderToOrder(product, amountSelected);
                amountSelected = 1;
            }
        }

        private void priceSetterForSmallNumbers(Double originalPrice) {
            if (originalPrice <= 0.001) {
                order.setPrice(0.0);
            } else {
                order.setPrice(originalPrice);
            }
        }

        private void AddProductInOrderToOrder(Product product, int amount) {
            String productId;
            boolean existingFlag = false;
            ProductInOrder prodInOrder = new ProductInOrder(product, amount, null);
            for (ProductInOrder prod : order.getProductsInOrder()) {
                productId = prod.getProduct().getProductId();
                if (productId.equals(product.getProductId())) {
                    prod.setAmount(amount + prod.getAmount());
                    prod.setTotalProductPrice(roundTo2Digit(calculatePriceAfterDiscount(prod.getAmount(),prod.getProduct().getPrice())));
                    priceSetterForSmallNumbers(roundTo2Digit(order.getPrice()-calculatePriceAfterDiscount(prod.getAmount()-amount,prod.getProduct().getPrice())
                            + (calculatePriceAfterDiscount(prod.getAmount(),prod.getProduct().getPrice()))));
                    existingFlag = true;
                    break;
                }
            }
            if (!existingFlag) {
                order.getProductsInOrder().add(prodInOrder);
                prodInOrder.setTotalProductPrice(roundTo2Digit(calculatePriceAfterDiscount(prodInOrder.getAmount(),prodInOrder.getProduct().getPrice())));
                priceSetterForSmallNumbers(roundTo2Digit(order.getPrice()-calculatePriceAfterDiscount(prodInOrder.getAmount()-amount,prodInOrder.getProduct().getPrice())
                        + (calculatePriceAfterDiscount(prodInOrder.getAmount(),prodInOrder.getProduct().getPrice()))));
            }
            UpdateCart(order);
            if(aliveSale)
            {
                Double original_price = findOriginalPrice(order);
                originalPrice.setText("Price before discount: "+String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, original_price)+StyleConstants.CURRENCY_SYMBOL);
                originalPrice.setVisible(true);

            }

            TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice())+StyleConstants.CURRENCY_SYMBOL);
        }

        //badihi
        private double roundTo2Digit(double num) {
            BigDecimal bd = new BigDecimal(num).setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        }

        private void removeItem() {
            String productId;
            for (ProductInOrder produtToRemove : order.getProductsInOrder()) {
                productId = produtToRemove.getProduct().getProductId();
                if (productId.equals(product.getProductId())) {
                    order.getProductsInOrder().remove(produtToRemove);
                    this.productMaxAmount = getMaxAmountOfProductInMachine();
                    counter.setText(StyleConstants.INIT_AMOUNT_OF_PRODUCTS_TO_ONE);
                    amountSelected = 1;
                    plusImage.setOpacity(1);
                    AddToCartButton.setText(StyleConstants.ADD_TO_CART_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, calculatePriceAfterDiscount(amountSelected,product.getPrice())) + StyleConstants.CURRENCY_SYMBOL);
                    priceSetterForSmallNumbers(order.getPrice() - calculatePriceAfterDiscount( produtToRemove.getAmount(), produtToRemove.getProduct().getPrice()));


                    if(aliveSale)
                    {
                        Double original_price = findOriginalPrice(order);
                        originalPrice.setText("Price before discount: "+String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, original_price)+StyleConstants.CURRENCY_SYMBOL);
                        originalPrice.setVisible(true);

                    }



                    TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice())+StyleConstants.CURRENCY_SYMBOL);
                    AddToCartButton.setOpacity(1);
                    break;
                }
            }
            UpdateCart(order);

            if(aliveSale)
            {
                Double original_price = findOriginalPrice(order);
                originalPrice.setText("Price before discount: "+String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, original_price)+StyleConstants.CURRENCY_SYMBOL);
                originalPrice.setVisible(true);

            }



            TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice())+StyleConstants.CURRENCY_SYMBOL);
        }
    }

    private void successPop(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.setHeaderText(null);
        ImageView image = new ImageView(new Image("assets/gift.png"));
        image.setFitHeight(100);
        image.setFitWidth(100);
        alert.setGraphic(image);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getScene().getStylesheets().add("styles/DiscountPopup.css");
        stage.setResizable(true);
        alert.show();
    }

    private void setfirstTimeMultiplier(Integer id,Order order)
    {

        Regions currReg = getRegion(order);
        if(aliveSale == false ||  !currReg.equals(oldRegion)) {
            requestReadySales(order);
            handleReponseGetReadySales();

        }
        oldRegion = currReg;

        List<Object> OrderedIds = requestCompletedOrders(id);
        Boolean isExist = (Boolean)OrderedIds.get(0);

        if(isExist)
        {
            firstTimeMultiplier = 1.0;
        }
        else
        {
            aliveSale = true;
            successPop("Congratulations on subscribing! As a member, enjoy a 20% discount on all prices.");
            firstTimeMultiplier = 0.8;
        }

    }

    /**
     * This method initializes data before the screen comes up
     */
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        NewOrderReplaced = false;
        if(UserInstallationController.configuration.equals("EK")){
            Thread timeOutThread = new Thread(new gui.NewOrderController.TimeOutControllerNewOrder());
            timeOutThread.start();
        }
        Timeline rotation = new Timeline(
                new KeyFrame(Duration.seconds(20),
                        new KeyValue(saleImage.rotateProperty(), 360)));
        rotation.setCycleCount(Animation.INDEFINITE);
        rotation.play();
        user = LoginController.user;
        aliveSale = false;
        saleLabel.setVisible(false);
        originalPrice.setVisible(false);

//        Tooltip tooltip = new Tooltip("Price before discount");
//        Tooltip.install(originalPrice, tooltip);
//        tooltip.setShowDuration(Duration.millis(3000));
//        tooltip.setShowDelay(Duration.millis(0));
        outOfStock.setVisible(false);
        saleImage.setVisible(false);
        Saletype.setVisible(false);
        textForSale.setVisible(false);
        cloudForSale.setVisible(false);
        discountDisplayed.setVisible(false);
        ProductsList.setOpacity(1);


        setUserProfile();
        putProductsInMachine();
        if (UserInstallationController.configuration.equals("EK")) {
            playSelectProductVoice();
        }
    }

    private void playSelectProductVoice() {
        String audioFile = Objects.requireNonNull(getClass().getResource("/assets/sounds/select.mp3")).toString();
        Media media = new Media(audioFile);
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.totalDurationProperty().addListener((observable, oldDuration, newDuration) -> {
            if (newDuration.greaterThan(Duration.ZERO)) {
                Platform.runLater(mediaPlayer::play);
            }
        });
    }


    private void setUserProfile()
    {
        NameLabel.setText(user.getFirstName() + " "+user.getLastName());
    }

    private Order recieveCurrentOrder() {
        Order order;
        if (BillWindowController.restoreOrder != null)
        {
            order = BillWindowController.restoreOrder;
        }
        else
        {
            order = receiveOrderFromPreviousPage();
        }
        return order;
    }

    private void UpdateCart(Order order) {
        List<ProductInOrder> productsInOrder = order.getProductsInOrder();
        CartListShop.getItems().clear();
        Image prodImage = null;
        if (order.getProductsInOrder().size() > 0) {
            ClearCart.setVisible(true);
            EmptyCartAlert.setVisible(false);

        } else {
            ClearCart.setVisible(false);
        }
        for (ProductInOrder prodInOrder : productsInOrder) {
            try {
                HBox hboxTop = new HBox();
                Pane pane = new Pane();
                try {

                    prodImage = recieveImageForProduct(prodInOrder.getProduct());
                } catch (Exception e) {
                    prodImage = new Image(StylePaths.DEFAULT_PRODUCT_IMAGE);
                }
                ImageView productImage = new ImageView(prodImage);
                productImage.setFitWidth(StyleConstants.CART_PRODUCT_IMAGE_WIDTH);
                productImage.setFitHeight(StyleConstants.CART_PRODUCT_IMAGE_HEIGHT);
                Image xmark = new Image(StylePaths.REMOVE_PRODUCT_IMAGE);
                ImageView xmarkImage = new ImageView(xmark);
                xmarkImage.setFitWidth(StyleConstants.REMOVE_BUTTON_SIZE);
                xmarkImage.setFitHeight(StyleConstants.REMOVE_BUTTON_SIZE);
                Integer amountOfProduct = new Integer(prodInOrder.getAmount());
                Double priceOfProduct = new Double((double) (calculatePriceAfterDiscount(amountOfProduct,prodInOrder.getProduct().getPrice())));
                hboxTop.getChildren().addAll(productImage, new Label("       x" + amountOfProduct.toString() + "   " + prodInOrder.getProduct().getName() + " - " + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, priceOfProduct) + StyleConstants.CURRENCY_SYMBOL), pane, xmarkImage);
                CartListShop.getItems().addAll(hboxTop);
                for (ProductInMachineMonitor monitor : allProductsMonitors) {
                    if (monitor.getMonitorMainProductID().equals(prodInOrder.getProduct().getProductId())) {
                        xmarkImage.setOnMouseClicked(event -> monitor.removeItem());
                    }
                }
                HBox.setHgrow(pane, Priority.ALWAYS);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }


    private void ClearCartClicked() {
    	
        for (ProductInMachineMonitor monitor : allProductsMonitors) {
            monitor.removeItem();
        }
    }


    private void ContinueOrder(Order order) {
        NewOrderReplaced = true;
        if (order.getPrice() <= 0.001) {
            EmptyCartAlert.setVisible(true);
            return;
        }
        previousOrder = order;
        AnchorPane pane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource(StylePaths.BILLWINDOW_CSS));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = StageSingleton.getInstance().getStage();
        stage.setTitle(StyleConstants.STAGE_LABEL);
        stage.setScene(new Scene(pane));
        stage.centerOnScreen();
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            try {
                Utils.forcedExit();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        stage.show();

    }
    private Image recieveImageForProduct(Product product)
    {
        Image image = new Image(new ByteArrayInputStream(product.getImage()));
        return image;
    }
    private void putProductsInMachine() {
        int buttonPlusMinus = StyleConstants.PLUS_MINUS_SIZE;
        Order order = recieveCurrentOrder();
        setfirstTimeMultiplier(user.getId(),order);
        ObservableList<Product> products = (ObservableList<Product>) getAllProductsFromDB(order);
        Image prodImage = null;
        ContinueButton.setOnMouseClicked(event -> ContinueOrder(order));
        ClearCart.setOnMouseClicked(event -> ClearCartClicked());
        ClearCart.setVisible(false);
        for (Product prod : products) {
            try {
                HBox hboxTop = new HBox();
                HBox hboxButtom = new HBox();
                VBox ProductSection = new VBox();
                Label ProdName = new Label("  " + prod.getName());
                Label counter = new Label(StyleConstants.INIT_AMOUNT_OF_PRODUCTS_TO_ONE);
                Pane pane = new Pane();
                Button AddToCartButton = new Button(StyleConstants.ADD_TO_CART_LABEL +String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, calculatePriceAfterDiscount(1,prod.getPrice())) + StyleConstants.CURRENCY_SYMBOL);
                AddToCartButton.setStyle(StylePaths.POSITIVE_BUTTON_CSS);
                Image min = new Image(StylePaths.MINUS_IMAGE);
                ImageView minusImage = new ImageView(min);
                minusImage.setFitWidth(buttonPlusMinus);
                minusImage.setFitHeight(buttonPlusMinus);
                minusImage.setOpacity(StyleConstants.LOW_OPACITY);
                Image plus = new Image(StylePaths.PLUS_IMAGE);
                ImageView plusImage = new ImageView(plus); //defultProductImage
                String productId;
                if(getMaxAmountOfProductInMachineFromDB(order,prod.getProductId())<=0)
                {
                    continue;
                }
                for (ProductInOrder prod1 : order.getProductsInOrder()) {
                    productId = prod1.getProduct().getProductId();
                    if (productId.equals(prod.getProductId())) {
                        if (prod1.getAmount() == getMaxAmountOfProductInMachineFromDB(order,productId)) {
                            counter.setText(StyleConstants.INIT_AMOUNT_OF_PRODUCTS_TO_ZERO);
                            AddToCartButton.setText(StyleConstants.OUT_OF_STOCK_LABEL);
                            plusImage.setOpacity(StyleConstants.LOW_OPACITY);
                            AddToCartButton.setOpacity(StyleConstants.LOW_OPACITY);
                            AddToCartButton.disabledProperty();
                        }
                    }
                }
                plusImage.setFitWidth(buttonPlusMinus);
                plusImage.setFitHeight(buttonPlusMinus);
                ProductInMachineMonitor productMonitor = new ProductInMachineMonitor(prod, AddToCartButton, counter, minusImage, plusImage, order);
                allProductsMonitors.add(productMonitor);
                plusImage.setOnMouseClicked(event -> productMonitor.increaseAmount());
                minusImage.setOnMouseClicked(event -> productMonitor.decreaseAmount());
                AddToCartButton.setOnMouseClicked(event -> productMonitor.AddToCart());
                UpdateCart(order);
                if(aliveSale)
                {
                    Double original_price = findOriginalPrice(order);
                    originalPrice.setText("Price before discount: "+String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, original_price)+StyleConstants.CURRENCY_SYMBOL);
                    originalPrice.setVisible(true);
                }

                TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice())+StyleConstants.CURRENCY_SYMBOL);

                try {
                    prodImage = recieveImageForProduct(prod);

                } catch (Exception e) {
                    System.out.println(e);
                    prodImage = new Image(StylePaths.DEFAULT_PRODUCT_IMAGE);
                }
                ImageView productImage = new ImageView(prodImage);

                productImage.setFitWidth(StyleConstants.PRODUCT_IMAGE_WIDTH);
                productImage.setFitHeight(StyleConstants.PRODUCT_IMAGE_HEIGHT);
                hboxTop.getChildren().addAll(productImage, ProdName, new Label("  |  " + prod.getInformation()), pane, minusImage, counter, plusImage);
                hboxButtom.getChildren().addAll(new Label("  			    "), AddToCartButton);
                ProductSection.getChildren().addAll(hboxTop, hboxButtom);
                ProductsList.getItems().addAll(ProductSection);
                HBox.setHgrow(pane, Priority.ALWAYS);
            } catch (Exception e) {
                System.out.println(e);
            }


        }
        if(ProductsList.getItems().size() == 0) 
        {
             outOfStock.setVisible(true);}


    }

    /**
     * A class that implements a runnable task for detecting and handling a time out event.
     * The time out event occurs when the elapsed time since the time out start time exceeds a specified time out time.
     */
    static class TimeOutControllerNewOrder implements Runnable {
        private int TimeOutTime = Utils.TIME_OUT_TIME_IN_MINUTES;//
        private long TimeOutStartTime = System.currentTimeMillis();

        /**
         * Detects and handles a time out event.
         * This task is executed every 10 seconds until the thread is interrupted or the `EKPageReplace` flag is set to `true`.
         * If a time out event occurs, the log out process is initiated.
         */
        @Override
        public void run() {
            Platform.runLater(()->handleAnyClick());
            while (!Thread.currentThread().isInterrupted()) {
                long TimeOutCurrentTime = System.currentTimeMillis();
                if (TimeOutCurrentTime - TimeOutStartTime >= TimeOutTime * 60 * 1000) {
                    System.out.println("Time Out passed");
                    try {
                        Platform.runLater(()-> {
                            try {
                                Utils.genericLogOut(getClass());
                            } catch (Exception e) {
                            }
                        });
                    } catch (Exception e) {
                    }
                    return;
                }
                if(NewOrderReplaced) {
                    System.out.println("Thread closed from TimeOut Controller");
                    return;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }

        private void handleAnyClick() {
            StageSingleton.getInstance().getStage().getScene().addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<javafx.scene.input.MouseEvent>(){
                @Override
                public void handle(MouseEvent mouseEvent) {
                    System.out.print("Mouse clicked, timeout time reset\n");
                    TimeOutStartTime = System.currentTimeMillis();
                }
            });
        }
    }



    /**
	 * This method describes setting up a new scene.
	 * 
	 * @param primaryStage Description: The stage on which the scene is presented
	 * @throws Exception Description: An exception will be thrown if there is a
	 *                    problem with the window that opens
	 */
    public void start(Stage primaryStage) throws Exception {
		AnchorPane pane;
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(getClass().getResource(StylePaths.NEW_ORDER_WINDOW_PATH));
			pane = loader.load();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		Stage stage = StageSingleton.getInstance().getStage();
		stage.setTitle(StyleConstants.STAGE_LABEL);
		stage.setScene(new Scene(pane));
		stage.centerOnScreen();
		stage.setResizable(false);
		stage.setOnCloseRequest(e -> {
			try {
				Utils.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}
}

