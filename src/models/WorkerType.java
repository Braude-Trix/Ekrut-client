package models;

import java.io.Serializable;

public enum WorkerType implements Serializable {
	// adding a constructor to define the string we will print
	CEO("CEO of EK-rut"),
	OperationalWorker("Operational worker"),
	MarketingManager("Marketing manager"),
	MarketingWorker("Marketing worker"),
	RegionalManager("Regional manager"),
	RegionalDelivery("Regional Delivery"),
	ServiceOperator("Service operator");

	private String name;

	WorkerType(String name)
	{
		this.name = name;
	}

	public boolean hasRegion() {
		return (this == RegionalDelivery) || (this == MarketingWorker)
				|| (this == RegionalManager); //|| (this == ServiceOperator);
	}

	@Override
	public String toString()
	{
		return name;
	}
}
