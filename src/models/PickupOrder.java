package models;

import java.io.Serializable;

public class PickupOrder extends Order implements Serializable {
	private String pickupCode;
	private Machine selectedMachine;
	
	public PickupOrder(String orderId, String date, double price, String machineId, String status,
			PickUpMethod pickUpMethod, String pickupCode, Machine selectedMachine) {
		super(orderId, date, price, machineId, status, pickUpMethod);
		this.pickupCode = pickupCode;
		this.selectedMachine = selectedMachine;
	}

	public String getPickupCode() {
		return pickupCode;
	}

	public void setPickupCode(String pickupCode) {
		this.pickupCode = pickupCode;
	}

	public Machine getSelectedMachine() {
		return selectedMachine;
	}

	public void setSelectedMachine(Machine selectedMachine) {
		this.selectedMachine = selectedMachine;
	}

}
