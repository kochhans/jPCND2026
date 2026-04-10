package application;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConfigLoader.class.getResourceAsStream("/application/config.properties")) {
            if (in != null) {
                props.load(in);
            } else {
                System.err.println("WARNUNG: config.properties nicht gefunden – standardwerte werden verwendet.");
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Laden von config.properties: " + e.getMessage());
        }
    }

    public static String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(props.getProperty(key, String.valueOf(defaultValue)));
        } catch (Exception e) {
            return defaultValue;
        }
    }

	public static boolean getBoolean(String string, boolean b)
	{
		// TODO Auto-generated method stub
		return false;
	}
}
