package application.utils.license;

import java.time.LocalDate;
import application.utils.HardwareUtil;

/**
 * Ergebnis einer Online-Lizenzprüfung / -aktivierung
 */
public class OnlineLicenseResult {

    /* ===============================
     * Status vom Server
     * =============================== */
    public enum Status {
        OK,
        INVALID,
        WRONG_MACHINE,
        SERVER_ERROR
    }

    private Status status;
    private String licenseKey;
    private LocalDate validUntil;
    private String email; // vom Server gelieferte E-Mail

    /* ===============================
     * Getter / Setter
     * =============================== */
    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /* ===============================
     * Hilfsmethoden
     * =============================== */
    public boolean isValid() {
        return status == Status.OK;
    }
    public boolean hasServerEmail() {
        return email != null && !email.trim().isEmpty();
    }


    /**
     * Mapping → internes Ergebnis
     */
    public LicenseCheckResult toCheckResult() {
        if (status == null) {
            return LicenseCheckResult.SERVER_ERROR;
        }

        return switch (status) {
            case OK -> LicenseCheckResult.VALID;
            case INVALID -> LicenseCheckResult.INVALID;
            case WRONG_MACHINE -> LicenseCheckResult.WRONG_MACHINE;
            case SERVER_ERROR -> LicenseCheckResult.SERVER_ERROR;

        };
    }

    /**
     * Erzeugt lokale Lizenz aus Serverantwort
     */
    public License toLicense() {
        License lic = new License();
        lic.setLicenseKey(licenseKey);
        lic.setValidUntil(validUntil);
        if (email != null && !email.isBlank()) {
            lic.setEmail(email); // Mail lokal speichern
        }
        lic.setHardwareId(HardwareUtil.getHardwareHash());
        lic.setActivatedAt(System.currentTimeMillis());
        lic.setLastOnlineCheck(LocalDate.now());
        return lic;
    }
}
