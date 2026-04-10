package application.models;

public class LiederStueckeBibelModel
{
	private String lsbibez; // z.B. "Joh 3,16"
	private String lsbiId; // z.B. "1"
	private String lsbiLs; // z.B. "Lied 5"

	public LiederStueckeBibelModel(String lsbibez, String lsbiId, String lsbiLs)
	{
		this.lsbibez = lsbibez;
		this.lsbiId = lsbiId;
		this.lsbiLs = lsbiLs;
	}

	public String getLsbibez()
	{
		return lsbibez;
	}

	public void setLsbibez(String lsbibez)
	{
		this.lsbibez = lsbibez;
	}

	public String getLsbiId()
	{
		return lsbiId;
	}

	public void setLsbiId(String lsbiId)
	{
		this.lsbiId = lsbiId;
	}

	public String getLsbiLs()
	{
		return lsbiLs;
	}

	public void setLsbiLs(String lsbiLs)
	{
		this.lsbiLs = lsbiLs;
	}
}
