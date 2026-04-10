package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class NotenmappeInhaltModel implements IDOwner
{
	public NotenmappeInhaltModel(Integer id, String edit, String nomabez, 
			String nomabem, String nomalag1, String nomalag2, String nomalag3, String titelgrafik, Integer nomaanzahl )
	{
			super();
			this.id = id;
			this.edit = edit;
			this.nomabez = nomabez;
			this.nomabem=nomabem;
			this.nomalag1=nomalag1;
			this.nomalag2=nomalag2;
			this.nomalag3=nomalag3;
			this.titelgrafik=titelgrafik;
			this.nomaanzahl=nomaanzahl;		

		}
	Integer id, nomaanzahl;
	String bez, edit,nomabez, nomabem,nomalag1, nomalag2,nomalag3 , titelgrafik;
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public Integer getNomaanzahl()
	{
		return nomaanzahl;
	}
	public void setNomaanzahl(Integer nomaanzahl)
	{
		this.nomaanzahl = nomaanzahl;
	}
	public String getBez()
	{
		return bez;
	}
	public void setBez(String bez)
	{
		this.bez = bez;
	}
	public String getEdit()
	{
		return edit;
	}
	public void setEdit(String edit)
	{
		this.edit = edit;
	}
	public String getNomabez()
	{
		return nomabez;
	}
	public void setNomabez(String nomabez)
	{
		this.nomabez = nomabez;
	}
	public String getNomabem()
	{
		return nomabem;
	}
	public void setNomabem(String nomabem)
	{
		this.nomabem = nomabem;
	}
	public String getNomalag1()
	{
		return nomalag1;
	}
	public void setNomalag1(String nomalag1)
	{
		this.nomalag1 = nomalag1;
	}
	public String getNomalag2()
	{
		return nomalag2;
	}
	public void setNomalag2(String nomalag2)
	{
		this.nomalag2 = nomalag2;
	}
	public String getNomalag3()
	{
		return nomalag3;
	}
	public void setNomalag3(String nomalag3)
	{
		this.nomalag3 = nomalag3;
	}


	public String getTitelgrafik()
	{
		return titelgrafik;
	}
	public void setTitelgrafik(String titelgrafik)
	{
		this.titelgrafik = titelgrafik;
	}
	@Override
	public Integer getID()
	{
		
		return null;


	
	}
}
