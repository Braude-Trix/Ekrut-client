package gui;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.math.RoundingMode;

import client.Client;
import client.ClientUI;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.*;
import utils.Util;
import utils.Utils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static java.lang.Thread.sleep;


public class NewOrderController implements Initializable {

    private ObservableList<Sale> readySales = FXCollections.observableArrayList();

    static Order previousOrder;
    public static boolean NewOrderReplaced = false;

    static User user = LoginController.user;//new Customer("Yuval", "Zohar", 318128841, "asdfjj2@gmail.com", "05234822234", "asdfk", "asdf",false, "00",CustomerType.Client,"3", Regions.South);
    //static User user = new User();

    @FXML
    private Label NameLabel;

    @FXML
    private ListView<VBox> ProductsList;

    @FXML
    private ListView<HBox> CartListShop;

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

    @FXML
    private ImageView saleImage;

    @FXML
    private Label saleLabel;

    @FXML
    void logOutClicked(ActionEvent event) {
        try {
            NewOrderReplaced = true;
            Util.genricLogOut(getClass());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void backBtnClicked(MouseEvent event) {
        NewOrderReplaced = true;
        Stage stage = StageSingleton.getInstance().getStage();
        // delivery, selfPickUp, latePickUp;
        //  System.out.println((""));

        if (LoginController.order.getPickUpMethod() == PickUpMethod.delivery)
        {
            stage.setScene(DeliveryFormController.scene);

        }
        else if(LoginController.order.getPickUpMethod() == PickUpMethod.selfPickUp)
        {
            stage.setScene(EKController.scene);
        }

        else
        {
            stage.setScene(PickupController.scene);
        }
    }



    @FXML
    void CancelOrderClicked(ActionEvent event) throws IOException {
        NewOrderReplaced = true;
        Parent root;
        Stage primaryStage = StageSingleton.getInstance().getStage();
        if (LoginController.order.getPickUpMethod() == PickUpMethod.delivery || LoginController.order.getPickUpMethod() == PickUpMethod.latePickUp)
            root = FXMLLoader.load(getClass().getResource("/assets/OLMain.fxml"));
        else{
            root = FXMLLoader.load(getClass().getResource("/assets/EKMain.fxml"));
        }
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles/customerMain.css").toExternalForm());
        primaryStage.setTitle("EKrut Main");
        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();
        primaryStage.setResizable(false);
        primaryStage.show();
        primaryStage.setMinHeight(primaryStage.getHeight());
        primaryStage.setMinWidth(primaryStage.getWidth());
    }





    List<ProductInMachineMonitor> allProductsMonitors = new ArrayList<>();
    private double firstTimeMultiplier;

    List<Object> MaxAmountsList;

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

    public List<Object> requestMachineProducts(String machineId) {
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
                System.out.println("Some error occurred");
        }
        return machineIdList;
    }

    public List<Object> requestProducts() {
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
                System.out.println("Some error occurred");
        }
        return null;
    }

    public List<Object> requestCompletedOrders(Integer userId) {
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
                System.out.println("Some error occurred");
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

    private void setLabelandImage(Sale sale)
    {
        saleImage.setVisible(true);
        Saletype.setText("2+1");
        Saletype.setVisible(true);
        Tooltip tooltip = new Tooltip(sale.getSaleName() + "\n"+sale.getSaleDiscription());
        Tooltip.install(saleImage, tooltip);
        saleLabel.setVisible(true);
    }
    private Double findDiscount(int amount, Double pricePerItem,Sale sale)
    {
        setLabelandImage(sale);
        System.out.println(sale.getSaleDiscription());
        return amount*pricePerItem*firstTimeMultiplier;
    }

    private Double calculatePriceAfterDiscount(int amount, Double pricePerItem)
    {
        Sale activeSale;
        for(Sale currentSale : readySales) {
            if (currentSale != null) {
                activeSale = currentSale;
                String dateString = currentSale.getSaleStartDate();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                LocalDate date = LocalDate.parse(dateString, formatter);
                LocalDate today = LocalDate.now();

                if (!date.isAfter(today)) {
                    return findDiscount(amount, pricePerItem,activeSale);
                }
            }
        }


        System.out.println("NO DISCOUNT");
        return amount*pricePerItem*firstTimeMultiplier;
    }

    private List<Product> getAllProductsFromDB(Order order) {
        List<Object> objectedProducts = requestProducts();
        List<Product> newProductsList = new ArrayList<>();
        List<Object> objectedProdInMachine = requestMachineProducts(order.getMachineId());
        if(objectedProducts == null)
        {
            System.out.println("Error in getAllProductsFromDB()");
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

    public class ProductInMachineMonitor {
        Product product;
        int amountSelected;
        Button AddToCartButton;
        Label counter;
        ImageView plusImage;
        ImageView minusImage;
        int productMaxAmount;
        Order order;


        public ProductInMachineMonitor(Product product, Button AddToCartButton,
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

        public String getMonitorMainProductID() {
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

        public void increaseAmount() {
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

        public void decreaseAmount() {
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
        public void AddToCart() {
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
            TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice()));
        }

        public double roundTo2Digit(double num) {
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
                    TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice()));
                    AddToCartButton.setOpacity(1);
                    break;
                }
            }
            UpdateCart(order);
            TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice()));
        }
    }

    private void setfirstTimeMultiplier(Integer id,Order order)
    {
        requestReadySales(order);
        handleReponseGetReadySales();
        List<Object> OrderedIds = requestCompletedOrders(id);
        Boolean isExist = (Boolean)OrderedIds.get(0);




        if(isExist)
        {
            firstTimeMultiplier = 1.0;
        }
        else
        {
            firstTimeMultiplier = 0.8;
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle)
    {
        NewOrderReplaced = false;
        if(UserInstallationController.configuration.equals("EK")){
            Thread timeOutThread = new Thread(new gui.NewOrderController.TimeOutControllerNewOrder());
            timeOutThread.start();
        }
        saleLabel.setVisible(false);
        saleImage.setVisible(false);
        Saletype.setVisible(false);
        setUserProfile();
        putProductsInMachine();
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
                Util.forcedExit();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
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
                if(getMaxAmountOfProductInMachineFromDB(order,prod.getProductId())==0)
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
                TotalOrderPrice.setText(StyleConstants.TOTAL_PRICE_LABEL + String.format(StyleConstants.NUMBER_OF_DECIMAL_DIGITS_CODE, order.getPrice()));
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

    }


    static class TimeOutControllerNewOrder implements Runnable {
        private int TimeOutTime = Utils.TIME_OUT_TIME_IN_MINUTES; //Utils.TIME_OUT_TIME_IN_MINUTES;
        private long TimeOutStartTime = System.currentTimeMillis();

        @Override
        public void run() {
            while (true) {
                Platform.runLater(()->handleAnyClick());
                long TimeOutCurrentTime = System.currentTimeMillis();
                if (TimeOutCurrentTime - TimeOutStartTime >= TimeOutTime * 60 * 1000) {
                    System.out.println("Time Out passed");
                    try {
                        Platform.runLater(()-> {
                            try {
                                Util.genricLogOut(getClass());
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
                if(NewOrderReplaced) {
                    System.out.println("Thread closed");
                    return;
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        public void handleAnyClick() {
            StageSingleton.getInstance().getStage().getScene().addEventFilter(javafx.scene.input.MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>(){
                @Override
                public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                    System.out.print("Mouse clicked, timeout time reset\n");
                    TimeOutStartTime = System.currentTimeMillis();
                }
            });
        }
    }
    
	/**
	 * This method describes setting up a new scene.
	 * 
	 * @param primaryStage, Description: The stage on which the scene is presented
	 * @throws Exception, Description: An exception will be thrown if there is a
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
				Util.forcedExit();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		});
	}

}

