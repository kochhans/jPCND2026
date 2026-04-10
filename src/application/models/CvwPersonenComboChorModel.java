package application.models;

public class CvwPersonenComboChorModel
{
	private String pechor;

	public CvwPersonenComboChorModel( String pechor)
	{

		this.pechor = pechor;

	}



	public String getPechor()
	{
		return pechor;
	}

	public void setPechor(String pechor)
	{
		this.pechor = pechor;
	}

	@Override
	public String toString() {
	    return pechor; // oder pechor
	}



}
