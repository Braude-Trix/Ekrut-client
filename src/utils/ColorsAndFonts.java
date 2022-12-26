package utils;

import javafx.scene.text.Font;

/**
 * Util class for constant colors and fonts
 */
public final class ColorsAndFonts {
    private ColorsAndFonts() {
    }

    public static final String SUCCESS_COLOR = "#363b4f";
    public static final String ERROR_COLOR = "#ff4747";
    public static final String ERROR_COMBO_BOX_COLOR = String.format("-fx-background-color: %s", ERROR_COLOR);
    public static final Font CONTENT_FONT = new Font("System", 13);
    public static final Font TITLE_FONT = new Font("System", 22);
    public static final String OK_COMBO_BOX_COLOR = "-fx-background-color: #d4d8db";
}
