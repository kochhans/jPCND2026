package application.models;

//F�r eine Combobox mit Editionlang und ID
public class EditionenlisteComboModel
{
	public EditionenlisteComboModel(String edart)
	{
		super();
		this.edart=edart;

	}
	String edart;

	public String getEdart()
	{
		return edart;
	}
	public void setEdart(String edart)
	{
		this.edart = edart;
	}
	//-------------
	
	@Override
	public String toString() {
		return edart;
	}





}
