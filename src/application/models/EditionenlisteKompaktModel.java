package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class EditionenlisteKompaktModel
{
	public EditionenlisteKompaktModel(String dbkedit, String lt,String titelgrafikpfad, String verlag)
	{
		super();
		this.dbkedit = dbkedit;
		this.lt = lt;
		this.tpfad = titelgrafikpfad;
		this.verlag = verlag;

	}
	String dbkedit;
	String lt, tpfad, verlag;

	public String getDbkedit()
	{
		return dbkedit;
	}
	public void setDbkedit(String dbkedit)
	{
		this.dbkedit = dbkedit;
	}
	public String getLt()
	{
		return lt;
	}
	public void setLt(String lt)
	{
		this.lt = lt;
	}
	public String getTpfad()
	{
		return tpfad;
	}
	public void setTpfad(String tpfad)
	{
		this.tpfad = tpfad;
	}
	public String getVerlag()
	{
		return verlag;
	}
	public void setVerlag(String verlag)
	{
		this.verlag = verlag;
	}
}
	


