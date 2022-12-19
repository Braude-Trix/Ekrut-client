package models;

import java.io.Serializable;

public class DeliveryOrder extends Order implements Serializable {

	private String shipmentAddress;
	private String timeForDelivery;
	private String distanceFromOperationsCenter;
	private String regionalManagerId;
	
	public DeliveryOrder(String orderId, String date, double price, String machineId, String status,
			PickUpMethod pickUpMethod, String shipmentAddress, String timeForDelivery, String distanceFromOperationsCenter, 
			String regionalManagerId) {
		super(orderId, date, price, machineId, status, pickUpMethod);
		this.shipmentAddress = shipmentAddress;
		this.timeForDelivery = timeForDelivery;
		this.distanceFromOperationsCenter = distanceFromOperationsCenter;
		this.regionalManagerId = regionalManagerId;
	}

	public String getShipmentAddress() {
		return shipmentAddress;
	}

	public void setShipmentAddress(String shipmentAddress) {
		this.shipmentAddress = shipmentAddress;
	}

	public String getTimeForDelivery() {
		return timeForDelivery;
	}

	public void setTimeForDelivery(String timeForDelivery) {
		this.timeForDelivery = timeForDelivery;
	}

	public String getDistanceFromOperationsCenter() {
		return distanceFromOperationsCenter;
	}

	public void setDistanceFromOperationsCenter(String distanceFromOperationsCenter) {
		this.distanceFromOperationsCenter = distanceFromOperationsCenter;
	}

	public String getRegionalManagerId() {
		return regionalManagerId;
	}

	public void setRegionalManagerId(String regionalManagerId) {
		this.regionalManagerId = regionalManagerId;
	}

	
}
