package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class EditionsartenModel 
{
	public EditionsartenModel(String eabez,  String eadb,  int eaid)
	{
			super();
			this.eabez = eabez;	
			this.eadb = eadb;
			this.eaid = eaid;

		}
	String eabez, eadb;
	Integer eaid;
	
	
	public Integer getEaid() {
	    return eaid;
	}

	public void setEaid(Integer eaid) {
	    this.eaid = eaid;
	}

	
	public String getEabez() {
		return eabez;
	}
	public void setEabez(String eabez) {
		this.eabez = eabez;
	}
	public String getEadb() {
		return eadb;
	}
	public void setEadb(String eadb) {
		this.eadb = eadb;
	}
	




}
