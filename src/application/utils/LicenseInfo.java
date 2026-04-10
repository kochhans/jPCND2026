package application.utils;

public class LicenseInfo
{
	public final String email;
	public final String license;
	public final long lastOnlineCheck;
	public final long remainingDays;
	public final String status;

	public LicenseInfo(String email, String license,
			long lastOnlineCheck, long remainingDays, String status)
	{
		this.email = email;
		this.license = license;
		this.lastOnlineCheck = lastOnlineCheck;
		this.remainingDays = remainingDays;
		this.status = status;
	}

}
