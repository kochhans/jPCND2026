package application.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.ComboBox;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class AutoCompleteComboBoxListener<T>
        implements EventHandler<KeyEvent> {

    private final ComboBox<T> comboBox;
    //private final StringBuilder sb;
    private final ObservableList<T> data;

    private boolean moveCaretToPos = false;
    private int caretPos;

    public AutoCompleteComboBoxListener(final ComboBox<T> comboBox) {
        this.comboBox = comboBox;
        //this.sb = new StringBuilder();
        this.data = comboBox.getItems();

        this.comboBox.setEditable(true);

        // Lambda statt anonymer Klasse
        this.comboBox.setOnKeyPressed(event -> comboBox.hide());

        this.comboBox.setOnKeyReleased(this);
    }

    @Override
    public void handle(KeyEvent event) {

        if (event.getCode() == KeyCode.UP) {
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;

        } else if (event.getCode() == KeyCode.DOWN) {
            if (!comboBox.isShowing()) {
                comboBox.show();
            }
            caretPos = -1;
            moveCaret(comboBox.getEditor().getText().length());
            return;

        } else if (event.getCode() == KeyCode.BACK_SPACE
                || event.getCode() == KeyCode.DELETE) {

            moveCaretToPos = true;
            caretPos = comboBox.getEditor().getCaretPosition();
        }

        if (event.getCode() == KeyCode.RIGHT
                || event.getCode() == KeyCode.LEFT
                || event.isControlDown()
                || event.getCode() == KeyCode.HOME
                || event.getCode() == KeyCode.END
                || event.getCode() == KeyCode.TAB) {
            return;
        }

        ObservableList<T> filteredList = FXCollections.observableArrayList();

        String text = comboBox.getEditor().getText().toLowerCase();

        for (T item : data) {
            if (item.toString().toLowerCase().startsWith(text)) {
                filteredList.add(item);
            }
        }

        comboBox.setItems(filteredList);
        comboBox.getEditor().setText(text);

        if (!moveCaretToPos) {
            caretPos = -1;
        }

        moveCaret(text.length());

        if (!filteredList.isEmpty()) {
            comboBox.show();
        }
    }

    private void moveCaret(int textLength) {
        if (caretPos == -1) {
            comboBox.getEditor().positionCaret(textLength);
        } else {
            comboBox.getEditor().positionCaret(caretPos);
        }
        moveCaretToPos = false;
    }
}
