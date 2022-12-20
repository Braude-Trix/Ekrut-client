package gui;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.math.RoundingMode;

import client.Client;
import client.ClientUI;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.*;


public class NewOrderController implements Initializable {

    static Order previousOrder;
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
    private Label ClearCart;

    List<ProductInMachineMonitor> allProductsMonitors = new ArrayList<>();

   ///////////////// CHANGE//////////////////////////

    List<Object> MaxAmountsList;
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
///////////////////////////////////////////////////////////
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
        Order order = new Order(null, "25", 0.0, "1", "asdf", PickUpMethod.latePickUp);
        return order;
    }

    private Double calculatePriceAfterDiscount(int amount, Double pricePerItem)

    {
        if (amount == 2)
        {
            return pricePerItem;

        }
        return amount*pricePerItem;


    }


    private List<Product> getAllProductsFromDB(Order order) {
//        Product product1 = (new Product("Bamba", "bambaaa", 4.6));
//        Product product2 = (new Product("Bisli", "Bisliiiii", 3.5));
//        Product product3 = (new Product("Cola", "Colaaaaa", 2.0));
//        Product product4 = (new Product("Bamba", "hello", 4.6));
//        Product product5 = (new Product("Bisli", "world", 3.5));
//        Product product6 = (new Product("Cola", "sucka", 2.0));

        List<Object> objectedProducts = requestProducts();
        List<Product> newProductsList = new ArrayList<>();
        List<Object> objectedProdInMachine = requestMachineProducts(order.getMachineId());
        if(objectedProducts == null)
        {
            System.out.println("Error in getAllProductsFromDB()");
            return null;
        }
        for(Object product : objectedProducts)
        {
            if(product instanceof Product)
            {
                for(Object prodInMachine : objectedProdInMachine) {
                    if(prodInMachine instanceof ProductInMachine) {
                        if(((ProductInMachine)prodInMachine).getProductId().equals(((Product)product).getProductId()))
                        {
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
                    plusImage.setOpacity(0.3);
                    return;

                }
                minusImage.setOpacity(1);
                plusImage.setOpacity(1);
                amountSelected++;
                if (amountSelected >= productMaxAmount) {
                    plusImage.setOpacity(0.3);
                }

           //     AddToCartButton.setText("Add To Cart - " + String.format("%.2f", amountSelected * product.getPrice()) + "₪");
                AddToCartButton.setText("Add To Cart - " + String.format("%.2f", calculatePriceAfterDiscount(amountSelected,product.getPrice())) + "₪");
                counter.setText("  " + amountSelected + "  ");
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void decreaseAmount() {
            try {
                if (amountSelected <= 1) { // Throw label
                    minusImage.setOpacity(0.3);
                    return;
                }
                minusImage.setOpacity(1);
                plusImage.setOpacity(1);

                amountSelected--;
                if (amountSelected <= 1) { // Throw label
                    minusImage.setOpacity(0.3);
                }
               // AddToCartButton.setText("Add To Cart - " + String.format("%.2f", amountSelected * product.getPrice()) + "₪");
                AddToCartButton.setText("Add To Cart - " + String.format("%.2f", calculatePriceAfterDiscount(amountSelected,product.getPrice())) + "₪");
                counter.setText("  " + amountSelected + "  ");
            } catch (Exception e) {
                System.out.println(e);
            }
        }

        public void AddToCart() {

            minusImage.setOpacity(0.3);
            productMaxAmount -= amountSelected;

            if (productMaxAmount <= 0) {

                counter.setText("  0  ");
                AddToCartButton.setText("Out Of Stock");
                plusImage.setOpacity(0.3);
                AddToCartButton.setOpacity(0.3);
                AddToCartButton.disabledProperty();

                AddProductInOrderToOrder(product, amountSelected);

            //    System.out.println("X" + amountSelected + " " + product.getName() + " Added for price: " + String.format("%.2f", amountSelected * product.getPrice()));
                amountSelected = 0;

            } else {

                counter.setText("  1  ");
                AddToCartButton.setText("Add To Cart - " + product.getPrice() + "₪");
                AddProductInOrderToOrder(product, amountSelected);
            //    System.out.println("X" + amountSelected + " " + product.getName() + " Added for price: " + String.format("%.2f", amountSelected * product.getPrice()));
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

                    prodInOrder.setTotalProductPrice(roundTo2Digit(calculatePriceAfterDiscount(prod.getAmount(),prod.getProduct().getPrice())));
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

          //  prodInOrder.setTotalProductPrice(roundTo2Digit(prodInOrder.getAmount() * prodInOrder.getProduct().getPrice()));
           // priceSetterForSmallNumbers(roundTo2Digit(order.getPrice() + (prodInOrder.getAmount() * prodInOrder.getProduct().getPrice())));



            UpdateCart(order);
            TotalOrderPrice.setText("Total Price: " + String.format("%.2f", order.getPrice()));

        }

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
                    counter.setText("  1  ");
                    amountSelected = 1;
                    plusImage.setOpacity(1);
                  //  AddToCartButton.setText("Add To Cart - " + String.format("%.2f", amountSelected * product.getPrice()) + "₪");
                    AddToCartButton.setText("Add To Cart - " + String.format("%.2f", calculatePriceAfterDiscount(amountSelected,product.getPrice())) + "₪");
//                    priceSetterForSmallNumbers(order.getPrice() - produtToRemove.getAmount() * produtToRemove.getProduct().getPrice());


                    priceSetterForSmallNumbers(order.getPrice() - calculatePriceAfterDiscount( produtToRemove.getAmount(), produtToRemove.getProduct().getPrice()));


                    TotalOrderPrice.setText("Total Price: " + String.format("%.2f", order.getPrice()));
                    AddToCartButton.setOpacity(1);
                    break;
                }
            }

            UpdateCart(order);
            TotalOrderPrice.setText("Total Price: " + String.format("%.2f", order.getPrice()));

        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        putProductsInMachine();

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
                    prodImage = new Image("styles/" + prodInOrder.getProduct().getName() + ".png");
                } catch (Exception e) {
                    prodImage = new Image("styles/defultProductImage.png");
                }
                ImageView productImage = new ImageView(prodImage);
                productImage.setFitWidth(25);
                productImage.setFitHeight(30);

                Image xmark = new Image("styles/x-mark.png");
                ImageView xmarkImage = new ImageView(xmark);
                xmarkImage.setFitWidth(20);
                xmarkImage.setFitHeight(20);
                Integer amountOfProduct = new Integer(prodInOrder.getAmount());
               // Double priceOfProduct = new Double((double) (amountOfProduct * prodInOrder.getProduct().getPrice()));
                Double priceOfProduct = new Double((double) (calculatePriceAfterDiscount(amountOfProduct,prodInOrder.getProduct().getPrice())));

                hboxTop.getChildren().addAll(productImage, new Label("       x" + amountOfProduct.toString() + "   " + prodInOrder.getProduct().getName() + " - " + String.format("%.2f", priceOfProduct) + "₪"), pane, xmarkImage);


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
        if (order.getPrice() <= 0.001) {
            EmptyCartAlert.setVisible(true);
            return;
        }
        previousOrder = order;
        AnchorPane pane;
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/assets/BillWindow.fxml"));
            pane = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        Stage stage = StageSingleton.getInstance().getStage();
        stage.setTitle("Order confirmation");
        stage.setScene(new Scene(pane));
        stage.centerOnScreen();
        stage.setResizable(false);
    }
    private Image recieveImageForProduct(String productName)
    {
        Image prodImage = new Image("styles/" +productName+ ".png");
        return prodImage;
    }
    private void putProductsInMachine() {
        int buttonPlusMinus = 25;
        Image prodImage = null;
        Order order = recieveCurrentOrder();
        ObservableList<Product> products = (ObservableList<Product>) getAllProductsFromDB(order);
        ContinueButton.setOnMouseClicked(event -> ContinueOrder(order));
        ClearCart.setOnMouseClicked(event -> ClearCartClicked());
        ClearCart.setVisible(false);


        for (Product prod : products) {
            try {
                HBox hboxTop = new HBox();
                HBox hboxButtom = new HBox();
                VBox ProductSection = new VBox();
                Label ProdName = new Label("  " + prod.getName());
                Label counter = new Label("  1  ");
                Pane pane = new Pane();
                Button AddToCartButton = new Button("Add To Cart - " + prod.getPrice() + "₪");
                AddToCartButton.setStyle("styles/GoodButton.css");
                Image min = new Image("styles/minus.png");
                ImageView minusImage = new ImageView(min);
                minusImage.setFitWidth(buttonPlusMinus);
                minusImage.setFitHeight(buttonPlusMinus);
                minusImage.setOpacity(0.3);
                Image plus = new Image("styles/plus.png");
                ImageView plusImage = new ImageView(plus); //defultProductImage
                String productId;
                for (ProductInOrder prod1 : order.getProductsInOrder()) {
                    productId = prod1.getProduct().getProductId();
                    if (productId.equals(prod.getProductId())) {
                        if (prod1.getAmount() == getMaxAmountOfProductInMachineFromDB(order,productId)) {
                            counter.setText("  0  ");
                            AddToCartButton.setText("Out Of Stock");
                            plusImage.setOpacity(0.3);
                            AddToCartButton.setOpacity(0.3);
                            AddToCartButton.disabledProperty();
                        }
                        break;
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
                TotalOrderPrice.setText("Total Price: " + String.format("%.2f", order.getPrice()));
                try {
                        prodImage = recieveImageForProduct(prod.getName());
//
                } catch (Exception e) {
                    prodImage = new Image("styles/defultProductImage.png");
                }
                ImageView productImage = new ImageView(prodImage);
//                productImage.setFitWidth(25);
//                productImage.setFitHeight(30);
                productImage.setFitWidth(40);
                productImage.setFitHeight(47);
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

}

