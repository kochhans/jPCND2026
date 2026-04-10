package application.utils;

public class StringBeschneiden
{
	    public String cutFront(String txt, String teil, int number) {
	        for (int i = 0; i < number; i++) {
	            txt = txt.substring(txt.indexOf(teil) + 1, txt.length());
	        }
	        return txt;
	    }

	    public String cutBack(String txt, String teil, int number) {
	        for (int i = 0; i < number; i++) {
	            txt = txt.substring(0, txt.lastIndexOf(teil));
	        }
	        return txt;
	    }
	    public static boolean isNumeric(String str) {
	        return str.matches("\\d+");
	    }
	    public static String extractNumbers(String str) {
	        return str.replaceAll("\\D", ""); // Entfernt alle Nicht-Ziffern
	    }

	} 
