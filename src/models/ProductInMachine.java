package models;

import java.io.Serializable;

public class ProductInMachine implements Serializable{
    String machineId;

    public ProductInMachine(String machineId, String productId, StatusInMachine statusInMachine, Double amount) {
        this.machineId = machineId;
        this.productId = productId;
        this.statusInMachine = statusInMachine;
        this.amount = amount;
    }

    String productId;
    StatusInMachine statusInMachine;
    Double amount;

    public String getMachineId() {
        return machineId;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public StatusInMachine getStatusInMachine() {
        return statusInMachine;
    }

    public void setStatusInMachine(StatusInMachine statusInMachine) {
        this.statusInMachine = statusInMachine;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
