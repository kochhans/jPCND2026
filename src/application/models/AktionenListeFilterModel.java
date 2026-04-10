package application.models;

import java.time.LocalDate;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class AktionenListeFilterModel implements IDOwner
{
	private LocalDate datumvon;
	private LocalDate datumbis;
	private String treffpunkt;
	private String akktyp;
	private String gruppe;
	private String ort;
	private String art;

	// Konstruktor
	public AktionenListeFilterModel()
	{
	}

	// Getter und Setter
	public LocalDate getDatumVon()
	{
		return datumvon;
	}

	public void setDatumVon(LocalDate datumvon)
	{
		this.datumvon = datumvon;
	}

	public LocalDate getDatumBis()
	{
		return datumbis;
	}

	public void setDatumBis(LocalDate datumbis)
	{
		this.datumbis = datumbis;
	}

	public String getTreffpunkt()
	{
		return treffpunkt;
	}

	public void setTreffpunkt(String treffpunkt)
	{
		this.treffpunkt = treffpunkt;
	}

	public String getAkktyp()
	{
		return akktyp;
	}

	public void setAkktyp(String akktyp)
	{
		this.akktyp = akktyp;
	}

	public String getGruppe()
	{
		return gruppe;
	}

	public void setGruppe(String gruppe)
	{
		this.gruppe = gruppe;
	}

	public String getOrt()
	{
		return ort;
	}

	public void setOrt(String ort)
	{
		this.ort = ort;
	}

	public String getArt()
	{
		return art;
	}

	public void setArt(String art)
	{
		this.art = art;
	}

	@Override
	public Integer getID()
	{
		// TODO Auto-generated method stub
		return null;
	}

}
