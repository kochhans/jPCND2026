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
		ObservableList<String> master = FXCollections.observableArrayList(items);

		FilteredList<String> filtered = new FilteredList<>(master, s -> true);

		combo.setItems(filtered);
		combo.setEditable(true);

		TextField editor = combo.getEditor();

		editor.textProperty().addListener((obs, old, neu) -> {

			// 🔥 NUR reagieren wenn Benutzer wirklich tippt
			if (!editor.isFocused())
				return;
			if (neu == null)
				return;

			String filter = neu.toLowerCase();

			filtered.setPredicate(item -> item.toLowerCase().contains(filter));

			// Dropdown nur öffnen wenn User tippt
			if (!combo.isShowing())
			{
				combo.show();
			}
		});
	}
}
