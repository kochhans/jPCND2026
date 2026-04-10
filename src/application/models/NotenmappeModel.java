package application.models;

//Klasse, um die Liste zur Ausgabe zusammenzustellen
public class NotenmappeModel implements IDOwner
{
	public NotenmappeModel(Integer id, String bez, String bem)
	{
			super();
			this.id = id;
			this.bez = bez;
			this.bem = bem;

		}
	Integer id;
	String bez, bem ;
	public Integer getId()
	{
		return id;
	}
	public void setId(Integer id)
	{
		this.id = id;
	}
	public String getBez()
	{
		return bez;
	}
	public void setBez(String bez)
	{
		this.bez = bez;
	}
	public String getBem()
	{
		return bem;
	}
	public void setBem(String bem)
	{
		this.bem = bem;
	}
	@Override
	public Integer getID()
	{
		
		return null;
	}
	@Override
	public String toString() {
		return bez;
	}





	

}
