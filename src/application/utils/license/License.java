package application.utils.license;


import java.io.Serializable;
import java.time.LocalDate;

	public class License implements Serializable {
	    private static final long serialVersionUID = 1L;

    private String licenseKey;
    private String email;
    private String hardwareId;
    private LocalDate validUntil;
    private LocalDate lastOnlineCheck;
    private long activatedAt;
    private String signature;

    // --- Getter / Setter ---

    public String getLicenseKey() {
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getHardwareId() {
        return hardwareId;
    }

    public void setHardwareId(String hardwareId) {
        this.hardwareId = hardwareId;
    }

    public LocalDate getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDate validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDate getLastOnlineCheck() {
        return lastOnlineCheck;
    }

    public void setLastOnlineCheck(LocalDate lastOnlineCheck) {
        this.lastOnlineCheck = lastOnlineCheck;
    }

    public long getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(long activatedAt) {
        this.activatedAt = activatedAt;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

	public String getCustomerName()
	{
		// TODO Auto-generated method stub
		return null;
	}
}
