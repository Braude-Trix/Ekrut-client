package logic;

import client.Client;
import client.ClientUI;
import models.Method;
import models.Request;
import models.ResponseCode;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author gal
 * A class that implements a runnable task for generating all repots at the end of month
 */
public class EndOfMonthTask implements Runnable {
    private int lastCollectedMonth = -1;
    private final int HOUR_IN_MILLIS = 60 * 60 * 1000;

    /**
     * Sends a request to the server to create all reports on the end of each month
     * checks if last month is current month, if true, thread sleeps for 1 hour.
     * else sends request to server to create monthly reports
     * This task is executed every 1 hour until the thread is interrupted.
     */
    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Calendar calendar = Calendar.getInstance();
                int currentMonth = calendar.get(Calendar.MONTH) + 1;
                if (lastCollectedMonth != currentMonth) {
                    System.out.println("[Reports] Month is over, starting creating reports");
                    boolean created_successfully = createAllReports();
                    if (created_successfully)
                        lastCollectedMonth = currentMonth;
                    // else => will retry next hour
                } else {
                    System.out.println("[Reports] Didn't pass a month, doesn't creating reports");
                }
                Thread.sleep(HOUR_IN_MILLIS);
            } catch (InterruptedException e) {
                System.out.println(e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
    }

    private boolean createAllReports() {
        List<Object> paramList = new ArrayList<>();
        Request request = new Request();
        request.setPath("/reports/allReports");
        request.setMethod(Method.POST);
        request.setBody(paramList);
        ClientUI.chat.accept(request);
        if (Client.resFromServer.getCode() == ResponseCode.OK) {
            System.out.println("[Reports] Reports has been created successfully");
            return true;
        } else {
            System.out.println("[Reports] Some error occurred while trying to create reports, please try again manually");
            return false;
        }
    }
}
