package models;

import java.io.Serializable;

public class ProductInOrder implements Serializable {
    private Product product;
    private Double amount;
    private Double totalProductPrice;


    private String productName;


    public ProductInOrder(Product product, Double amount, Double totalProductPrice) {
        super();
        this.product = product;
        this.amount = amount;
        this.totalProductPrice = totalProductPrice;
        this.productName = product.getName();
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getTotalProductPrice() {
        return totalProductPrice;
    }

    public void setTotalProductPrice(Double totalProductPrice) {
        this.totalProductPrice = totalProductPrice;
    }


    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }
}



