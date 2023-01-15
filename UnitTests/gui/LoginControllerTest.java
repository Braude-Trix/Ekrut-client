package gui;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import client.IClient;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import models.Customer;
import models.CustomerType;
import models.Method;
import models.Regions;
import models.Request;
import models.Response;
import models.ResponseCode;
import models.SaleStatus;
import models.User;
import models.Worker;
import models.WorkerType;
import utils.IUtil;

class LoginControllerTest {

	private LoginController loginCon;
	private boolean conditionPassword, conditionUsername;
	private String txtPassword;
	private String txtUsername;
	private String errorL;
	private String errorTouch;
	private Request requestTest;
	private Response responseTest;
	private Customer customer;
	private Worker worker;
	private User user;
	private List<Object> listForResponse;
	private Field config;
	private Field userInLoginCon;
	private java.lang.reflect.Method rEKCustomer;
	private java.lang.reflect.Method rUser;
	private java.lang.reflect.Method rOLUser;
	private java.lang.reflect.Method isValidSelectSubscriber;
	private java.lang.reflect.Method rById;
	private java.lang.reflect.Method setCombobox;

	private Integer subscriberId;
	private ObservableList<Integer> subscribersList;
	
	private String nameWindow = "LoginController";
	private boolean isRunThread;

	public class UtilTest implements IUtil {
		@Override
	    public boolean isBlankString(String s) {
	        return s.equals(txtUsername)? conditionUsername:conditionPassword;
	    }
		
		@Override
	    public String getTxtPassword()
	    {
	    	return txtPassword;
	    }
		
		@Override
		public String getTxtUsername() {
			return txtUsername;
		}
		
		@Override
		public String getErrorLabel() {
			return errorL;
		}

		@Override
		public void setErrorLabel(String msgError) {
			errorL = msgError;
		}

		@Override
		public Integer getValueSubscriberSelected() {
			return subscriberId;
		}

		@Override
		public void setValueSubscriberSelected(Integer id) {
			subscriberId = id;
		}

		@Override
		public String getErrorTouch() {
			return errorTouch;
		}

		@Override
		public void setErrorTouch(String msgError) {
			errorTouch = msgError;
		}

		@Override
		public ObservableList<Integer> getAllSubscribers() {
			return subscribersList;
		}

		@Override
		public void setAllSubscribers(ObservableList<Integer> options) {
			subscribersList = options;
		}		
	}
	
	public class ClientTest implements IClient{
		@Override
		public void setRequestForServer(Request request) {
			requestTest = request;
		}

		@Override
		public Response getResFromServer() {
			return responseTest;
		}
	}
	
	public class InitWindowTest implements IInitWindow{
		@Override
		public void runWindow(String nameController) throws Exception {
			nameWindow =  nameController;	
		}
	}
	
    public class RunThreadTest implements IRunThread{
		@Override
		public void Run() {
			isRunThread = true;
		}	
    }
	
	private UtilTest utilTest;
	private ClientTest clientTest;
	private InitWindowTest initWindowTest;
	private RunThreadTest runThreadTest;

	
	@SuppressWarnings("static-access")
	@BeforeEach
	void setUp() throws Exception {
		utilTest = new UtilTest();
		clientTest = new ClientTest();
		initWindowTest = new InitWindowTest();
		runThreadTest = new RunThreadTest();
		loginCon = new LoginController(utilTest, clientTest, initWindowTest, runThreadTest);
		user = new User("Zion","Assaf", 111111111, "zion@gmail.com", "0521234567", "user1","1234", false, "1111111111111111");
		worker = new Worker(user, WorkerType.MarketingWorker, Regions.North);
		customer = new Customer(user, CustomerType.Subscriber, null, null);	
		listForResponse = new ArrayList<>();
		
		config = LoginController.class.getDeclaredField("configuration");
		config.setAccessible(true);
		
		userInLoginCon = LoginController.class.getDeclaredField("user");
		userInLoginCon.setAccessible(true);
		userInLoginCon.set(loginCon, null);
		isRunThread = false;
		
		rEKCustomer = LoginController.class.getDeclaredMethod("requestEKCustomer");
		rEKCustomer.setAccessible(true);
		
		rUser = LoginController.class.getDeclaredMethod("requestUser");
		rUser.setAccessible(true);
		
		rOLUser = LoginController.class.getDeclaredMethod("requestOLUser");
		rOLUser.setAccessible(true);
		
		isValidSelectSubscriber = LoginController.class.getDeclaredMethod("isValidFillComboBoxes");
		isValidSelectSubscriber.setAccessible(true);
		
		rById = LoginController.class.getDeclaredMethod("requestUserById");
		rById.setAccessible(true);
		
		setCombobox = LoginController.class.getDeclaredMethod("setComboBoxFastLogin");
		setCombobox.setAccessible(true);
		
		loginCon.customerAndWorker = null;	
	}

	@Test
	void validationUsernameFail() throws Exception {
		conditionUsername = true;
		conditionPassword = false;
		txtUsername = "";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		String expected = "The username is incorrect";
		String result = errorL;
		assertEquals(expected, result);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@Test
	void validationPasswordFail() throws Exception {
		conditionUsername = false;
		conditionPassword = true;
		txtUsername = "user1";
		txtPassword = "";
		loginCon.login(new ActionEvent());
		String expected = "The password is incorrect";
		String result = errorL;
		assertEquals(expected, result);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@Test
	void validationPasswordAndUsernameFail() throws Exception {
		conditionUsername = true;
		conditionPassword = true;
		txtUsername = "";
		txtPassword = "";
		loginCon.login(new ActionEvent());
		String expected = "The username and password are incorrect";
		String result = errorL;
		assertEquals(expected, result);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestUserFailDB() throws Exception {
		responseTest = setResponse(ResponseCode.DB_ERROR, "Problem in fetching the data", null);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		Request result = requestTest;
		Request expected = getRequest("/login/getUser",Method.GET, new ArrayList<>(Arrays.asList(txtUsername,txtPassword)));
		assertTrue(isEqualsRequest(expected, result));
		assertEquals(responseTest.getDescription(), errorL);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestUserNotExist() throws Exception {
		responseTest = setResponse(ResponseCode.INVALID_DATA, "The username or password are incorrect", null);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		Request result = requestTest;
		Request expected = getRequest("/login/getUser",Method.GET, new ArrayList<>(Arrays.asList(txtUsername,txtPassword)));
		assertTrue(isEqualsRequest(expected, result));
		assertEquals(responseTest.getDescription(), errorL);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestUserResponseNull() throws Exception {
		responseTest = null;
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		Request result = requestTest;
		Request expected = getRequest("/login/getUser",Method.GET, new ArrayList<>(Arrays.asList(txtUsername,txtPassword)));
		assertTrue(isEqualsRequest(expected, result));
		assertEquals("Communication error", errorL);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestUser_responseOK() throws Exception {
		listForResponse.add(user);
		responseTest = setResponse(ResponseCode.OK, "Successfully got user details", listForResponse);
		loginCon.user = user;
		rUser.invoke(loginCon);
		assertEquals(loginCon.user, user);
		assertEquals(null, errorL);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestUser_BodyResponseNull() throws Exception {
		responseTest = setResponse(ResponseCode.OK, "Successfully got user details", null);
		rUser.invoke(loginCon);
		assertEquals(loginCon.user, null);
		assertEquals("Data error", errorL);
	}
	

	
	
//	@SuppressWarnings("static-access")
//	@Test
//	void requestUserLoggedIn() throws Exception {
//		responseTest = setResponse(ResponseCode.INVALID_DATA, "The user is already logged in", null);
//		conditionUsername = false;
//		conditionPassword = false;
//		txtUsername = "user1";
//		txtPassword = "1234";
//		loginCon.login(new ActionEvent());
//		Request result = requestTest;
//		Request expected = getRequest("/login/getUser",Method.GET, new ArrayList<>(Arrays.asList(txtUsername,txtPassword)));
//		assertTrue(isEqualsRequest(expected, result));
//		assertEquals(responseTest.getDescription(), errorL);
//		assertEquals(loginCon.user, null);
//	}
		
	///////////////////////////////////EK FLOW////////////////////////////////
	
	@SuppressWarnings("static-access")
	@Test
	void requestCustomerEkConfiguration_registeredUser() throws Exception {
		config.set(loginCon, "EK");
		listForResponse.add(customer);
		responseTest = setResponse(ResponseCode.OK, "Registered customer successfully accepted", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Customer)loginCon.user, customer);
		assertEquals(nameWindow, "EKController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestCustomerEkConfiguration_UnregisteredUser() throws Exception {
		responseTest = setResponse(ResponseCode.INVALID_DATA, "Unregistered user", null);
		loginCon.user = user;
		rEKCustomer.invoke(loginCon);
		Request result = requestTest;
		Request expected = getRequest("/login/getUserForEkConfiguration",Method.GET, new ArrayList<>(Arrays.asList(user)));
		assertTrue(isEqualsRequest(expected, result));
		assertEquals(loginCon.user, user);
		assertEquals(null, errorL);
		assertEquals(nameWindow, "UnregisteredUserController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestCustomerEkConfiguration_responseError() throws Exception {
		responseTest = setResponse(ResponseCode.DB_ERROR, "Error loading data (DB)", null);
		loginCon.user = user;
		rEKCustomer.invoke(loginCon);
		assertEquals(loginCon.user, user);
		assertEquals("Error loading data (DB)", errorL);
		assertEquals(nameWindow, "LoginController");
	}
	
	@Test
	void requestCustomerEkConfiguration_responseNull() throws Exception {
		responseTest = null;
		rEKCustomer.invoke(loginCon);
		assertEquals("Communication error", errorL);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestCustomerEkConfiguration_BodyResponseNull() throws Exception {
		responseTest = setResponse(ResponseCode.OK, "Registered customer successfully accepted", null);
		rEKCustomer.invoke(loginCon);
		assertEquals(loginCon.user, null);
		assertEquals("Data error", errorL);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestCustomerEkConfiguration_BodyResponseWorkerFail() throws Exception {
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "Registered customer successfully accepted", listForResponse);
		rEKCustomer.invoke(loginCon);
		assertEquals(loginCon.user, null);
		assertEquals("Data error", errorL);
		assertEquals(nameWindow, "LoginController");
	}
	
	///////////////////////////////////OL FLOW////////////////////////////////
	
	@SuppressWarnings("static-access")
	@Test
	void request_CustomerOLConfiguration_registeredUser() throws Exception {
		config.set(loginCon, "OL");
		listForResponse.add(customer);
		responseTest = setResponse(ResponseCode.OK, "Registered customer successfully accepted", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Customer)loginCon.user, customer);
		assertEquals(nameWindow, "OLController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_MarketingWorker_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.MarketingWorker);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "MarketingWorkerWindowController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_MarketingManager_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.MarketingManager);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "MarketingManagerController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_CEO_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.CEO);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "CeoGui");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_OperationalWorker_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.OperationalWorker);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "OperationalWorkerGui");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_RegionalDelivery_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.RegionalDelivery);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "RegionalDeliveryController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_RegionalManager_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.RegionalManager);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "RegionalManagerGui");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_ServiceOperator_OLConfiguration_registeredUser() throws Exception {
		worker.setType(WorkerType.ServiceOperator);
		config.set(loginCon, "OL");
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Worker)loginCon.user, worker);
		assertEquals(nameWindow, "ServiceOperatorController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_WorkerAndCustomer_OLConfiguration_registeredUser() throws Exception {
		config.set(loginCon, "OL");
		listForResponse.add(customer);
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		conditionUsername = false;
		conditionPassword = false;
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		assertEquals("", errorL);
		assertEquals((Customer)loginCon.customerAndWorker.get(0), customer);
		assertEquals((Worker)loginCon.customerAndWorker.get(1), worker);
		assertEquals(nameWindow, "SelectOptionWorkerOrCustomer");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestOL_WorkerAndCustomer_reverseOrderFail() throws Exception {
		listForResponse.add(worker);
		listForResponse.add(customer);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		assertEquals("Data error", errorL);
		assertEquals(loginCon.customerAndWorker, null);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestOL_WorkerAndCustomer_CustomerAndCustomerFail() throws Exception {
		listForResponse.add(worker);
		listForResponse.add(worker);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		loginCon.user = user;

		rOLUser.invoke(loginCon);
		assertEquals("Data error", errorL);
		assertEquals(loginCon.customerAndWorker, null);
		assertEquals(nameWindow, "LoginController");
	}
	
	
	@SuppressWarnings("static-access")
	@Test
	void request_OLConfiguration_UnregisteredUser() throws Exception {
		responseTest = setResponse(ResponseCode.INVALID_DATA, "Unregistered user", null);
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		assertEquals(null, errorL);
		assertEquals(loginCon.user, user);
		assertEquals(nameWindow, "UnregisteredUserController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_OLConfiguration_unknownWorkerTypeNull() throws Exception {
		listForResponse.add(worker);
		worker.setType(null);
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		assertEquals("Employee error", errorL);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_OLConfiguration_NullBodyResponse() throws Exception {
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", null);
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		assertEquals("Data error", errorL);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_OLConfiguration_ErrorBodyData() throws Exception {
		listForResponse.add("error");
		responseTest = setResponse(ResponseCode.OK, "The employee has successfully logged in", listForResponse);
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		assertEquals("Data error", errorL);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
	}
	
	
	@SuppressWarnings("static-access")
	@Test
	void request_OLConfiguration_DBFailed() throws Exception {
		responseTest = setResponse(ResponseCode.DB_ERROR, "Error loading data (DB)", null);
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		assertEquals("Error loading data (DB)", errorL);
		assertEquals(loginCon.user, user);
		assertEquals(nameWindow, "LoginController");
	}
	
	@Test
	void request_OLConfiguration_nullResponse() throws Exception {
		responseTest = null;
		rOLUser.invoke(loginCon);
		assertEquals("Communication error", errorL);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestOLUser_Success() throws Exception {
		loginCon.user = user;
		rOLUser.invoke(loginCon);
		Request result = requestTest;
		Request expected = getRequest("/login/getUserForOLConfiguration",Method.GET, new ArrayList<>(Arrays.asList(user)));
		assertTrue(isEqualsRequest(expected, result));
	}
	
	///////////////////////////////////Login Simulation//////////////////////////////
	@Test
	void EKConfiguration_TouchSimulation_NotSelectedSubscriber() throws Exception {	
		loginCon.btnTouch(new ActionEvent());
		assertEquals(null, errorL);
		assertEquals("Please select subscriber id", errorTouch);
		assertEquals(nameWindow, "LoginController");
	}
	
	@Test
	void EKConfiguration_TouchSimulation_SelectedSubscriber() throws Exception {
		subscriberId = user.getId();
		boolean isValid = (boolean) isValidSelectSubscriber.invoke(loginCon);
		assertTrue(isValid);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_Subscriber_EKConfiguration_SuccessSimulation() throws Exception {
		subscriberId = user.getId();
		listForResponse.add(customer);
		responseTest = setResponse(ResponseCode.OK, "Successfully got user details", listForResponse);
		loginCon.btnTouch(new ActionEvent());
		assertEquals(null, errorL);
		assertEquals(null, errorTouch);
		assertEquals((Customer)loginCon.user, customer);
		assertEquals(nameWindow, "EKController");
		assertEquals(isRunThread, true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	void request_Subscriber_EKConfiguration_FailSimulation() throws Exception {
		subscriberId = user.getId();
		listForResponse.add(customer);
		responseTest = setResponse(ResponseCode.DB_ERROR, "Error loading data (DB)", listForResponse);
		loginCon.btnTouch(new ActionEvent());
		assertEquals(null, errorL);
		assertEquals("Error loading data (DB)", errorTouch);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
		assertEquals(isRunThread, false);
	}
	
	@Test
	void requestById_Success() throws Exception {
		subscriberId = user.getId();
		rById.invoke(loginCon);
		Request result = requestTest;
		Request expected = getRequest("/login/getCustomerById",Method.GET, new ArrayList<>(Arrays.asList(subscriberId)));
		assertTrue(isEqualsRequest(expected, result));
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestById_BodyResponseNull() throws Exception {
		responseTest = setResponse(ResponseCode.OK, "Successfully got user details", null);
		rById.invoke(loginCon);
		assertEquals(null, errorL);
		assertEquals("Data error", errorTouch);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
	}
	
	@SuppressWarnings("static-access")
	@Test
	void requestById_NullResponse() throws Exception {
		rById.invoke(loginCon);
		assertEquals(null, errorL);
		assertEquals("Communication error", errorTouch);
		assertEquals(loginCon.user, null);
		assertEquals(nameWindow, "LoginController");
	}
	

	
	@Test
	void requestSetCombobox_Success() throws Exception {
		setCombobox.invoke(loginCon);
		Request result = requestTest;
		Request expected = getRequest("/login/getAllSubscriberForFastLogin",Method.GET, null);
		assertTrue(isEqualsRequest(expected, result));
	}
	
	@Test
	void requestSetCombobox_NullResponse() throws Exception {
		setCombobox.invoke(loginCon);
		assertEquals(null, errorL);
		assertEquals("Communication error", errorTouch);
	}
	
	@Test
	void requestSetCombobox_BodyResponseNull() throws Exception {
		responseTest = setResponse(ResponseCode.OK, "Successfully sent all subscribers id", null);
		setCombobox.invoke(loginCon);
		assertEquals(null, errorL);
		assertEquals(null, errorTouch);
		assertEquals(null, subscribersList);		
	}
	
	@Test
	void requestSetCombobox_ErrorDBResponse() throws Exception {
		responseTest = setResponse(ResponseCode.DB_ERROR, "Error loading data (DB)", null);
		setCombobox.invoke(loginCon);
		assertEquals(null, errorL);
		assertEquals("Error loading data (DB)", errorTouch);
	}
	
	@Test
	void requestSetCombobox_OKResponse() throws Exception {
		listForResponse.add(customer.getId());
		responseTest = setResponse(ResponseCode.OK, "Successfully sent all subscribers id", listForResponse);
		setCombobox.invoke(loginCon);
		assertEquals(1, subscribersList.size());
		assertEquals(customer.getId(), subscribersList.get(0));
		assertEquals(null, errorL);
		assertEquals(null, errorTouch);
	}

	///////////////////////////////////helpful Method////////////////////////////////
	private Request getRequest(String path, Method method, List<Object> body) {
		Request req = new Request();
		req.setBody(body);
		req.setMethod(method);
		req.setPath(path);
		return req;
	}
	
	private Response setResponse(ResponseCode code, String description, List<Object> body) {
		Response res = new Response();
		res.setBody(body);
		res.setCode(code);
		res.setDescription(description);
		return res;
	}
	/*
	@Test
	void requestUserSuccess() throws Exception {
		conditionUsername = false;
		conditionPassword = false;
		stringUsername = "user1";
		txtUsername = "user1";
		txtPassword = "1234";
		loginCon.login(new ActionEvent());
		String expected = "The username and password are incorrect";
		String result = errorL;
		assertEquals(expected, result);
	}*/
	
	private boolean isEqualsRequest(Request r1, Request r2) {
		if (!r1.getPath().equals(r2.getPath())) {
			return false;
		}
		if (!(r1.getMethod()==r2.getMethod())) {
			return false;
		}
		
		if(r1.getBody() == null && r2.getBody() == null) {
			return true;
		}
		
		if (r1.getBody().size() != r2.getBody().size()) {
			return false;
		}
		
		int i=0;
		for (Object object:r1.getBody()) {
			if (!r2.getBody().get(i).equals(object)) {
				return false;
			}
			i++;
		}
		return true;
	}

}
