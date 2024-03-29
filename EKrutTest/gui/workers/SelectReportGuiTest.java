package gui.workers;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import client.IClient;
import models.Regions;
import models.ResponseCode;
import models.SavedReportRequest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import models.InventoryReport;
import models.Machine;
import models.ReportType;
import models.Request;
import models.Response;


@RunWith(MockitoJUnitRunner.class)
class SelectReportGuiTest {

	private static final ReportType REPORT_TYPE = ReportType.INVENTORY;
	private static final Regions REGION = Regions.North;
	private static final String INVENTORY_FXML = "/assets/workers/fxmls/InventoryReportPage.fxml";
	private static final InventoryReport REPORT = new InventoryReport(null, null, null, null, null, null);
	IClient clientMock;
	IReportPopupOpen popUpMock;
	static SelectReportGui selectReportGui;
	static Field reportType;
	static IComboBox machineMock;
	static IComboBox yearMock;
	static IComboBox monthMock;
	static Field machinesSet;
	static Method loadReportPopup;

	private List<Machine> getMachinesSet() {
		return Arrays.asList(
				new Machine("123", "EM Building braude", Regions.North.name(), "5"),
				new Machine("999", "Not Exists", Regions.North.name(), "5"));
	}

    private Request getReportRequest(SavedReportRequest reportRequest) {
        List<Object> paramList = new ArrayList<>();
        paramList.add(reportRequest);

        Request request = new Request();
        request.setPath("/reports");
        request.setMethod(models.Method.GET);
        request.setBody(paramList);
    	return request;
    }

	private Response getOkResponse() {
		Response okResponse = new Response();
		okResponse.setBody(Arrays.asList(REPORT));
		okResponse.setDescription("Server successfully fetched report");
		okResponse.setCode(ResponseCode.OK);
		return okResponse;
	}

	private Response getErrorResponse() {
		Response errorResponse = new Response();
		errorResponse.setBody(null);
		errorResponse.setDescription("Server had error in fetching report");
		errorResponse.setCode(ResponseCode.INVALID_DATA);
		return errorResponse;
	}

	 @BeforeAll
	 public static void beforeClass() throws NoSuchFieldException, NoSuchMethodException {
		 reportType = SelectReportGui.class.getDeclaredField("reportType");
		 machinesSet = SelectReportGui.class.getDeclaredField("machinesSet");
		 loadReportPopup = SelectReportGui.class.getDeclaredMethod("loadReportPopup");

		 reportType.setAccessible(true);
		 machinesSet.setAccessible(true);
		 loadReportPopup.setAccessible(true);
		 RegionalManagerGui.region = REGION;
	 }

	 @BeforeEach
	 public void setUp() {
		 clientMock = mock(IClient.class);
		 popUpMock = mock(IReportPopupOpen.class);
		 machineMock = mock(IComboBox.class);
		 yearMock = mock(IComboBox.class);
		 monthMock = mock(IComboBox.class);
         selectReportGui = new SelectReportGui(clientMock, popUpMock, machineMock, yearMock, monthMock);
	 }

	// Functionality: Test the loadReportPopup method when it is successful.
	// Input data: machine = "EM Building braude", year = "2022",
	// month = "12", reportType = REPORT_TYPE, machinesSet = predefine machines.
	// Expected result: The client will send the request to the server
	// with the expected request body and the reportPopup will open
	// with the expected machine name, year, month, and report data.
	@Test
	void successfullyLoadingReport() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		reportType.set(selectReportGui, REPORT_TYPE);
		machinesSet.set(selectReportGui, getMachinesSet());
		when(machineMock.getSelected()).thenReturn("EM Building braude");
		when(yearMock.getSelected()).thenReturn("2022");
		when(monthMock.getSelected()).thenReturn("12");
		when(clientMock.getResFromServer()).thenReturn(getOkResponse());
        SavedReportRequest expectedRequestBody = new SavedReportRequest(2022, 12, REPORT_TYPE, REGION, 123);
    	Request expectedRequest = getReportRequest(expectedRequestBody);

		Boolean actualIsLoaded = (Boolean) loadReportPopup.invoke(selectReportGui);

		verify(clientMock).setRequestForServer(expectedRequest);
		verify(popUpMock).openReportPopup(INVENTORY_FXML);
        assertEquals(InventoryReportPopupGui.machineName, "EM Building braude");
        assertEquals(InventoryReportPopupGui.year, 2022);
        assertEquals(InventoryReportPopupGui.month, 12);
        assertEquals(InventoryReportPopupGui.inventoryReportData, REPORT);
		assertTrue(actualIsLoaded);
	}

	// Functionality: Test the checkGetReportRequest method when the server returns an error.
	// Input data: Report type = REPORT_TYPE, machine = "EM Building braude",
	// year = "2022", month = "12", response = predefine response with error.
	// Expected result: The request sent to the server should match the expected request,
	// the report popup is not opened, the method returns false.
	@Test
	void failedLoadingReportOnServerError() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		reportType.set(selectReportGui, REPORT_TYPE);
		machinesSet.set(selectReportGui, getMachinesSet());
		when(machineMock.getSelected()).thenReturn("EM Building braude");
		when(yearMock.getSelected()).thenReturn("2022");
		when(monthMock.getSelected()).thenReturn("12");
		when(clientMock.getResFromServer()).thenReturn(getErrorResponse());
        SavedReportRequest expectedRequestBody = new SavedReportRequest(2022, 12, REPORT_TYPE, REGION, 123);
    	Request expectedRequest = getReportRequest(expectedRequestBody);

		Boolean actualIsLoaded = (Boolean) loadReportPopup.invoke(selectReportGui);

		verify(clientMock).setRequestForServer(expectedRequest);
		verify(popUpMock, times(0)).openReportPopup(INVENTORY_FXML);
		assertFalse(actualIsLoaded);
	}

	// Functionality: Test the loadReportPopup method when it fails because invalid input.
	// Input data: selected machine = null,selected year = null,selected month = null.
	// Expected result: An exception is thrown and the method does not open the report popup.
	@Test
	void failedLoadingReportOnInvalidInput() throws IllegalArgumentException {
		when(machineMock.getSelected()).thenReturn(null);
		when(yearMock.getSelected()).thenReturn(null);
		when(monthMock.getSelected()).thenReturn(null);

		try {
			loadReportPopup.invoke(selectReportGui);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		}
	}
}
