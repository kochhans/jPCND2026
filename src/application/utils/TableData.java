package application.utils;

import java.util.Collection;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TableView;
// Hilfsklasse für die TableViews -- Data - Filter -- Sortierung
public class TableData<T>
{
    public ObservableList<T> master = FXCollections.observableArrayList();
    public FilteredList<T> filtered = new FilteredList<>(master, p -> true);
    public SortedList<T> sorted = new SortedList<>(filtered);

    public void bind(TableView<T> table)
    {
        sorted.comparatorProperty().bind(table.comparatorProperty());
        table.setItems(sorted);
    }

    // 🔥 NEU
    public void setAll(Collection<T> data)
    {
        master.setAll(data);
    }

    // 🔥 optional (wirst du brauchen)
    public void clear()
    {
        master.clear();
    }

    public int size()
    {
        return master.size();
    }
}