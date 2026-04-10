package application.models;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LiteraturlisteModel   {

    // Felder wie bisher
    Integer litmin, litsec;
    Integer id, nummersort, seitesort;
    String literfasst;
    String dbkennung, littitel, litedit, litstueckart, litkomp, litbearb, litseite, litnr, litbesetzung,
           littonart, litgrafik, litwoli, litthema, litnoma, verlag, dichter;

    public LiteraturlisteModel(Integer id, String dbkennung, String littitel, String litedit, String litstueckart,
                               String litkomp, String litbearb, String litseite, String litnr, String litbesetzung,
                               String littonart, String litgrafik, Integer seitesort, Integer nummersort, String litwoli,
                               String litthema, String litnoma, Integer litmin, Integer litsec, String verlag,
                               String dichter, String literfasst) {
        this.id = id;
        this.dbkennung = dbkennung;
        this.littitel = littitel;
        this.litedit = litedit;
        this.litstueckart = litstueckart;
        this.litkomp = litkomp;
        this.litbearb = litbearb;
        this.litseite = litseite;
        this.litnr = litnr;
        this.litbesetzung = litbesetzung;
        this.littonart = littonart;
        this.litgrafik = litgrafik;
        this.seitesort = seitesort;
        this.nummersort = nummersort;
        this.litwoli = litwoli;
        this.litthema = litthema;
        this.litnoma = litnoma;
        this.litmin = litmin;
        this.litsec = litsec;
        this.verlag = verlag;
        this.dichter = dichter;
        this.literfasst = literfasst;
    }

    // Factory-Methode
    public static LiteraturlisteModel fromResultSet(ResultSet rs) throws SQLException {
        return new LiteraturlisteModel(
            rs.getInt("lit_id"),
            rs.getString("lit_db"),
            rs.getString("lit_titel"),
            rs.getString("lit_edit"),
            rs.getString("lit_stueckart"),
            rs.getString("lit_komp"),
            rs.getString("lit_bearb"),
            rs.getString("lit_seite"),
            rs.getString("lit_nr"),
            rs.getString("lit_besetzung"),
            rs.getString("lit_tonart"),
            rs.getString("lit_grafik"),
            rs.getInt("seitesort"),
            rs.getInt("nummersort"),
            rs.getString("lit_woli"),
            rs.getString("lit_thema"),
            rs.getString("lit_noma"),
            rs.getInt("lit_min"),
            rs.getInt("lit_sec"),
            rs.getString("lit_verlag"),
            rs.getString("lit_stckdichter"),
            rs.getString("lit_erfasst")
        );
    }

    // Getter/Setter wie bisher...


	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}


	public String getDichter()
	{
		return dichter;
	}

	public void setDichter(String dichter)
	{
		this.dichter = dichter;
	}

	public String getVerlag()
	{
		return verlag;
	}

	public void setVerlag(String verlag)
	{
		this.verlag = verlag;
	}

	public String getDbkennung()
	{
		return dbkennung;
	}

	public void setDbkennung(String dbkennung)
	{
		this.dbkennung = dbkennung;
	}

	public String getLittitel()
	{
		return littitel;
	}

	public void setLittitel(String littitel)
	{
		this.littitel = littitel;
	}

	public String getLitedit()
	{
		return litedit;
	}

	public void setLitedit(String litedit)
	{
		this.litedit = litedit;
	}

	public String getLitstueckart()
	{
		return litstueckart;
	}

	public void setLitstueckart(String litstueckart)
	{
		this.litstueckart = litstueckart;
	}

	public String getLitkomp()
	{
		return litkomp;
	}

	public void setLitkomp(String litkomp)
	{
		this.litkomp = litkomp;
	}

	public String getLitbearb()
	{
		return litbearb;
	}

	public void setLitbearb(String litbearb)
	{
		this.litbearb = litbearb;
	}

	public String getLitseite()
	{
		if (litseite == "")
		{
			litseite = null;
		}
		return litseite;
	}

	public void setLitseite(String litseite)
	{
		this.litseite = litseite;
	}

	public String getLitnr()
	{
		if (litnr == "")
		{
			litnr = null;
		}

		return litnr;
	}

	public void setLitnr(String litnr)
	{
		this.litnr = litnr;
	}

	public Integer getSeitesort()
	{

		return seitesort;
	}

	public void setSeitesort(Integer seitesort)
	{
		this.seitesort = seitesort;
	}

	public Integer getNummersort()
	{

		return nummersort;

	}

	public void setNummersort(Integer nummersort)
	{
		this.nummersort = nummersort;
	}

	public String getLitbesetzung()
	{
		return litbesetzung;
	}

	public void setLitbesetzung(String litbesetzung)
	{
		this.litbesetzung = litbesetzung;
	}

	public String getLittonart()
	{
		return littonart;
	}

	public void setLittonart(String littonart)
	{
		this.littonart = littonart;
	}

	public String getLitgrafik()
	{
		return litgrafik;
	}

	public void setLitgrafik(String litgrafik)
	{
		this.litgrafik = litgrafik;
	}

	@Override
	public String toString()
	{
		return littitel;
	}

	public String getLitthema()
	{
		return litthema;
	}

	public void setLitthema(String litthema)
	{
		this.litthema = litthema;
	}

	public String getLitwoli()
	{
		return litwoli;
	}

	public void setLitwoli(String litwoli)
	{
		this.litwoli = litwoli;
	}

	public String getLitnoma()
	{
		return litnoma;
	}

	public void setLitnoma(String litnoma)
	{
		this.litnoma = litnoma;
	}

	public Integer getLitsec()
	{
		return litsec;
	}

	public void setLitsec(int litsec)
	{
		this.litsec = litsec;
	}

	public Integer getLitmin()
	{
		
		return litmin;
	}

	public void setLitmin(int litmin)
	{
		this.litmin = litmin;
	}
	
	public String getLiterfasst()
	{
		return literfasst;
	}

	public void setLiterfasst(String literfasst)
	{
		this.literfasst = literfasst;
	}

}
