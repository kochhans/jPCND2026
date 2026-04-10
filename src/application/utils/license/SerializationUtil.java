package application.utils.license;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializationUtil
{
	// Schreiben
	public static void writeLicense(File file, License lic) throws Exception {
	    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
	        oos.writeObject(lic);
	    }
	}

	// Lesen
	public static License readLicense(File file) throws Exception {
	    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
	        return (License) ois.readObject();
	    }
	}


}
