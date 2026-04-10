package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class VerlaglisteModel 
{
	public VerlaglisteModel(String vverlag,  String vort, String vbem, String verfasst, String vdb, int vid)
	{
			super();
			this.vort = vort;	
			this.vverlag = vverlag;
			this.vbem = vbem;
			this.vdb = vdb;
			this.verfasst=verfasst;
			this.vid = vid;

		}
	String vverlag, vort,vbem , verfasst , vdb;
	Integer vid;
	
	
	public Integer getVid() {
	    return vid;
	}

	public void setVid(Integer vid) {
	    this.vid = vid;
	}

	
	public String getVort() {
		return vort;
	}
	public void setVort(String vort) {
		this.vort = vort;
	}
	public String getVbem() {
		return vbem;
	}
	public void setVbem(String vbem) {
		this.vbem = vbem;
	}
	public String getVerfasst() {
		return verfasst;
	}
	public void setVerfasst(String verfasst) {
		this.verfasst = verfasst;
	}
	public String getVverlag()
	{
		return vverlag;
	}
	public void setVverlag(String vverlag)
	{
		this.vverlag = vverlag;
	}
	public String getVweb()
	{
		return vort;
	}
	public void setVweb(String vort)
	{
		this.vort = vort;
	}
	
	public String getVdb()
	{
		return vdb;
	}
	public void setVdb(String vdb)
	{
		this.vdb = vdb;
	}
	
	@Override
	public String toString() {
		return vverlag;
	}



}
