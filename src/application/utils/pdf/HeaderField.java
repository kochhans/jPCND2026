package application.utils.pdf;

import java.util.function.Function;

class HeaderField<M> {

    final String label;
    final Function<M,Object> extractor;

    HeaderField(String label, Function<M,Object> extractor) {
        this.label = label;
        this.extractor = extractor;
    }
}