package models;

import java.io.Serializable;

public class Subscriber implements Serializable {

	private Integer monthlyBill;
	private Integer idNumber;
	
	public Subscriber(Integer idNumber, Integer monthlyBill) {
		this.idNumber = idNumber;
		this.monthlyBill = monthlyBill;
	}

	public Integer getMonthlyBill() {
		return monthlyBill;
	}

	public void setMonthlyBill(Integer monthlyBill) {
		this.monthlyBill = monthlyBill;
	}

	public Integer getIdNumber() {
		return idNumber;
	}

	public void setIdNumber(Integer idNumber) {
		this.idNumber = idNumber;
	}
	
	
//	private String firstName;
//	private String lastName;
//	private String id;
//	private String phoneNumber;
//	private String emailAddress;
//	private String creditCardNumber;
//	private String subscriberNumber;
//
//	public Subscriber(String firstName, String lastName, String id, String phoneNumber, String emailAddress,
//					  String creditCardNumber, String subscriberNumber) {
//		this.firstName = firstName;
//		this.lastName = lastName;
//		this.id = id;
//		this.phoneNumber = phoneNumber;
//		this.emailAddress = emailAddress;
//		this.creditCardNumber = creditCardNumber;
//		this.subscriberNumber = subscriberNumber;
//	}
//	public String getFirstName() {
//		return firstName;
//	}
//	public void setFirstName(String firstName) {
//		this.firstName = firstName;
//	}
//	public String getLastName() {
//		return lastName;
//	}
//	public void setLastName(String lastName) {
//		this.lastName = lastName;
//	}
//	public String getId() {
//		return id;
//	}
//	public void setId(String id) {
//		this.id = id;
//	}
//	public String getPhoneNumber() {
//		return phoneNumber;
//	}
//	public void setPhoneNumber(String phoneNumber) {
//		this.phoneNumber = phoneNumber;
//	}
//	public String getEmailAddress() {
//		return emailAddress;
//	}
//	public void setEmailAddress(String emailAddress) {
//		this.emailAddress = emailAddress;
//	}
//	public String getCreditCardNumber() {
//		return creditCardNumber;
//	}
//	public void setCreditCardNumber(String creditCardNumber) {
//		this.creditCardNumber = creditCardNumber;
//	}
//	public String getSubscriberNumber() {
//		return subscriberNumber;
//	}
//	public void setSubscriberNumber(String subscriberNumber) {
//		this.subscriberNumber = subscriberNumber;
//	}
}
