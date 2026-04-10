package application.utils;

import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AktuellesDatum
{

	public String getDateAsStringLang()
	{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date());
	}
	public String getDateAsString()
	{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		return formatter.format(new Date());
	}
	public String getDateKurzAsString()
	{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		return formatter.format(new Date());
	}
	public String getDateTimeAsString()
	{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm");
		return formatter.format(new Date());
	}
	public String getDateTimeAsStringMitSec()
	{
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		return formatter.format(new Date());
	}
	
	
}