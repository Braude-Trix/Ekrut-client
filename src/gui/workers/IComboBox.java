package gui.workers;

import javafx.scene.control.ComboBox;

public interface IComboBox {
    ComboBox<String> getComboBox();
    String getSelected();
}
