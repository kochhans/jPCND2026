package application;

import java.io.IOException;

public class RestartUtil {

    public static void restartApplication() throws IOException {
        String javaBin = System.getProperty("java.home") + "/bin/java";
        String classPath = System.getProperty("java.class.path");
        String className = System.getProperty("sun.java.command");

        ProcessBuilder builder = new ProcessBuilder(
                javaBin, "-cp", classPath, className
        );
        builder.start();
        System.exit(0);
    }
}
