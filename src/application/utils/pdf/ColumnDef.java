package application.utils.pdf;

import java.util.function.Function;

class ColumnDef<D> {

    final String label;
    final Function<D,Object> extractor;

    ColumnDef(String label, Function<D,Object> extractor) {
        this.label = label;
        this.extractor = extractor;
    }
}
