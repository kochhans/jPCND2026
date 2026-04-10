package application.utils;


public class ToolsBrowser
{
//	private static void openBrowser(String url) {
//	    try {
//	        URI uri = URI.create(url);
//
//	        if (Desktop.isDesktopSupported() &&
//	                Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
//	            Desktop.getDesktop().browse(uri);
//	            return;
//	        }
//
//	        // Fallbacks für OS-basierte Browser-Öffnung
//	        String os = System.getProperty("os.name").toLowerCase();
//
//	        if (os.contains("win")) {
//	            new ProcessBuilder("cmd", "/c", "start", uri.toString()).start();
//	        } else if (os.contains("mac")) {
//	            new ProcessBuilder("open", uri.toString()).start();
//	        } else if (os.contains("nux") || os.contains("nix")) {
//	            new ProcessBuilder("xdg-open", uri.toString()).start();
//	        } else {
//	            throw new RuntimeException("Browser öffnen nicht unterstützt.");
//	        }
//	    }
//	    catch (Exception e) {
//	        e.printStackTrace();
//	        Platform.runLater(() ->
//	            showError(null, "Fehler", "Die Browser-Öffnung ist fehlgeschlagen:\n" + e.getMessage()));
//	    }
//	}

//	private static Object showError(Object object, String string, String string2)
//	{
//		// TODO Auto-generated method stub
//		return null;
//	}

}
