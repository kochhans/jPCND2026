package application.models;

public class AktionenListePersonenModel
{
	private int capeid;
	private int capecaid; // FK zu AktionenListeModel
	private String capename;
	private String capevname;
	private String capestimme;
	private String capeinstrument;

	public AktionenListePersonenModel(int capekeyid, int capecaid, String capename, String capevname,
			String capestimme, String capeinstrument)
	{
		this.capeid = capekeyid;
		this.capecaid = capecaid;
		this.capename = capename;
		this.capevname = capevname;
		this.capestimme = capestimme;
		this.capeinstrument = capeinstrument;
	}

	public int getCapeid()
	{
		return capeid;
	}

	public void setCapeid(int capeid)
	{
		this.capeid = capeid;
	}

	public int getCapecaid()
	{
		return capecaid;
	}

	public void setCapecaid(int capecaid)
	{
		this.capecaid = capecaid;
	}

	public String getCapename()
	{
		return capename;
	}

	public void setCapename(String capename)
	{
		this.capename = capename;
	}

	public String getCapevname()
	{
		return capevname;
	}

	public void setCapevname(String capevname)
	{
		this.capevname = capevname;
	}

	public String getCapestimme()
	{
		return capestimme;
	}

	public void setCapestimme(String capestimme)
	{
		this.capestimme = capestimme;
	}

	public String getCapeinstrument()
	{
		return capeinstrument;
	}

	public void setCapeinstrument(String capeinstrument)
	{
		this.capeinstrument = capeinstrument;
	}



}
