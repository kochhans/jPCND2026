package application.models;

//Für eine Combobox mit Editionlang und ID
public class EditionenlisteComboNaModel
{
	public EditionenlisteComboNaModel(String lt)
	{
		super();
		this.lt=lt;

	}
	String lt;

	public String getLt()
	{
		return lt;
	}
	public void setLt(String lt)
	{
		this.lt = lt;
	}
	//-------------
	
	@Override
	public String toString() {
		return lt;
	}





}
