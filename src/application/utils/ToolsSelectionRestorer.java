package application.utils;

import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;

public class ToolsSelectionRestorer {

    /**
     * Stellt die Selektion in einem TableView wieder her, sobald dessen Items geladen sind.
     *
     * @param table Die TableView, in der eine Zeile selektiert werden soll
     * @param index Der Index der Zeile, die wieder selektiert werden soll
     */
    public static void restoreSelectionWhenReady(TableView<?> table, int index) {
        if (table == null || index < 0) return;

        ObservableList<?> items = table.getItems();

        if (index < items.size()) {
            // Sofort selektieren, falls schon möglich
            select(table, index);
            return;
        }

        ListChangeListener<Object> listener = new ListChangeListener<>() {
            @Override
            public void onChanged(Change<?> c) {
                if (index >= 0 && index < items.size()) {
                    select(table, index);
                    items.removeListener(this); // Nur einmal ausführen
                }
            }
        };

        items.addListener(listener);
    }

    private static void select(TableView<?> table, int index) {
        table.getSelectionModel().select(index);
        table.getFocusModel().focus(index);
        //table.scrollTo(index);
        System.out.println("✅ Zeile selektiert (Index " + index + ") in " + table.getId());
    }
}
