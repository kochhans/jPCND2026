package application.uicomponents;

import application.models.IDOwner;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.util.StringConverter;

public class HkComboBox<T> extends ComboBox<T>
{
	public HkComboBox(ObservableList<T> items)
	{
		this.setConverter(new StringConverter<T>()
		{
			@Override
			public String toString(T object)
			{
				if(object == null)
					return "nothing selected";
				return object.toString();
			}

			@Override
			public T fromString(String string)
			{
				// return cbxWochenlied.getItems().stream().filter(ap ->
				// ap.toString().equals(string)).findFirst().orElse(null);
				return HkComboBox.this.getValue();
			}
		});
		this.setItems(items);

		this.setValue(this.getItems().get(0));

		this.valueProperty().addListener((obs, oldval, newval) -> {
			if(newval != null)
				System.out.println(((IDOwner) newval).getID());
		});
	}
}
