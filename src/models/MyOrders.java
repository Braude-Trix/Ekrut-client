package models;

import java.io.Serializable;


import javafx.scene.control.Button;

public class MyOrders extends Order implements Serializable {

    private String receivedDate;
    private Button btnApprove;
    
    public MyOrders(String orderId, String execDate, String receivedDate, double price, String machineId, String status, PickUpMethod pickUpMethod) {
    	super (orderId, execDate, price, machineId, status, pickUpMethod);
        this.receivedDate = receivedDate;
        btnApprove = new Button("Approve");
    }
    
    public Button getBtnApprove() {
		return btnApprove;
	}


	public void setBtnApprove(Button btnApprove) {
		this.btnApprove = btnApprove;
	}

	public String getReceivedDate() {
		return receivedDate;
	}


	public void setReceivedDate(String receivedDate) {
		this.receivedDate = receivedDate;
	}

}
