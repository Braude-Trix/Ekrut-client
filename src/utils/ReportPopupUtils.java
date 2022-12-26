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

    public static void setSubTitle(Label titleInfo, int month, int year) {
        String title = String.format("%s/%s", month, year);
        titleInfo.setText(title);
    }

    public static void setSubTitle(Label titleInfo, String machineName, int month, int year) {
        String title = String.format("%s - %s/%s", machineName, month, year);
        titleInfo.setText(title);
    }

    public static int getDaysOfMonth(int month, int year) {
        // Get the number of days in that month
        YearMonth yearMonthObject = YearMonth.of(year, month);
        return yearMonthObject.lengthOfMonth();
    }

    public static void onCloseClicked() {
        SelectReportGui.popupDialog.close();
    }
}
