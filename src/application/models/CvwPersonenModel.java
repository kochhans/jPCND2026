package application.models;

public class CvwPersonenModel
{
	private int pekeyid; //PK
	private int peid; // Id aus CVW
	private String pename;
	private String pevname;
	private String peinstrument;
	private String pechor;
	private String pestimme;
	private String pegruppe;
	private String petelefon;
	private String pemail;

	public CvwPersonenModel(int pekeyid, int peid, String pename, String pevname,
			String peinstrument, String pechor, String pestimme, String pegruppe, String petelefon, String pemail)
	{
		this.pekeyid = pekeyid;
		this.peid = peid;
		this.pename = pename;
		this.pevname = pevname;
		this.peinstrument = peinstrument;
		this.pechor = pechor;
		this.pestimme = pestimme;
		this.pegruppe = pegruppe;
		this.petelefon = petelefon;
		this.pemail = pemail;
	}

	public int getPekeyid()
	{
		return pekeyid;
	}

	public void setPekeyid(int pekeyid)
	{
		this.pekeyid = pekeyid;
	}

	public int getPeid()
	{
		return peid;
	}

	public void setPeid(int peid)
	{
		this.peid = peid;
	}

	public String getPename()
	{
		return pename;
	}

	public void setPename(String pename)
	{
		this.pename = pename;
	}

	public String getPevname()
	{
		return pevname;
	}

	public void setPevname(String pevname)
	{
		this.pevname = pevname;
	}

	public String getPeinstrument()
	{
		return peinstrument;
	}

	public void setPeinstrument(String peinstrument)
	{
		this.peinstrument = peinstrument;
	}

	public String getPechor()
	{
		return pechor;
	}

	public void setPechor(String pechor)
	{
		this.pechor = pechor;
	}

	public String getPestimme()
	{
		return pestimme;
	}

	public void setPestimme(String pestimme)
	{
		this.pestimme = pestimme;
	}
	public String getPegruppe()
	{
		return pegruppe;
	}

	public void setPegruppe(String pegruppe)
	{
		this.pegruppe = pegruppe;
	}


	public String getPetelefon()
	{
		return petelefon;
	}

	public void setPetelefon(String petelefon)
	{
		this.petelefon = petelefon;
	}
	
	public String getPemail()
	{
		return pemail;
	}

	public void setPemail(String pemail)
	{
		this.pemail = pemail;
	}
	



}
