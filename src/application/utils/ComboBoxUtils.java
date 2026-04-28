package application.utils;

import java.util.function.Function;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;


	public class ComboBoxUtils {

	    public static void makeSearchable(ComboBox<String> combo) {

	        ObservableList<String> originalItems =
	                FXCollections.observableArrayList(combo.getItems());

	        FilteredList<String> filtered =
	                new FilteredList<>(originalItems, s -> true);

	        combo.setItems(filtered);
	        combo.setEditable(true);

	        TextField editor = combo.getEditor();

	        editor.textProperty().addListener((obs, old, neu) -> {
	            if (neu == null) return;

	            String filter = neu.toLowerCase();

	            filtered.setPredicate(item ->
	                item.toLowerCase().contains(filter)
	            );

	            if (!combo.isShowing()) {
	                combo.show();
	            }
	        });

	        combo.setOnAction(e -> {
	            String selected = combo.getSelectionModel().getSelectedItem();
	            if (selected != null) {
	                combo.getEditor().setText(selected);
	            }
	        });
	    }
	}
