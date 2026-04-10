package application.utils.license;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class LicenseFileStore {

    private static final Path LICENSE_PATH =
        Paths.get(System.getenv("APPDATA"), "jPCND", "license.json");

    public static Optional<License> load() {
        if (!Files.exists(LICENSE_PATH)) return Optional.empty();
        // JSON lesen (Jackson / Gson / simple)
		return null;
    }

    public static void save(License license) {
        try
		{
			Files.createDirectories(LICENSE_PATH.getParent());
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        // JSON schreiben
    }

    public static void delete() {
        try
		{
			Files.deleteIfExists(LICENSE_PATH);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
