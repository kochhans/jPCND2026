package application.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AktionenEinzelnPerId
{

    private int caid;
    private String caakttyp;
    private LocalDate cadatum;
    private String cabeschreibung;
    private LocalTime catreffpunkt;
    private String cabemerkung;
    private String caverantwortlich;
    private String cagruppe;
    private String caaktionsort;


    public AktionenEinzelnPerId(int caid, String caakttyp, LocalDate cadatum, String cabeschreibung,
                              LocalTime catreffpunkt,
                              String cabemerkung, String caverantwortlich, String cagruppe,
                              String caaktionsort) {
        this.caid = caid;
        this.caakttyp = caakttyp;
        this.cadatum = cadatum;
        this.cabeschreibung = cabeschreibung;
        this.catreffpunkt = catreffpunkt;
 
        this.cabemerkung = cabemerkung;
        this.caverantwortlich = caverantwortlich;
        this.cagruppe = cagruppe;
        this.caaktionsort = caaktionsort;

    }

    // ---------------- Getter & Setter ----------------
    public int getCaid() { return caid; }
    public String getCaakttyp() { return caakttyp; }
    public LocalDate getCadatum() { return cadatum; }
    public String getCabeschreibung() { return cabeschreibung; }
    public LocalTime getCatreffpunkt() { return catreffpunkt; }

    public String getCabemerkung() { return cabemerkung; }
    public String getCaverantwortlich() { return caverantwortlich; }
    public String getCagruppe() { return cagruppe; }
    public String getCaaktionsort() { return caaktionsort; }


}
