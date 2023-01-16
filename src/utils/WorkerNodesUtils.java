package utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Regions;
import models.Worker;
import models.WorkerType;

import java.util.ArrayList;
import java.util.List;

/**
 * Util class for general and common javafx Nodes for Worker windows
 */
public final class WorkerNodesUtils {
    private WorkerNodesUtils() {
    }

    /**
     * @param msg to add to label
     * @return Success Label that holds the msg content
     */
    public static Label getSuccessLabel(String msg) {
        Label label = getCenteredContentLabel(msg);
        label.setTextFill(Paint.valueOf(ColorsAndFonts.SUCCESS_COLOR));
        return label;
    }

    /**
     * @param msg to add to label
     * @return Error Label that holds the msg content
     */
    public static Label getErrorLabel(String msg) {
        Label errorLabel = getCenteredContentLabel(msg);
        errorLabel.setTextFill(Paint.valueOf(ColorsAndFonts.ERROR_COLOR));
        return errorLabel;
    }

    /**
     * @param msg to add to label
     * @return Centered Label that holds the msg content
     */
    public static Label getCenteredContentLabel(String msg) {
        Label label = new Label();
        label.setFont(ColorsAndFonts.CONTENT_FONT);
        label.setText(msg);
        label.setAlignment(Pos.CENTER);
        return label;
    }

    /**
     * @param e The Class object of the content of the table to be created
     * @param <E> The type of the class contant in table
     * @return TableView instance of the specified type
     */
    public static <E> TableView<E> getTableView(Class<E> e) {
        TableView<E> tableView = new TableView<>();
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableView.setPrefSize(580, 300);
        tableView.setMaxSize(tableView.getPrefWidth(), tableView.getPrefHeight());
        tableView.setPadding(new Insets(5, 0, 0, 0));
        tableView.getStylesheets().add("styles/worker_table.css");
        return tableView;
    }

    /**
     * @param start from index
     * @param end to index (included)
     * @return List in size of end-start+1, that holds strings that are indexes in that bounded range
     */
    public static List<String> getListInRange(int start, int end) {
        List<String> rangeList = new ArrayList<>();
        for (int i = start; i <= end; i++) {
            rangeList.add(String.valueOf(i));
        }
        return rangeList;
    }

    /**
     * Setting Image of the specified path to the specified ImageView
     */
    public static void setBackground(String pathToImg, ImageView bgImage) {
        Image image = new Image(pathToImg);
        bgImage.setImage(image);
    }

    /**
     * Setting Hello full name for specified Label
     */
    public static void setUserName(Label userNameLabel, Worker worker) {
        userNameLabel.setText("Hello " + worker.getFirstName() + " " + worker.getLastName());
    }

    /**
     * Setting Role and region of worker for specified Label
     */
    public static void setRole(Label userRoleLabel, Regions region, WorkerType workerType) {
        if (region != null && region != Regions.All)
            userRoleLabel.setText(workerType.toString() + ": " + region.name());
        else
            setRole(userRoleLabel, workerType);
    }

    /**
     * Setting Role of worker for specified Label
     */
    public static void setRole(Label userRoleLabel, WorkerType workerType) {
        userRoleLabel.setText(workerType.toString());
    }

    /**
     * Adding styled title to specified Vbox
     */
    public static void setTitle(String titleName, VBox vBoxContainer) {
        Label title = new Label();
        title.setText(titleName);
        title.setFont(ColorsAndFonts.TITLE_FONT);
        vBoxContainer.getChildren().add(title);
    }

    /**
     * @param btnName Text to represent on the returned Button
     * @return Button with style width(=85)
     */
    public static Button createWide85Button(String btnName) {
        Button button = new Button();
        button.setText(btnName);
        button.setPrefSize(85, 30);
        button.setMaxSize(button.getPrefWidth(), button.getPrefHeight());
        return button;
    }

    /**
     * @param btnName Text to represent on the returned Button
     * @return Button with style width(=200)
     */
    public static Button createWide200Button(String btnName) {
        Button button = new Button();
        button.setPrefSize(200, 40);
        button.setMaxSize(button.getPrefWidth(), button.getPrefHeight());
        button.setText(btnName);
        return button;
    }

    /**
     * Helper Class that holds the x,y coordinates
     */
    static class Delta {
        double x, y;
    }

    /**
     * Function that enables movement of an UNDECORATED stage
     * @param stage The UNDECORATED stage to enable movement on
     */
    public static void setStageMovable(Stage stage) {
        final Delta dragDelta = new Delta();
        stage.getScene().setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
        });
        stage.getScene().setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
    }
}
