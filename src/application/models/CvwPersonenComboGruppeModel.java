package application.models;

public class CvwPersonenComboGruppeModel
{
	private String pegruppe;

	public CvwPersonenComboGruppeModel( String pegruppe)
	{

		this.pegruppe = pegruppe;

	}



	public String getPegruppe()
	{
		return pegruppe;
	}

	public void setPechor(String pegruppe)
	{
		this.pegruppe = pegruppe;
	}

	@Override
	public String toString() {
	    return pegruppe; // oder pechor
	}



}
