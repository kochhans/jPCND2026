package application.utils.license;

public enum LicenseCheckResult {
    VALID,
    NOT_FOUND,
    INVALID,
    WRONG_MACHINE,
    EXPIRED,
    OFFLINE_TOO_LONG,
    SAVE_FAILED,
    SERVER_ERROR,
    EMAIL_MISMATCH,
    WRONG_EMAIL
}
