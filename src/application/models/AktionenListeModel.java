package application.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class AktionenListeModel {

    private int caid;
    private String caakttyp;
    private LocalDate cadatum;
    private String cabeschreibung;
    private LocalTime catreffpunkt;
    private LocalTime cabeginn;
    private int caanwesend;
    private String cabemerkung;
    private String caverantwortlich;
    private String cagruppe;
    private String caaktionsort;
    private String caveranstalter;
    private int caauftrittstermin;      // 0=Probe, 1=Auftritt
    private int caprogobererrand;
    private int caproglinkerrand;
    private int caproguntererrand;
    private int caprogkopfabstand;
    private int caprogpositionsabstand;
    private int cagema;                 // 0/1

    // ---------------- Konstruktor ----------------
    public AktionenListeModel(int caid, String caakttyp, LocalDate cadatum, String cabeschreibung,
                              LocalTime catreffpunkt, LocalTime cabeginn, int caanwesend,
                              String cabemerkung, String caverantwortlich, String cagruppe,
                              String caaktionsort, String caveranstalter,
                              int caauftrittstermin,
                              int caprogobererrand, int caproglinkerrand, int caproguntererrand,
                              int caprogkopfabstand, int caprogpositionsabstand,
                              int cagema) {
        this.caid = caid;
        this.caakttyp = caakttyp;
        this.cadatum = cadatum;
        this.cabeschreibung = cabeschreibung;
        this.catreffpunkt = catreffpunkt;
        this.cabeginn = cabeginn;
        this.caanwesend = caanwesend;
        this.cabemerkung = cabemerkung;
        this.caverantwortlich = caverantwortlich;
        this.cagruppe = cagruppe;
        this.caaktionsort = caaktionsort;
        this.caveranstalter = caveranstalter;
        this.caauftrittstermin = caauftrittstermin;
        this.caprogobererrand = caprogobererrand;
        this.caproglinkerrand = caproglinkerrand;
        this.caproguntererrand = caproguntererrand;
        this.caprogkopfabstand = caprogkopfabstand;
        this.caprogpositionsabstand = caprogpositionsabstand;
        this.cagema = cagema;
    }

    // ---------------- Getter & Setter ----------------
    public int getCaid() { return caid; }
    public void setCaid(int caid) { this.caid = caid; }

    public String getCaakttyp() { return caakttyp; }
    public void setCaakttyp(String caakttyp) { this.caakttyp = caakttyp; }

    public LocalDate getCadatum() { return cadatum; }
    public void setCadatum(LocalDate cadatum) { this.cadatum = cadatum; }

    public String getCabeschreibung() { return cabeschreibung; }
    public void setCabeschreibung(String cabeschreibung) { this.cabeschreibung = cabeschreibung; }

    public LocalTime getCatreffpunkt() { return catreffpunkt; }
    public void setCatreffpunkt(LocalTime catreffpunkt) { this.catreffpunkt = catreffpunkt; }

    public LocalTime getCabeginn() { return cabeginn; }
    public void setCabeginn(LocalTime cabeginn) { this.cabeginn = cabeginn; }

    public int getCaanwesend() { return caanwesend; }
    public void setCaanwesend(int caanwesend) { this.caanwesend = caanwesend; }

    public String getCabemerkung() { return cabemerkung; }
    public void setCabemerkung(String cabemerkung) { this.cabemerkung = cabemerkung; }

    public String getCaverantwortlich() { return caverantwortlich; }
    public void setCaverantwortlich(String caverantwortlich) { this.caverantwortlich = caverantwortlich; }

    public String getCagruppe() { return cagruppe; }
    public void setCagruppe(String cagruppe) { this.cagruppe = cagruppe; }

    public String getCaaktionsort() { return caaktionsort; }
    public void setCaaktionsort(String caaktionsort) { this.caaktionsort = caaktionsort; }

    public String getCaveranstalter() { return caveranstalter; }
    public void setCaveranstalter(String caveranstalter) { this.caveranstalter = caveranstalter; }

    public int getCaauftrittstermin() { return caauftrittstermin; }
    public void setCaauftrittstermin(int caauftrittstermin) { this.caauftrittstermin = caauftrittstermin; }

    public int getCaprogobererrand() { return caprogobererrand; }
    public void setCaprogobererrand(int caprogobererrand) { this.caprogobererrand = caprogobererrand; }

    public int getCaproglinkerrand() { return caproglinkerrand; }
    public void setCaproglinkerrand(int caproglinkerrand) { this.caproglinkerrand = caproglinkerrand; }

    public int getCaproguntererrand() { return caproguntererrand; }
    public void setCaproguntererrand(int caproguntererrand) { this.caproguntererrand = caproguntererrand; }

    public int getCaprogkopfabstand() { return caprogkopfabstand; }
    public void setCaprogkopfabstand(int caprogkopfabstand) { this.caprogkopfabstand = caprogkopfabstand; }

    public int getCaprogpositionsabstand() { return caprogpositionsabstand; }
    public void setCaprogpositionsabstand(int caprogpositionsabstand) { this.caprogpositionsabstand = caprogpositionsabstand; }

    public int getCagema() { return cagema; }
    public void setCagema(int cagema) { this.cagema = cagema; }

    // Optional: Convenience-Methoden für UI (Checkbox/Radio)
    public boolean isAuftritt() { return caauftrittstermin == 1; }
    public boolean isProbe() { return caauftrittstermin == 0; }
    public boolean isGema() { return cagema != 0; }
}
