package application.models;
//St�ckarten komplettModell
public class BibellisteModel implements IDOwner
{
	public BibellisteModel(
			String buch,
			String kuerzel,
			String birang,
			String bidb
			
			)//Reihenfolge ist entscheidend f�r die Darstellung in der Combobox
	{
		super();
		this.buch = buch;
		this.kuerzel=kuerzel;
		this.birang=birang;
		this.bidb = bidb;

	}

	String buch, kuerzel, birang, bidb;


	public String buch()
	{
		return buch;
	}

	public void setBuch(String buch)
	{
		this.buch = buch;
	}

	public String getBuch()
	{
		return buch;
	}
	public String getKuerzel()
	{
		return kuerzel;
	}

	public void setKuerzel(String kuerzel)
	{
		this.kuerzel = kuerzel;
	}

	public String getBirang()
	{
		return birang;
	}

	public void setBirang(String birang)
	{
		this.birang = birang;
	}
	
	
	public String getBidb()
	{
		return bidb;
	}

	public void setBidb(String bidb)
	{
		this.bidb = bidb;
	}
//-------------

	@Override
	public String toString()
	{
		return buch;
	}

@Override
	public Integer getID() 
	{
		return null;
	}





}
