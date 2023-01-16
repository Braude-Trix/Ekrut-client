package utils;

import gui.workers.SelectReportGui;
import javafx.scene.control.Label;

import java.time.YearMonth;

/**
 * Util class for general and common report popups functions
 */
public final class ReportPopupUtils {
    private ReportPopupUtils() {
    }

    /**
     * Setting subTitle for non machine based reports (i.e. Users/Orders)
     * @param titleInfo The label to be set text to
     * @param month The month number to add to title
     * @param year The year number to add to title
     */
    public static void setSubTitle(Label titleInfo, int month, int year) {
        String title = String.format("%s/%s", month, year);
        titleInfo.setText(title);
    }

    /**
     * Setting subTitle for machine based reports (i.e. Inventory)
     * @param titleInfo The label to be set text to
     * @param machineName The name of the machine's report
     * @param month The month number to add to title
     * @param year The year number to add to title
     */
    public static void setSubTitle(Label titleInfo, String machineName, int month, int year) {
        String title = String.format("%s - %s/%s", machineName, month, year);
        titleInfo.setText(title);
    }

    /**
     * @return number of days of specified month and year number
     */
    public static int getDaysOfMonth(int month, int year) {
        // Get the number of days in that month
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    /**
     * Closing report popup window
     */
    public static void onCloseClicked() {
        SelectReportGui.popupDialog.close();
    }
}
