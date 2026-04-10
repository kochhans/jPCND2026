package application.models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class EditionenlisteModel {

    private final IntegerProperty id = new SimpleIntegerProperty();

    private final StringProperty dbkedit = new SimpleStringProperty();
    private final StringProperty edjahr = new SimpleStringProperty();
    private final StringProperty eingabezeitpunkt = new SimpleStringProperty();
    private final StringProperty schwierig = new SimpleStringProperty();
    private final StringProperty lt = new SimpleStringProperty();
    private final StringProperty kt = new SimpleStringProperty();
    private final StringProperty hrsg = new SimpleStringProperty();
    private final StringProperty bestnr = new SimpleStringProperty();
    private final StringProperty edart = new SimpleStringProperty();
    private final StringProperty besch = new SimpleStringProperty();
    private final StringProperty titelgrafikpfad = new SimpleStringProperty();
    private final StringProperty verlag = new SimpleStringProperty();
    private final StringProperty noma = new SimpleStringProperty();
    private final StringProperty erfasst = new SimpleStringProperty();

    public EditionenlisteModel(
            int id,
            String dbkedit, String edjahr, String eingabezeitpunkt,
            String lt, String kt, String hrsg,
            String bestnr, String edart, String besch,
            String titelgrafikpfad, String verlag,
            String noma, String schwierig, String erfasst
    ) {
        this.id.set(id);

        this.dbkedit.set(dbkedit);
        this.edjahr.set(edjahr);
        this.eingabezeitpunkt.set(eingabezeitpunkt);
        this.lt.set(lt);
        this.kt.set(kt);
        this.hrsg.set(hrsg);
        this.bestnr.set(bestnr);
        this.edart.set(edart);
        this.besch.set(besch);
        this.titelgrafikpfad.set(titelgrafikpfad);
        this.verlag.set(verlag);
        this.noma.set(noma);
        this.erfasst.set(erfasst);
        this.schwierig.set(schwierig);
    }

    // ===== JavaFX Properties =====
    public IntegerProperty idProperty() { return id; }

    public StringProperty dbkeditProperty() { return dbkedit; }
    public StringProperty edjahrProperty() { return edjahr; }
    public StringProperty eingabezeitpunktProperty() { return eingabezeitpunkt; }
    public StringProperty ltProperty() { return lt; }
    public StringProperty ktProperty() { return kt; }
    public StringProperty hrsgProperty() { return hrsg; }
    public StringProperty bestnrProperty() { return bestnr; }
    public StringProperty edartProperty() { return edart; }
    public StringProperty beschProperty() { return besch; }
    public StringProperty titelgrafikpfadProperty() { return titelgrafikpfad; }
    public StringProperty verlagProperty() { return verlag; }
    public StringProperty nomaProperty() { return noma; }
    public StringProperty erfasstProperty() { return erfasst; }
    public StringProperty schwierigProperty() { return schwierig; }

    // ===== klassische Getter =====
    public int getId() { return id.get(); }

    public String getDbkedit() { return dbkedit.get(); }
    public String getEdjahr() { return edjahr.get(); }
    public String getEingabezeitpunkt() { return eingabezeitpunkt.get(); }
    public String getLt() { return lt.get(); }
    public String getKt() { return kt.get(); }
    public String getHrsg() { return hrsg.get(); }
    public String getBestnr() { return bestnr.get(); }
    public String getEdart() { return edart.get(); }
    public String getBesch() { return besch.get(); }
    public String getTitelgrafikpfad() { return titelgrafikpfad.get(); }
    public String getVerlag() { return verlag.get(); }
    public String getNoma() { return noma.get(); }
    public String geterfasst() { return erfasst.get(); }
    public String getSchwierig() { return schwierig.get(); }
}
