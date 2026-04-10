package application.utils;

import javafx.scene.image.Image;

public final class IconUtil {

    private IconUtil() {}

    public static Image load(String name) {
        return new Image(
            IconUtil.class.getResourceAsStream(
                "/icons/javafx/" + name + ".png"
            )
        );
    }
}
