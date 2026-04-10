package application.utils;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import java.util.List;
import java.util.function.Function;
import java.util.function.ToIntFunction;


/*
 * Wenn die id ein Int ist:
 * TableUtils.selectRowById(tblvwLiteratur, Integer.parseInt(idcheck), LiteraturlisteModel::getId);

Wenn die id ein String ist
 * TableUtils.selectRowById(tblvwLiteratur, idcheck, LiteraturlisteModel::getId);

 * 
 */
public class TableUtils {

    /**
     * Variante 1 – für numerische IDs (int)
     */
    public static <T> void selectRowById(TableView<T> tableView, int idToFind, ToIntFunction<T> idExtractor) {
        Platform.runLater(() -> {
            List<T> items = tableView.getItems();
            if (items == null || items.isEmpty()) return;

            for (T item : items) {
                if (idExtractor.applyAsInt(item) == idToFind) {
                    int index = items.indexOf(item);

                    tableView.getSelectionModel().clearSelection();
                    tableView.getSelectionModel().select(index);
                    tableView.scrollTo(index);
                    tableView.getFocusModel().focus(index);
                    tableView.requestFocus();
                    break;
                }
            }
        });
    }

    /**
     * Variante 2 – für String-IDs (z. B. "LIT_001")
     */
    public static <T> void selectRowById(TableView<T> tableView, String idToFind, Function<T, String> idExtractor) {
        Platform.runLater(() -> {
            List<T> items = tableView.getItems();
            if (items == null || items.isEmpty() || idToFind == null) return;

            for (T item : items) {
                String currentId = idExtractor.apply(item);
                if (idToFind.equals(currentId)) {
                    int index = items.indexOf(item);

                    tableView.getSelectionModel().clearSelection();
                    tableView.getSelectionModel().select(index);
                    tableView.scrollTo(index);
                    tableView.getFocusModel().focus(index);
                    tableView.requestFocus();
                    break;
                }
            }
        });
    }
    
    public static <T> void selectRowById(TableView<T> tableView, int zeile) {
        Platform.runLater(() -> {
            List<T> items = tableView.getItems();
            if (items == null || items.isEmpty()) return;

                    tableView.getSelectionModel().clearSelection();
                    tableView.getSelectionModel().select(zeile);
                    tableView.scrollTo(zeile);
                    tableView.getFocusModel().focus(zeile);
                    tableView.requestFocus();


        });
    }
  
    
    
}

