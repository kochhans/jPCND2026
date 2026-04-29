package application.utils;

import java.util.List;
import java.util.function.Function;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;

public class ComboBoxUtils
{
	public static void makeSearchable(ComboBox<String> combo, List<String> items)
	{
	    ObservableList<String> master =
	            FXCollections.observableArrayList(items);

	    FilteredList<String> filtered =
	            new FilteredList<>(master, s -> true);

	    combo.setItems(filtered);
	    combo.setEditable(true);

	    TextField editor = combo.getEditor();

	    // 🔥 USER TYPING → FILTER
	    editor.textProperty().addListener((obs, old, neu) -> {

	        if (!editor.isFocused()) return;

	        if (neu == null || neu.isBlank()) {
	            filtered.setPredicate(s -> true);
	            return;
	        }

	        String filter = neu.toLowerCase();

	        filtered.setPredicate(item ->
	                item.toLowerCase().contains(filter)
	        );
	    });

	    // 🔥 USER HAT AUSGEWÄHLT → FILTER RESET
	    combo.setOnAction(e -> {

	        String value = combo.getValue();

	        // Filter zurücksetzen
	        filtered.setPredicate(s -> true);

	        // Editor sauber synchronisieren
	        if (value != null) {
	            editor.setText(value);
	        }
	    });

	    // 🔥 Popup sauber öffnen
	    combo.setOnMouseClicked(e -> combo.show());
	}
}
