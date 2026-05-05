package application.utils.license;

import java.io.File;

import java.time.temporal.ChronoUnit;
import java.util.Objects;

import application.ConfigManager;
import application.ValuesGlobals;
import application.utils.HardwareUtil;

import java.time.LocalDate;

public class LicenseManager
{

	private static LicenseManager instance;

	private LicenseManager()
	{
		// privater Konstruktor → keine direkte Instanzierung
	}

	public static LicenseManager getInstance()
	{
		if (instance == null)
		{
			instance = new LicenseManager();
		}
		return instance;
	}

	// ---------------------------
	// Lizenzprüfung lokal
	// ---------------------------
	public LicenseCheckResult checkLicense()
	{
		try
		{
			File licenseFile = getLicenseFile();
			if (!licenseFile.exists())
			{
				return LicenseCheckResult.INVALID;
			}

			License lic = loadLicense();
			if (lic == null)
			{
				return LicenseCheckResult.INVALID;
			}

			// 1️⃣ Hardwarebindung prüfen
			String currentHw = HardwareUtil.getHardwareHash();
			if (!currentHw.equals(lic.getHardwareId()))
			{
				return LicenseCheckResult.WRONG_MACHINE;
			}

			// 2️⃣ Ablaufdatum prüfen
			if (lic.getValidUntil() != null &&
					LocalDate.now().isAfter(lic.getValidUntil()))
			{
				return LicenseCheckResult.INVALID;
			}

			// 3️⃣ Prüfen ob Online-Check notwendig
			if (needsOnlineCheck(lic))
			{
				LicenseCheckResult onlineResult = checkOnline(lic);

				if (onlineResult == LicenseCheckResult.VALID)
				{
					lic.setLastOnlineCheck(LocalDate.now());
					saveLicense(lic);
					return LicenseCheckResult.VALID;
				}

				// Online nicht erreichbar → Grace-Period
				if (isWithinGracePeriod(lic))
				{
					return LicenseCheckResult.VALID;
				}

				return onlineResult;
			}

			// Alles OK, keine Onlineprüfung nötig
			return LicenseCheckResult.VALID;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return LicenseCheckResult.SERVER_ERROR;
		}
	}

	private boolean isWithinGracePeriod(License lic)
	{
		if (lic.getLastOnlineCheck() == null)
		{
			return false;
		}

		LocalDate last = lic.getLastOnlineCheck();
		LocalDate limit = last.plusDays(ValuesGlobals.OFFLINE_GRACE_DAYS);

		return !LocalDate.now().isAfter(limit);
	}

	private boolean needsOnlineCheck(License lic)
	{
		if (lic.getLastOnlineCheck() == null)
		{
			return true;
		}

		return lic.getLastOnlineCheck()
				.plusDays(ValuesGlobals.OFFLINE_GRACE_DAYS)
				.isBefore(LocalDate.now());
	}

	private LicenseCheckResult checkOnline(License lic)
	{
		try
		{
			OnlineLicenseResult result = OnlineLicenseService.checkLicense(
					lic.getLicenseKey(),
					lic.getEmail(),
					lic.getHardwareId());

			return result.toCheckResult();

		}
		catch (Exception e)
		{
			return LicenseCheckResult.SERVER_ERROR;
		}
	}

//	public LicenseCheckResult activate2(String key, String email)
//	{
//		try
//		{
//			OnlineLicenseResult result = OnlineLicenseService.activateLicense(
//					key,
//					email,
//					HardwareUtil.getHardwareHash());
//
//			if (result.getStatus() != OnlineLicenseResult.Status.OK)
//			{
//				return result.toCheckResult();
//			}
//
//			License lic = result.toLicense();
//			saveLicense(lic);
//
//			return LicenseCheckResult.VALID;
//
//		}
//		catch (Exception e)
//		{
//			return LicenseCheckResult.SERVER_ERROR;
//		}
//	}

	public LicenseCheckResult activate(String key, String email)
	{
		try
		{
			OnlineLicenseResult result = OnlineLicenseService.activateLicense(
					key,
					email,
					HardwareUtil.getHardwareHash());



			if (result.getStatus() != OnlineLicenseResult.Status.OK)
			{
				return result.toCheckResult();
			}

			// 🔒 Lizenz IMMER komplett hier aufbauen
			License lic = new License();
			lic.setLicenseKey(key);
			lic.setEmail(
				    (result.getEmail() != null && !result.getEmail().isBlank())
				        ? result.getEmail()
				        : email
				);

			lic.setHardwareId(HardwareUtil.getHardwareHash());
			lic.setActivatedAt(System.currentTimeMillis());
			lic.setLastOnlineCheck(LocalDate.now());
			lic.setValidUntil(result.getValidUntil());

			saveLicense(lic);
			return LicenseCheckResult.VALID;

		}
		catch (Exception e)
		{
			e.printStackTrace();
			return LicenseCheckResult.SERVER_ERROR;
		}
	}

	// ---------------------------
	// Pfad zur Lizenzdatei
	// ---------------------------
	public File getLicenseFile()
	{
		return new File(ConfigManager.getConfigDirectory(), "license.key");
	}

	// ---------------------------
	// Lizenz aus Datei laden
	// ---------------------------
	private License loadLicense()
	{
		try
		{
			return SerializationUtil.readLicense(getLicenseFile()); // z.B. mit ObjectInputStream
		}
		catch (Exception e)
		{
			return null;
		}
	}

	// ---------------------------
	// Lizenz speichern
	// ---------------------------
	public void saveLicense(License lic) {
		System.out.println("DEBUG saveLicense:");
		System.out.println("  lic        = " + lic);
		System.out.println("  email      = " + lic.getEmail());
		System.out.println("  licenseKey = " + lic.getLicenseKey());

	    Objects.requireNonNull(lic.getEmail(), "License email missing");
	    Objects.requireNonNull(lic.getLicenseKey(), "License key missing");
	    Objects.requireNonNull(lic.getHardwareId(), "Hardware ID missing");

	    File licenseFile = getLicenseFile();
	    File dir = licenseFile.getParentFile();

	    if (!dir.exists() && !dir.mkdirs()) {
	        throw new RuntimeException(
	            "Konnte Lizenzverzeichnis nicht erstellen: " + dir
	        );
	    }

	    try
		{
			SerializationUtil.writeLicense(licenseFile, lic);
		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    System.out.println(
	        "Lizenz gespeichert unter: " + licenseFile.getAbsolutePath()
	    );
	}



	// ---------------------------
	// Lizenzinfos holen
	// ---------------------------

	public License getCurrentLicense()
	{
		return loadLicense();
	}

	public String getLicenseStatusText()
	{
		License lic = loadLicense();
		if (lic == null)
		{
			return "Keine Lizenz vorhanden";
		}

		// Hardware
		if (!HardwareUtil.getHardwareHash().equals(lic.getHardwareId()))
		{
			return "Lizenz gehört zu einem anderen Gerät";
		}

		// Ablaufdatum
		if (lic.getValidUntil() != null &&
				LocalDate.now().isAfter(lic.getValidUntil()))
		{
			return "Lizenz abgelaufen";
		}

		// Grace-Period
		if (needsOnlineCheck(lic))
		{
			if (isWithinGracePeriod(lic))
			{
				return "Offline-Nutzung (Grace-Period)";
			}
			else
			{
				return "Online-Prüfung erforderlich";
			}
		}

		return "Lizenz gültig";
	}

	// ---------------------------
	// Lizenz online erneuern
	// ---------------------------

	private long getRemainingDays(License lic)
	{
		if (lic.getValidUntil() == null)
		{
			return Long.MAX_VALUE;
		}

		return ChronoUnit.DAYS.between(
				LocalDate.now(),
				lic.getValidUntil());
	}

	private boolean needsAutoRenew(License lic)
	{
		long remainingDays = getRemainingDays(lic);
		return remainingDays >= 0 && remainingDays < ValuesGlobals.AUTO_RENEW_DAYS;
	}

	@SuppressWarnings("unused")
	private LicenseCheckResult autoRenewIfNeeded(License lic)
	{

		if (!needsAutoRenew(lic))
		{
			return LicenseCheckResult.VALID;
		}

		try
		{
			OnlineLicenseResult result = OnlineLicenseService.checkLicense(
					lic.getLicenseKey(),
					lic.getEmail(),
					lic.getHardwareId());

			if (result.toCheckResult() == LicenseCheckResult.VALID)
			{
				// 🔁 neue Daten übernehmen
				lic.setValidUntil(result.getValidUntil());
				lic.setLastOnlineCheck(LocalDate.now());
				saveLicense(lic);
				return LicenseCheckResult.VALID;
			}

			return result.toCheckResult();

		}
		catch (Exception e)
		{
			// Offline → Grace-Period greift später
			return LicenseCheckResult.SERVER_ERROR;
		}
	}

	public LicenseCheckResult renewLicenseOnline()
	{
		License lic = loadLicense();
		if (lic == null)
		{
			return LicenseCheckResult.INVALID;
		}

		try
		{
			OnlineLicenseResult result = OnlineLicenseService.checkLicense(
					lic.getLicenseKey(),
					lic.getEmail(),
					lic.getHardwareId());

			if (result.toCheckResult() == LicenseCheckResult.VALID)
			{
				lic.setLastOnlineCheck(LocalDate.now());
				saveLicense(lic);
			}

			return result.toCheckResult();

		}
		catch (Exception e)
		{
			return LicenseCheckResult.SERVER_ERROR;
		}
	}

}

//public class LicenseManager
//{
//	ALTE PFADE!!!!

//
//	private static LicenseManager instance;
//
//	public static LicenseManager getInstance()
//	{
//		if (instance == null)
//			instance = new LicenseManager();
//		return instance;
//	}
//
//	private LicenseManager()
//	{
//		
//	}
//
//	private static final String LICENSE_FILENAME = "license.key";
//
//	public File getLicenseFile()
//	{
//		return new File(application.utils.ConfigManager.getConfigDirectory(), LICENSE_FILENAME);
//	}
//
//	public void saveLicense(License license) throws IOException
//	{
//		File file = getLicenseFile();
//		if (file.getParentFile() != null)
//			file.getParentFile().mkdirs();
//
//		Properties props = new Properties();
//		props.setProperty("licenseKey", license.getLicenseKey());
//		props.setProperty("email", license.getEmail());
//		props.setProperty("hardwareId", license.getHardwareId());
//		props.setProperty("activatedAt", String.valueOf(license.getActivatedAt()));
//		props.setProperty("lastOnlineCheck", String.valueOf(System.currentTimeMillis()));
//
//		try (FileOutputStream fos = new FileOutputStream(file))
//		{
//			props.store(fos, "Lokale Lizenzdatei");
//		}
//	}
//
//	public LicenseCheckResult activate(String key, String email)
//	{
//		try
//		{
//			OnlineLicenseResult result = OnlineLicenseService.validate(key, email, HardwareUtil.getHardwareHash());
//
//			if (!result.isValid())
//			{
//				switch (result.getResultType())
//				{
//				case INVALID -> {
//					return LicenseCheckResult.INVALID;
//				}
//				case WRONG_MACHINE -> {
//					return LicenseCheckResult.WRONG_MACHINE;
//				}
//				default -> {
//					return LicenseCheckResult.SERVER_ERROR;
//				}
//				}
//			}
//
//			License lic = result.toLicense();
//			saveLicense(lic);
//
//			return LicenseCheckResult.VALID;
//
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//			return LicenseCheckResult.SAVE_FAILED;
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//			return LicenseCheckResult.SERVER_ERROR;
//		}
//	}
//}
