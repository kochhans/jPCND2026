package application.models;
import java.time.LocalDate;


public class AktionenListePositionenAufgefuehrtModel  {

    private int capoId;
    private int capoPos;
    private String capoBem;
    private int capoCaId; // FK zu AktionenListeModel
    private String capoStcktitel;
    private String capoEdition;
    private String capoArt;
    private int capoDauermin;
    private int capoDauersec;
    private String capoSonstiges;
    private String capoNr;
    private String capoSeite;
    private String capoBesetzung;
    private String capoTonart;
    private String capoKomponist;
    private String capoZeilentyp;
    private String capoTitelbild;
    private String capoBearbeiter;
    private int capoLiteratur;
    private int capoZwischentext;
    private int capoLitId;
    //private IntegerProperty capo_pos = new SimpleIntegerProperty();
    private LocalDate cadatum; 
    private String caakttyp;
    private String caaktionsort;
    private String cagruppe;
    
    

    public AktionenListePositionenAufgefuehrtModel(int capoId, int capoPos, String capoBem, int capoCaId,
                                       String capoStcktitel, String capoEdition, String capoArt,
                                       int capoDauermin, int capoDauersec, String capoSonstiges,
                                       String capoNr, String capoSeite, String capoBesetzung, String capoTonart,
                                       String capoKomponist, String capoZeilentyp, String capoTitelbild,
                                       String capoBearbeiter, int capoLiteratur, int capoZwischentext,
                                       int capoLitId, LocalDate cadatum,String caakttyp,
                                       String caaktionsort , String cagruppe) {
        this.capoId = capoId;
        this.capoPos = capoPos;
        this.capoBem = capoBem; //PrgPkt
        this.capoCaId = capoCaId;
        this.capoStcktitel = capoStcktitel;
        this.capoEdition = capoEdition;
        this.capoArt = capoArt;
        this.capoDauermin = capoDauermin;
        this.capoDauersec = capoDauersec;
        this.capoSonstiges = capoSonstiges;
        this.capoNr = capoNr;
        this.capoSeite = capoSeite;
        this.capoBesetzung = capoBesetzung;
        this.capoTonart = capoTonart;
        this.capoKomponist = capoKomponist;
        this.capoZeilentyp = capoZeilentyp;
        this.capoTitelbild = capoTitelbild;
        this.capoBearbeiter = capoBearbeiter;
        this.capoLiteratur = capoLiteratur;
        this.capoZwischentext = capoZwischentext;
        this.capoLitId = capoLitId;
        this.cadatum = cadatum;        
        this.caakttyp = caakttyp;   
        this.caaktionsort = caaktionsort;
        this.cagruppe = cagruppe;
        
        
    }

    public int getCapoZwischentext()
	{
		return capoZwischentext;
	}

	public void setCapoZwischentext(int capoZwischentext)
	{
		this.capoZwischentext = capoZwischentext;
	}

	// ---------------- Getter ----------------
    public int getCapoId() { return capoId; }
    public int getCapoPos() { return capoPos; } 
    public String getCapoBem() { return capoBem; }
    public int getCapoCaId() { return capoCaId; }
    public String getCapoStcktitel() { return capoStcktitel; }
    public String getCapoEdition() { return capoEdition; }
    public String getCapoArt() { return capoArt; }
    public int getCapoDauermin() { return capoDauermin; }
    public int getCapoDauersec() { return capoDauersec; }
    public String getCapoSonstiges() { return capoSonstiges; }
    public String getCapoNr() { return capoNr; }
    public String getCapoSeite() { return capoSeite; }
    public String getCapoBesetzung() { return capoBesetzung; }
    public String getCapoTonart() { return capoTonart; }
    public String getCapoKomponist() { return capoKomponist; }
    public String getCapoZeilentyp() { return capoZeilentyp; }
    public String getCapoTitelbild() { return capoTitelbild; }
    public String getCapoBearbeiter() { return capoBearbeiter; }
    public int isCapoLiteratur() { return capoLiteratur; }
    public int isCapoZwischentext() { return capoZwischentext; }
    public int getCapoLitId() { return capoLitId; }


    public LocalDate getCadatum() { return cadatum; }
    public void setCadatum(LocalDate cadatum) { this.cadatum = cadatum; }
    public String getCaakttyp() { return caakttyp; }
    public void setCaakttyp(String caakttyp) { this.caakttyp = caakttyp; }
    public String getCaaktionsort() { return caaktionsort; }
    public void setCaaktionsort(String caaktionsort) { this.caaktionsort = caaktionsort; }
    public String getCagruppe() { return cagruppe; }
    public void setCagruppe(String cagruppe) { this.cagruppe = cagruppe; }

	public String getLittitel()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitedit()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitkomp()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitbearb()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitseite()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitnr()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitstueckart()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLittonart()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitbesetzung()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getId()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public String getLitgrafik()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getLitmin()
	{
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getLitsec()
	{
		// TODO Auto-generated method stub
		return null;
	}

    

    
}
