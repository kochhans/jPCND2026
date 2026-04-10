package application.models;

public class LiederStueckeModel
{
	public LiederStueckeModel(
			int id,
			String dbke, 
			String stcktitel, String stckdicht ,
			String stckthema, String stckwoli, String stckbib, 
			String stckgesangbuch, String stckerfasst
			)
	{
		super();
		this.id=id;
		this.dbke = dbke;
		this.stcktitel = stcktitel;
		this.stckdicht = stckdicht;
		this.stckthema=stckthema;
		this.stckwoli=stckwoli;
		this.stckbib=stckbib;
		this.stckgesangbuch=stckgesangbuch;
		this.stckerfasst=stckerfasst;
	}

	
	String dbke, stcktitel, stckdicht, stckthema, stckwoli, stckbib, stckgesangbuch, stckerfasst ;
	Integer id;

	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}
	
	public String getDbs()
	{
		return dbke;
	}

	public void setDbs(String dbke)
	{
		this.dbke = dbke;
	}


	public String getStcktitel()
	{
		return stcktitel;
	}

	public void setStcktitel(String stcktitel)
	{
		this.stcktitel = stcktitel;
	}

	public String getStckdicht()
	{
		return stckdicht;
	}

	public void setStckdicht(String stckdicht)
	{
		this.stckdicht = stckdicht;
	}

	public String getStckthema()
	{
		return stckthema;
	}

	public void setStckthema(String stckthema)
	{
		this.stckthema = stckthema;
	}

	public String getStckwoli()
	{
		return stckwoli;
	}

	public void setStckwoli(String stckwoli)
	{
		this.stckwoli = stckwoli;
	}

	public String getStckbib()
	{
		return stckbib;
	}

	public void setStckbib(String stckbib)
	{
		this.stckbib = stckbib;
	}


	public String getStckgesangbuch()
	{
		return stckgesangbuch;
	}

	public void setStckgesangbuch(String stckgesangbuch)
	{
		this.stckgesangbuch = stckgesangbuch;
	}

	public String getStckerfasst()
	{
		return stckerfasst;
	}

	public void setStckerfasst(String stckerfasst)
	{
		this.stckerfasst = stckerfasst;
	}


}
