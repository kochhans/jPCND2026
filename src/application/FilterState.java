package application;

import application.ConfigManager;

/**
 * Zentraler Zustand für Start-Filter
 * -> UI-unabhängig
 * -> jederzeit speicherbar
 */
public class FilterState {

    private static FilterState instance;

    public static FilterState get() {
        if (instance == null) {
            instance = new FilterState();
            instance.load();
        }
        return instance;
    }

    // =========================
    // FELDER
    // =========================

    public String titel = "";
    public String stueckart = "";
    public String edition = "";
    public String komponist = "";
    public String dichter = "";
    public String verlag = "";
    public String wochenlied = "";
    public String thema = "";
    public String notenmappe = "";
    public String bibel = "";
    public String gesangbuch = "";

    // =========================
    // LOAD
    // =========================

    public void load() {
        titel = ConfigManager.loadFilterStartTitel();
        stueckart = ConfigManager.loadFilterStartStckart();
        edition = ConfigManager.loadFilterStartEdition();
        komponist = ConfigManager.loadFilterStartKomponist();
        dichter = ConfigManager.loadFilterStartDichter();
        verlag = ConfigManager.loadFilterStartVerlag();
        wochenlied = ConfigManager.loadFilterStartWoli();
        thema = ConfigManager.loadFilterStartThema();
        notenmappe = ConfigManager.loadFilterStartNoma();
        bibel = ConfigManager.loadFilterStartBib();
        gesangbuch = ConfigManager.loadFilterStartGesangbuch();
    }

    // =========================
    // SAVE
    // =========================

    public void save() {
        ConfigManager.saveFilterStartTitel(titel);
        ConfigManager.saveFilterStartStckart(stueckart);
        ConfigManager.saveFilterStartEdition(edition);
        ConfigManager.saveFilterStartKomponist(komponist);
        ConfigManager.saveFilterStartDichter(dichter);
        ConfigManager.saveFilterStartVerlag(verlag);
        ConfigManager.saveFilterStartWoli(wochenlied);
        ConfigManager.saveFilterStartThema(thema);
        ConfigManager.saveFilterStartNoma(notenmappe);
        ConfigManager.saveFilterStartBib(bibel);
        ConfigManager.saveFilterStartGesangbuch(gesangbuch);
    }
}
