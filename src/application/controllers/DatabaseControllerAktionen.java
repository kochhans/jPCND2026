package application.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import application.ValuesGlobals;
import application.db.DBManager;
import application.models.AktionenEinzelnPerId;
import application.models.AktionenListeModel;
import application.models.AktionenListePersonenModel;
import application.models.AktionenListePositionenAufgefuehrtModel;
import application.models.AktionenListePositionenModel;
import application.models.CvwPersonenComboChorModel;
import application.models.CvwPersonenComboGruppeModel;
import application.models.CvwPersonenModel;
import application.models.LiteraturlisteModel;

import application.models.NotenmappeModel;
import application.models.StueckartlisteModel;
import application.models.ThemenlisteModel;
import application.models.VerlaglisteModel;
import application.models.WochenliedlisteModel;
import application.uicomponents.Msgbox;


public class DatabaseControllerAktionen
{

	// --------------------------------------------------------------------------------
	// Interne Hilfsfelder
	// --------------------------------------------------------------------------------
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;

	// --------------------------------------------------------------------------------
	// Sicheres Vorbereiten von SQL-Statements
	// --------------------------------------------------------------------------------
	public void setUpStatement(String sql) throws SQLException
	{
		checkConnection();
		if (preparedStatement != null && !preparedStatement.isClosed())
		{
			preparedStatement.close();
		}
		Connection connection = DBManager.getConnection();
		preparedStatement = connection.prepareStatement(sql);
	}

	// --------------------------------------------------------------------------------
	public ResultSet runStatement() throws SQLException
	{
		if (preparedStatement == null)
		{
			throw new SQLException("PreparedStatement ist noch nicht gesetzt!");
		}
		resultSet = preparedStatement.executeQuery();
		return resultSet;
	}

	// --------------------------------------------------------------------------------
	// ResultSet abrufen
	// --------------------------------------------------------------------------------
	public ResultSet getResults() throws SQLException
	{
		try
		{
			if (resultSet == null)
			{
				throw new SQLException("ResultSet ist noch nicht vorhanden! runStatement() vorher aufrufen.");
			}
		}
		catch (SQLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return resultSet;
	}

	// --------------------------------------------------------------------------------
	// Sicherstellen, dass DB-Verbindung existiert
	// --------------------------------------------------------------------------------
	private void checkConnection() throws SQLException
	{
		Connection connection = DBManager.getConnection();
		if (connection == null || connection.isClosed())
		{
			throw new SQLException("Keine gültige Datenbankverbindung!");
		}
	}

//================================================================================= 
//  	Scene Aktionen  -- Startfenster  
//=================================================================================
	// ---------------- Aktionen auslesen ----------------
	public List<AktionenListeModel> getAktionenListeAll() throws SQLException
	{
		String sql = "SELECT * FROM tblChoraktionen ORDER BY ca_datum DESC, ca_treffpunkt DESC";
		List<AktionenListeModel> result = new ArrayList<>();

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				// Datum
				String datumStr = rs.getString("ca_datum");
				LocalDate datum = null;
				if (datumStr != null && !datumStr.isBlank())
				{
					try
					{
						// falls DB-Format yyyy-MM-dd
						datum = LocalDate.parse(datumStr);
					}
					catch (Exception e1)
					{
						try
						{
							// falls DB-Format dd.MM.yyyy
							datum = LocalDate.parse(datumStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
						}
						catch (Exception e2)
						{
							datum = null; // falls alles schief geht
						}
					}
				}

				String ort = rs.getString("ca_aktionsort");
				if (ort != null && ort.isBlank())
					ort = null;

				// Treffpunkt
				String treffpunktStr = rs.getString("ca_treffpunkt");
				LocalTime treffpunkt = (treffpunktStr != null && !treffpunktStr.isBlank())
						? LocalTime.parse(treffpunktStr)
						: null;

				// Beginn
				String beginnStr = rs.getString("ca_beginn");
				LocalTime beginn = (beginnStr != null && !beginnStr.isBlank())
						? LocalTime.parse(beginnStr)
						: null;

				result.add(new AktionenListeModel(
						rs.getInt("ca_id"),
						rs.getString("ca_akttyp"),
						datum,
						rs.getString("ca_beschreibung"),
						treffpunkt, // <-- korrekt LocalTime
						beginn, // <-- korrekt LocalTime
						rs.getInt("ca_anwesend"),
						rs.getString("ca_bemerkung"),
						rs.getString("ca_verantwortlich"),
						rs.getString("ca_gruppe"),
						rs.getString("ca_aktionsort"),
						rs.getString("ca_veranstalter"),
						rs.getInt("ca_auftrittstermin"),
						rs.getInt("ca_prog_oberer_rand"),
						rs.getInt("ca_prog_linker_rand"),
						rs.getInt("ca_prog_unterer_rand"),
						rs.getInt("ca_prog_kopfabstand"),
						rs.getInt("ca_prog_positionsabstand"),
						rs.getInt("ca_gema")));
			}
		}

		return result;
	}

	// ---------------- Aktionen mit Filter ----------------
	public List<AktionenListeModel> getAktionenListeFilter(
			LocalDate filterDatumVon,
			LocalDate filterDatumBis,
			String filterAktion,
			String filterOrt,
			String filterGruppe,
			String filterArt, 
			String filterAktionenBeschreibung) throws SQLException
	{

		List<AktionenListeModel> result = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM tblChoraktionen WHERE 1=1 ");

		if (filterDatumVon != null)
			sql.append("AND ca_datum >= ? ");
		if (filterDatumBis != null)
			sql.append("AND ca_datum <= ? ");
		if (filterAktion != null && !filterAktion.isBlank())
			sql.append("AND ca_akttyp LIKE ? ");
		if (filterOrt != null && !filterOrt.isBlank())
			sql.append("AND ca_aktionsort LIKE ? ");
		if (filterGruppe != null && !filterGruppe.isBlank())
			sql.append("AND ca_gruppe LIKE ? ");
		if (filterArt != null && !filterArt.equalsIgnoreCase("alles"))
			sql.append("AND ca_auftrittstermin = ? ");
		if (filterAktionenBeschreibung != null && !filterAktionenBeschreibung.isBlank())
			sql.append("AND ca_beschreibung LIKE ? ");

		sql.append("ORDER BY ca_datum DESC, ca_treffpunkt DESC");

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql.toString()))
		{

			int index = 1;
			if (filterDatumVon != null)
				pstmt.setString(index++, filterDatumVon.toString());
			if (filterDatumBis != null)
				pstmt.setString(index++, filterDatumBis.toString());
			if (filterAktion != null && !filterAktion.isBlank())
				pstmt.setString(index++, "%" + filterAktion + "%");
			if (filterOrt != null && !filterOrt.isBlank())
				pstmt.setString(index++, "%" + filterOrt + "%");
			if (filterGruppe != null && !filterGruppe.isBlank())
				pstmt.setString(index++, "%" + filterGruppe + "%");
			if (filterArt != null && !filterArt.equalsIgnoreCase("alles"))
				pstmt.setInt(index++, filterArt.equalsIgnoreCase("auff") ? 1 : 0);
			if (filterAktionenBeschreibung != null && !filterAktionenBeschreibung.isBlank())
				pstmt.setString(index++, "%" + filterAktionenBeschreibung + "%");

			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					// Datum
					String datumStr = rs.getString("ca_datum");
					LocalDate datum = (datumStr != null && !datumStr.isBlank())
							? LocalDate.parse(datumStr)
							: null;

					// Treffpunkt
					String treffpunktStr = rs.getString("ca_treffpunkt");
					LocalTime treffpunkt = (treffpunktStr != null && !treffpunktStr.isBlank())
							? LocalTime.parse(treffpunktStr)
							: null;

					// Beginn
					String beginnStr = rs.getString("ca_beginn");
					LocalTime beginn = (beginnStr != null && !beginnStr.isBlank())
							? LocalTime.parse(beginnStr)
							: null;

					result.add(new AktionenListeModel(
							rs.getInt("ca_id"),
							rs.getString("ca_akttyp"),
							datum,
							rs.getString("ca_beschreibung"),
							treffpunkt, // <-- LocalTime korrekt
							beginn, // <-- LocalTime korrekt
							rs.getInt("ca_anwesend"),
							rs.getString("ca_bemerkung"),
							rs.getString("ca_verantwortlich"),
							rs.getString("ca_gruppe"),
							rs.getString("ca_aktionsort"),
							rs.getString("ca_veranstalter"),
							rs.getInt("ca_auftrittstermin"),
							rs.getInt("ca_prog_oberer_rand"),
							rs.getInt("ca_prog_linker_rand"),
							rs.getInt("ca_prog_unterer_rand"),
							rs.getInt("ca_prog_kopfabstand"),
							rs.getInt("ca_prog_positionsabstand"),
							rs.getInt("ca_gema")));
					// System.out.println("DB Wert: " + rs.getInt("ca_auftrittstermin"));
				}

			}
		}

		return result;
	}

	// ---------------- Datensatz speichern / update ----------------
	public int saveAktion(boolean neu,
			String caakttyp, LocalDate cadatum, String cakurzbeschr,
			LocalTime catreffpunkt, LocalTime cabeginn,
			int caanwesend, String cabemerkung, String caverantwortlich,
			String cagruppe, String caort, String caveranstalter,
			int caprobeauftritt, Boolean cagema, int caid) throws SQLException
	{

		String sql;
		if (neu)
		{
			sql = "INSERT INTO tblChoraktionen (ca_akttyp, ca_datum, ca_beschreibung, ca_treffpunkt, "
					+ "ca_beginn, ca_anwesend, ca_bemerkung, ca_verantwortlich, ca_gruppe, "
					+ "ca_aktionsort, ca_veranstalter, ca_auftrittstermin, ca_gema) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		}
		else
		{
			sql = "UPDATE tblChoraktionen SET ca_akttyp=?, ca_datum=?, ca_beschreibung=?, ca_treffpunkt=?, "
					+ "ca_beginn=?, ca_anwesend=?, ca_bemerkung=?, ca_verantwortlich=?, ca_gruppe=?, "
					+ "ca_aktionsort=?, ca_veranstalter=?, ca_auftrittstermin=?, ca_gema=? "
					+ "WHERE ca_id=?";
		}

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
		//neue ID gleich zurückgeben
		{

			int index = 1;
			pstmt.setString(index++, caakttyp);
			pstmt.setString(index++, cadatum != null ? cadatum.toString() : null); // yyyy-MM-dd
			pstmt.setString(index++, cakurzbeschr);
			pstmt.setString(index++, catreffpunkt != null ? catreffpunkt.toString() : null); // HH:mm:ss
			pstmt.setString(index++, cabeginn != null ? cabeginn.toString() : null); // HH:mm:ss
			pstmt.setInt(index++, caanwesend);
			pstmt.setString(index++, cabemerkung);
			pstmt.setString(index++, caverantwortlich);
			pstmt.setString(index++, cagruppe);
			pstmt.setString(index++, caort);
			pstmt.setString(index++, caveranstalter);
			pstmt.setInt(index++, caprobeauftritt);
			pstmt.setBoolean(index++, cagema != null && cagema);

			 if (!neu) {
		            pstmt.setInt(index, caid);
		        }

		        pstmt.executeUpdate();

		        if (neu) {
		            try (ResultSet rs = pstmt.getGeneratedKeys()) {
		                if (rs.next()) {
		                    return rs.getInt(1);
		                }
		            }
		        } else {
		            return caid;
		        }
		    }

		    throw new SQLException("Keine ID erhalten!");
		}

	public void copyPositionen(int alteCaid, int neueCaid) throws SQLException {

	    String sql = """
	        INSERT INTO tblChoraktionenPositionen (
	            capo_pos,
	            capo_bem,
	            capo_ca_id,
	            capo_stcktitel,
	            capo_edition,
	            capo_art,
	            capo_dauermin,
	            capo_dauersec,
	            capo_sonstiges,
	            capo_nr,
	            capo_besetzung,
	            capo_tonart,
	            capo_komponist,
	            capo_bearbeiter,
	            capo_auspcnd,
	            capo_zwischentext,
	            capo_lit_id,
	            capo_zeilentyp,
	            capo_titelbild,
	            capo_seite,
	            user_modified
	        )
	        SELECT
	            capo_pos,
	            capo_bem,
	            ?,
	            capo_stcktitel,
	            capo_edition,
	            capo_art,
	            capo_dauermin,
	            capo_dauersec,
	            capo_sonstiges,
	            capo_nr,
	            capo_besetzung,
	            capo_tonart,
	            capo_komponist,
	            capo_bearbeiter,
	            capo_auspcnd,
	            capo_zwischentext,
	            capo_lit_id,
	            capo_zeilentyp,
	            capo_titelbild,
	            capo_seite,
	            user_modified
	        FROM tblChoraktionenPositionen
	        WHERE capo_ca_id = ?
	    """;

	    try (Connection conn = DBManager.getConnection();
	         PreparedStatement pstmt = conn.prepareStatement(sql)) {

	        pstmt.setInt(1, neueCaid);
	        pstmt.setInt(2, alteCaid);

	        pstmt.executeUpdate();
	    }
	}
	
	// ---------------- Datensatz löschen ----------------
	public void deleteAktion(int caId) throws SQLException
	{
		String sql = "DELETE FROM tblChoraktionen WHERE ca_id=?";
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{

			pstmt.setInt(1, caId);
			pstmt.executeUpdate();
		}
	}

	// IMPORT CSV
	public Set<Integer> getAlleAktionIds() throws SQLException
	{
		Set<Integer> ids = new HashSet<>();
		String sql = "SELECT ca_id FROM tblChoraktionen";

		try (Connection conn = DBManager.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery())
		{

			while (rs.next())
			{
				ids.add(rs.getInt("ca_id"));
			}
		}
		return ids;
	}

	// Aktionen Positionen =====================================================
	// ---------------- Positionen auslesen ----------------
	public List<AktionenListePositionenModel> getAktionenPositionenListeAll(int aktuellAktion) throws SQLException
	{

		List<AktionenListePositionenModel> resultList = new ArrayList<>();
		String sql = "SELECT * FROM tblChoraktionenPositionen WHERE capo_ca_id=? ";
		sql += ("ORDER BY capo_pos ASC ");
		// -----String sql = "SELECT * FROM tblChoraktionenPositionen";
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, aktuellAktion);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					resultList.add(new AktionenListePositionenModel(
							rs.getInt("capo_id"),
							rs.getInt("capo_pos"),
							rs.getString("capo_bem"),
							rs.getInt("capo_ca_id"),
							rs.getString("capo_stcktitel"),
							rs.getString("capo_edition"),
							rs.getString("capo_art"),
							rs.getInt("capo_dauermin"),
							rs.getInt("capo_dauersec"),
							rs.getString("capo_sonstiges"),
							rs.getString("capo_nr"),
							rs.getString("capo_seite"),
							rs.getString("capo_besetzung"),
							rs.getString("capo_tonart"),
							rs.getString("capo_komponist"),
							rs.getString("capo_zeilentyp"),
							rs.getString("capo_titelbild"),
							rs.getString("capo_bearbeiter"),
							rs.getInt("capo_auspcnd"),
							rs.getInt("capo_zwischentext"),
							rs.getInt("capo_lit_id")));
				}
			}

			return resultList;
		}
	}

	// Positionen Aufgeführt =========================
	public List<AktionenListePositionenAufgefuehrtModel> getAktionenPositionenAufgefuehrt(String titel, String edition, String stueckart, int auffproballes) throws SQLException
	{
		LocalDate datum = null;
		String datumStr = "";

		List<AktionenListePositionenAufgefuehrtModel> resultList = new ArrayList<>();
		String sql = "SELECT * FROM tblChoraktionen "
				+ "INNER JOIN tblChoraktionenPositionen ON tblChoraktionen.ca_id = tblChoraktionenPositionen.capo_ca_id "
				+ "WHERE capo_stcktitel LIKE ? "
				+ "AND capo_edition LIKE ? "
				+ "AND capo_art LIKE ? ";
		if (auffproballes == 0)
		{ // Probe
			sql += "AND ca_auftrittstermin = 0 ";

		}
		else if (auffproballes == 1)
		{ // AUff
			sql += "AND ca_auftrittstermin = 1 ";

		}
		else
		{
			sql += " ";
		}

		sql += ("ORDER BY ca_datum DESC, capo_pos ASC ");

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, titel);
			pstmt.setString(2, edition);
			pstmt.setString(3, stueckart);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					datumStr = rs.getString("ca_datum");
					// selbst vorab parsen von SQLite
					if (datumStr != null)
					{
						datum = LocalDate.parse(datumStr); // ISO-Format yyyy-MM-dd
					}

					resultList.add(new AktionenListePositionenAufgefuehrtModel(
							rs.getInt("capo_id"),
							rs.getInt("capo_pos"),
							rs.getString("capo_bem"),
							rs.getInt("capo_ca_id"),
							rs.getString("capo_stcktitel"),
							rs.getString("capo_edition"),
							rs.getString("capo_art"),
							rs.getInt("capo_dauermin"),
							rs.getInt("capo_dauersec"),
							rs.getString("capo_sonstiges"),
							rs.getString("capo_nr"),
							rs.getString("capo_seite"),
							rs.getString("capo_besetzung"),
							rs.getString("capo_tonart"),
							rs.getString("capo_komponist"),
							rs.getString("capo_zeilentyp"),
							rs.getString("capo_titelbild"),
							rs.getString("capo_bearbeiter"),
							rs.getInt("capo_auspcnd"),
							rs.getInt("capo_zwischentext"),
							rs.getInt("capo_lit_id"),
							datum,
							rs.getString("ca_akttyp"),
							rs.getString("ca_aktionsort"),
							rs.getString("ca_gruppe")));
					// System.out.println(conn.getMetaData().getURL());
					// System.out.println("DB NR=" + rs.getString("capo_nr")+ " SEITE=" +
					// rs.getString("capo_seite"));
// DEBUGGING:
//					System.out.println(
//							"ID=" + rs.getInt("capo_id") +
//									"POS=" + rs.getString("capo_pos") +
//									" CA_ID=" + rs.getInt("capo_ca_id") +
//									" NR=" + rs.getString("capo_nr") +
//									" SEITE=" + rs.getString("capo_seite"));
				}
			}

			return resultList;
		}
	}

	// alle Choraktionen-Positionen ids holen
	public Set<Integer> getAlleCapoIds() throws SQLException
	{
		Set<Integer> ids = new HashSet<>();
		String sql = "SELECT capo_id FROM tblChoraktionenPositionen";
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{
			while (rs.next())
			{
				ids.add(rs.getInt("capo_id"));
			}
		}
		return ids;
	}

	// -----Aktionen ------- Datensatz speichern / update ----------------
	public void saveAktionPosition(
			int capoid, int capopos, String capobem, int capocaid, String capostcktitel,
			String capoedition, String capostckart, int capodauermin, int capodauersec,
			String caposonstiges, String caponr, String caposeite, String capobesetzung,
			String capotonart, String capokomp, String capobearb, int capoauspcnd, int capozwischentext,
			int capolitid, String capozeilentyp, String capotitelbild, boolean neu) throws SQLException
	{

		String sql;
		if (neu)
		{
			sql = "INSERT INTO tblChoraktionenPositionen ("
					+ "capo_pos, capo_bem, capo_ca_id, capo_stcktitel, "
					+ "capo_edition, capo_art, capo_dauermin, capo_dauersec, capo_sonstiges, "
					+ "capo_nr, capo_besetzung, capo_tonart, capo_komponist, capo_bearbeiter,"
					+ "capo_auspcnd, capo_zwischentext, capo_lit_id, capo_zeilentyp, capo_titelbild, "
					+ "capo_seite) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,"
					+ " ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		}
		else
		{
			sql = "UPDATE tblChoraktionenPositionen "
//					+ "SET ca_akttyp=?, ca_datum=?, ca_beschreibung=?, ca_treffpunkt=?, "
//					+ "ca_beginn=?, ca_anwesend=?, ca_bemerkung=?, ca_verantwortlich=?, ca_gruppe=?, "
//					+ "ca_aktionsort=?, ca_veranstalter=?, ca_auftrittstermin=?, ca_gema=? "
//					+ "WHERE ca_id=?";
					+ "SET capo_pos=?, capo_bem=?, capo_ca_id=?, capo_stcktitel=?, "
					+ "capo_edition=?, capo_art=?, capo_dauermin=?, capo_dauersec=?, capo_sonstiges=?, "
					+ "capo_nr=?, capo_besetzung=?, capo_tonart=?, capo_komponist=?, capo_bearbeiter=?,"
					+ "capo_auspcnd=?, capo_zwischentext=?, capo_lit_id=?, capo_zeilentyp=?, capo_titelbild=?, "
					+ "capo_seite=? "
					+ "WHERE capo_id =? ";

		}

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{

			int index = 1;
			pstmt.setInt(index++, capopos);
			pstmt.setString(index++, capobem);
			pstmt.setInt(index++, capocaid);
			pstmt.setString(index++, capostcktitel);
			pstmt.setString(index++, capoedition);
			pstmt.setString(index++, capostckart);
			pstmt.setInt(index++, capodauermin);
			pstmt.setInt(index++, capodauersec);

			pstmt.setString(index++, caposonstiges);
			pstmt.setString(index++, caponr);
			pstmt.setString(index++, capobesetzung);
			pstmt.setString(index++, capotonart);
			pstmt.setString(index++, capokomp);
			pstmt.setString(index++, capobearb);
			pstmt.setInt(index++, capoauspcnd);
			pstmt.setInt(index++, capozwischentext);
			pstmt.setInt(index++, capolitid);
			pstmt.setString(index++, capozeilentyp);
			pstmt.setString(index++, capotitelbild);
			pstmt.setString(index++, caposeite);
			if (!neu)
			{
				pstmt.setInt(index, capoid);
			}

			pstmt.executeUpdate();
		}
	}

	// Aktionen - Positionen Verschieben
	// =======================================
	public void verschiebeAlleAbPosition(int caId, int posnr) throws SQLException
	{// Verschieben nach neuem Datensatz -- alles ab der Einfügepositon nach unten
		Connection conn = DBManager.getConnection();
		String sql = "UPDATE tblChoraktionenPositionen " +
				"SET capo_pos = capo_pos + 1 " +
				"WHERE capo_ca_id = ? AND capo_pos >= ?";

		PreparedStatement ps = conn.prepareStatement(sql);
		ps.setInt(1, caId); // ✅ Aktion (1:n Gruppierung)
		ps.setInt(2, posnr); // ✅ ab dieser Position verschieben
		ps.executeUpdate();
	}

	public void verschiebeAlleNachLoeschen(int caId, int geloeschtePos) throws SQLException
	{

		Connection conn = DBManager.getConnection();

		String sql = "UPDATE tblChoraktionenPositionen " +
				"SET capo_pos = capo_pos - 1 " +
				"WHERE capo_ca_id = ? AND capo_pos > ?";

		PreparedStatement ps = conn.prepareStatement(sql);

		ps.setInt(1, caId); // Aktion
		ps.setInt(2, geloeschtePos); // Position der gelöschten Zeile

		ps.executeUpdate();
	}

	public void setVerschiebenPosNr(int posid, int posnr)
	{
		String sql = null;
		

		try (Connection conn = DBManager.getConnection())
		{

			PreparedStatement pstmt;

			sql = "UPDATE tblChoraktionenPositionen SET capo_pos = ?  WHERE capo_id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, posnr);
			pstmt.setInt(2, posid);

			System.out.println("SQL auszuführen: " + sql);
			pstmt.executeUpdate();

		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 19)
			{
				Msgbox.warn("Fehler beim Speichern", "Verschieben ging schief");
			}
			else
			{
				System.err.println("SQL: " + sql);
				System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
			}
		}

	}

	// ---------------- Datensatz löschen ----------------
	public void deleteAndRenumber(int idLoeschen, int geloeschtePos, int capo_caid) throws SQLException
	{

		String deleteSql = """
	        DELETE FROM tblChoraktionenPositionen
	        WHERE capo_id = ?
	        """;

		String renumberSql = """
	        UPDATE tblChoraktionenPositionen
	        SET capo_pos = capo_pos - 1
	        WHERE capo_pos > ?
	          AND capo_ca_id = ?
	        """;

		try (Connection con = DBManager.getConnection())
		{
			con.setAutoCommit(false);

			try (PreparedStatement del = con.prepareStatement(deleteSql);
					PreparedStatement ren = con.prepareStatement(renumberSql))
			{

				del.setInt(1, idLoeschen);
				del.executeUpdate();

				ren.setInt(1, geloeschtePos);
				ren.setInt(2, capo_caid);
				ren.executeUpdate();

				con.commit();
			}
			catch (SQLException e)
			{
				con.rollback();
				throw e;
			}
		}
	}

	public AktionenEinzelnPerId getAktionPerId(int id)
	{

		String sql = """
	        SELECT ca_id, ca_akttyp, ca_datum, ca_beschreibung,
	               ca_treffpunkt, ca_bemerkung,
	               ca_verantwortlich, ca_gruppe, ca_aktionsort
	        FROM tblChoraktionen
	        WHERE ca_id = ?
	        """;
		LocalDate datum = null;
		LocalTime zeit = null;
		String datumStr = "";
		String zeitStr = "";

		Connection conn = DBManager.getConnection();
		try (PreparedStatement stmt = conn.prepareStatement(sql))
		{

			stmt.setInt(1, id);

			ResultSet rs = stmt.executeQuery();

			if (rs.next())
			{
				datumStr = rs.getString("ca_datum");
				zeitStr = rs.getString("ca_treffpunkt");
				// selbst vorab parsen von SQLite
				if (datumStr != null)
				{
					datum = LocalDate.parse(datumStr); // ISO-Format yyyy-MM-dd
				}
				if (zeitStr != null)
				{
					zeit = LocalTime.parse(zeitStr);
				}

				return new AktionenEinzelnPerId(
						rs.getInt("ca_id"),
						rs.getString("ca_akttyp"),
						datum,
						rs.getString("ca_beschreibung"),
						zeit,
						rs.getString("ca_bemerkung"),
						rs.getString("ca_verantwortlich"),
						rs.getString("ca_gruppe"),
						rs.getString("ca_aktionsort"));
			}

		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	// Aktionen Personen
	// ==================================================================
	// ---------------- Personen auslesen ----------------
	public List<AktionenListePersonenModel> getAktionenPersonenListeAll(int aktuellAktion) throws SQLException
	{
		List<AktionenListePersonenModel> resultList = new ArrayList<>();
		String sql = "SELECT * FROM tblChoraktionenPersonen WHERE cape_ca_id=? ";
		sql += ("ORDER BY cape_name COLLATE NOCASE, cape_vname COLLATE NOCASE");

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setInt(1, aktuellAktion);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					resultList.add(new AktionenListePersonenModel(
							rs.getInt("cape_id"),
							rs.getInt("cape_ca_id"),
							rs.getString("cape_name"),
							rs.getString("cape_vname"),
							rs.getString("cape_stimme"),
							rs.getString("cape_instrument")));
				}
			}
			return resultList;
		}
	}

	// ---------------- Mitwirkende speichern ----------------
	public void saveAktionenPerson(int aktuellAktion, int personId,
			String capename, String capevorname, String capestimme, String capeinstrument) throws SQLException
	{// wenn als PersonId 0 kommt 00 neuer Datensatz
		String sql = "";
		try (Connection conn = DBManager.getConnection())
		{
			if (personId == 0)
			{
				sql = "INSERT INTO tblChoraktionenPersonen "
						+ "(cape_ca_id, cape_name, cape_vname, cape_stimme, cape_instrument) "
						+ "VALUES (?, ?, ?, ?, ?)";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				int index = 1;
				pstmt.setInt(index++, aktuellAktion);
				pstmt.setString(index++, capename);
				pstmt.setString(index++, capevorname);
				pstmt.setString(index++, capestimme);
				pstmt.setString(index++, capeinstrument);
				pstmt.executeUpdate();
			}
			else
			{
				sql = "UPDATE tblChoraktionenPersonen " +
						"SET cape_ca_id = ?,"
						+ "cape_name = ?,"
						+ "cape_vname = ?,"
						+ "cape_stimme = ?,"
						+ "cape_instrument = ? "
						+ "WHERE cape_id = ?";
				PreparedStatement pstmt = conn.prepareStatement(sql);
				int index = 1;
				pstmt.setInt(index++, aktuellAktion);
				pstmt.setString(index++, capename);
				pstmt.setString(index++, capevorname);
				pstmt.setString(index++, capestimme);
				pstmt.setString(index++, capeinstrument);
				pstmt.setInt(index++, personId);
				pstmt.executeUpdate();

			}
		}
		catch (SQLException e)
		{

			if (e.getErrorCode() == 19)
			{
				Msgbox.warn("Doppelter Eintrag!", "Speichern nicht möglich -- Diese Person ist bereits der aktuellen Aktion zugewiesen");
			}
			else
			{
				System.err.println("SQL: " + sql);
				System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
			}
		}

	}

	// ---------------- Datensatz löschen ----------------
	public void deleteAktionenPerson(int cape_id) throws SQLException
	{
		String sql = "DELETE FROM tblChoraktionenPersonen WHERE cape_id=?";
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{

			pstmt.setInt(1, cape_id);
			pstmt.executeUpdate();
		}
	}

	// ---------------- Personen auslesen ----------------
	public List<CvwPersonenModel> getPersonenListeAll(
			String filpersonname, String filpersonvname, String filpersoninstrument,
			String filpersonchor, String filpersongruppe, String filpersonstimme) throws SQLException
	{
		String sql = "SELECT * FROM tblChorPersonen ";
		sql += ("WHERE "
				+ "pe_name LIKE ? AND "
				+ "pe_vorname LIKE ? AND "
				+ "pe_instrument LIKE ? AND "
				+ "pe_chor LIKE ? AND "
				+ "pe_stimme LIKE ? AND "
				+ "pe_gruppe LIKE ? "
				// + " ORDER BY pe_chor COLLATE NOCASE, pe_gruppe COLLATE NOCASE, pe_name
				// COLLATE NOCASE, pe_vorname COLLATE NOCASE"
				+ ";");

		List<CvwPersonenModel> resultList = new ArrayList<>();
		Connection conn = DBManager.getConnection();
		PreparedStatement pstmt = conn.prepareStatement(sql);
		{
			pstmt.setString(1, filpersonname + '%');
			pstmt.setString(2, filpersonvname + '%');
			pstmt.setString(3, filpersoninstrument + '%');
			pstmt.setString(4, filpersonchor + '%');
			pstmt.setString(5, filpersonstimme + '%');
			pstmt.setString(6, filpersongruppe + '%');

			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					resultList.add(new CvwPersonenModel(
							rs.getInt("pe_keyid"),
							rs.getInt("pe_id"),
							rs.getString("pe_name"),
							rs.getString("pe_vorname"),
							rs.getString("pe_instrument"),
							rs.getString("pe_chor"),
							rs.getString("pe_stimme"),
							rs.getString("pe_gruppe"),
							rs.getString("pe_telefon"),
							rs.getString("pe_mail")));
				}
			}

		}
		return resultList;
	}

	public List<CvwPersonenComboChorModel> getCvwPersonenComboChor() throws SQLException
	{
		String sql = "SELECT DISTINCT  pe_chor FROM tblChorPersonen ORDER BY pe_chor COLLATE NOCASE";
		List<CvwPersonenComboChorModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new CvwPersonenComboChorModel(
						rs.getString("pe_chor")));
			}
		}
		return result;
	}

	public List<CvwPersonenComboGruppeModel> getCvwPersonenComboGruppe() throws SQLException
	{
		String sql = "SELECT DISTINCT  pe_gruppe FROM tblChorPersonen ORDER BY pe_gruppe COLLATE NOCASE";
		List<CvwPersonenComboGruppeModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new CvwPersonenComboGruppeModel(
						rs.getString("pe_gruppe")));
			}
		}
		return result;
	}

	// ---------------- Datensatz löschen ----------------
	public void deleteCvwPerson(int pekey_id) throws SQLException
	{
		String sql = "DELETE FROM tblChorPersonen WHERE pe_keyid=?";
		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{

			pstmt.setInt(1, pekey_id);
			pstmt.executeUpdate();
		}
	}

	// ---------------- Mitwirkende speichern ----------------
	public int saveCvwPerson(int peid, String pechor, String pegruppe, String peinstrument, String pemail,
			String pename, String pestimme, String petelefon, String pevorname, int pekeyid) throws SQLException
	{

		String sql = "";
		@SuppressWarnings("unused")
		int returnpersonid = 0;

		try (Connection conn = DBManager.getConnection())
		{

			if (pekeyid == 0) // neuer Datensatz
			{

				sql = "INSERT INTO tblChorPersonen "
						+ "(pe_id, pe_chor, pe_gruppe, pe_instrument, pe_mail, pe_name, pe_stimme, pe_telefon, pe_vorname) "
						+ "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

				try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
				{

					int index = 1;

					pstmt.setInt(index++, 0);
					pstmt.setString(index++, pechor);
					pstmt.setString(index++, pegruppe);
					pstmt.setString(index++, peinstrument);
					pstmt.setString(index++, pemail);
					pstmt.setString(index++, pename);
					pstmt.setString(index++, pestimme);
					pstmt.setString(index++, petelefon);
					pstmt.setString(index++, pevorname);

					pstmt.executeUpdate();

					try (ResultSet rs = pstmt.getGeneratedKeys())
					{

						if (rs.next())
						{
							int newKeyId = rs.getInt(1);

							String updateSql = "UPDATE tblChorPersonen SET pe_id=? WHERE pe_keyid=?";

							try (PreparedStatement upd = conn.prepareStatement(updateSql))
							{
								upd.setInt(1, newKeyId);
								upd.setInt(2, newKeyId);
								upd.executeUpdate();
							}
							returnpersonid = newKeyId;
						}
					}
				}
			}
			else
			{

				peid = pekeyid;

				sql = "UPDATE tblChorPersonen "
						+ "SET pe_id = ?, "
						+ "pe_chor = ?, "
						+ "pe_gruppe = ?, "
						+ "pe_instrument = ?, "
						+ "pe_mail = ?, "
						+ "pe_name = ?, "
						+ "pe_stimme = ?, "
						+ "pe_telefon = ?, "
						+ "pe_vorname = ? "
						+ "WHERE pe_keyid = ?";

				try (PreparedStatement pstmt = conn.prepareStatement(sql))
				{

					int index = 1;

					pstmt.setInt(index++, peid);
					pstmt.setString(index++, pechor);
					pstmt.setString(index++, pegruppe);
					pstmt.setString(index++, peinstrument);
					pstmt.setString(index++, pemail);
					pstmt.setString(index++, pename);
					pstmt.setString(index++, pestimme);
					pstmt.setString(index++, petelefon);
					pstmt.setString(index++, pevorname);
					pstmt.setInt(index++, pekeyid);

					pstmt.executeUpdate();
				}
				returnpersonid = pekeyid;
			}
		}
		catch (SQLException e)
		{

			if (e.getErrorCode() == 19)
			{
				Msgbox.warn("Doppelter Eintrag!", "Speichern nicht möglich -- Diese Person ist bereits vorhanden");
			}
			else
			{
				System.err.println("SQL: " + sql);
				System.err.println("Fehlermeldung: " + e.getMessage() + " / Code: " + e.getErrorCode());
			}
		}
		return peid;
	}

//================================================================================= 
//  Aktionspositionen bearbeiten ...
//=================================================================================
	// Aktionsposition bearbeiten / Stückarten holen
	public List<StueckartlisteModel> getStueckartListeAll() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndStueckart ORDER BY sar_bez COLLATE NOCASE";
		List<StueckartlisteModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new StueckartlisteModel(
						rs.getString("sar_bez"),
						rs.getString("sar_db"),
						rs.getInt("sar_id")));
			}
		}

		return result;
	}

	// Aktionsposition bearbeiten / Alle Verlage holen
	public List<VerlaglisteModel> getVerlaglisteListeAlle() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndVerlag ORDER BY v_verlag COLLATE NOCASE";
		List<VerlaglisteModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new VerlaglisteModel(
						rs.getString("v_verlag"),
						rs.getString("v_ort"),
						rs.getString("v_bem"),
						rs.getString("v_erfasst"),
						rs.getString("v_db"),
						rs.getInt("v_id")));
			}
		}

		return result;
	}

	// Aktionsposition bearbeiten / Alle Themen holen
	public List<ThemenlisteModel> getThemenListeAll() throws SQLException
	{
		String sql = "SELECT th_bez, th_db, th_id FROM tblPcndThema ORDER BY th_bez COLLATE NOCASE";

		List<ThemenlisteModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new ThemenlisteModel(
						rs.getString("th_bez"),
						rs.getString("th_db"),
						rs.getInt("th_id")));
			}
		}

		return result;
	}

	// Aktionsposition bearbeiten / ALle Wochenlieder holen
	public List<WochenliedlisteModel> getWochenliedlisteListeAll() throws SQLException
	{
		String sql = "SELECT wl_bez, wl_rang, wl_db, wl_id FROM tblPcndWochenlied ORDER BY wl_rang COLLATE NOCASE";

		List<WochenliedlisteModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new WochenliedlisteModel(
						rs.getString("wl_bez"),
						rs.getString("wl_rang"),
						rs.getString("wl_db"),
						rs.getInt("wl_id")));
			}
		}

		return result;
	}
	// Aktionsposition bearbeiten / Notenmapopoen holen

	public List<NotenmappeModel> getNomaAll() throws SQLException
	{
		String sql = "SELECT * FROM tblNotenmappe";
		List<NotenmappeModel> result = new ArrayList<>();
		Connection connection = DBManager.getConnection();
		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new NotenmappeModel(
						rs.getInt("nm_id"),
						rs.getString("nm_bezeichnung"),
						rs.getString("nm_bemerkung")));
			}
		}

		return result;
	}

	// Aktionsposition bearbeiten / Literaturfilter zum Zuweisen an eine Positioen
	public List<LiteraturlisteModel> getLiteraturListeFilter(int filterlitid, String filterlittitel, String filterlitedit, String filterlitkomp, String filterlitstueckart,
			String filterlitthema,
			String filterlitwoli, String filterlitnoma, String filterverlag, String filterdichter) throws Exception
	{
		// Grund-SELECT
		String sqlStringLit = "";
		String sqlStringLit2 = "";

		sqlStringLit = "SELECT l_id,l_db, l_ls, l_db AS dbk,  l_edition, l_stueckart,  "
				+ "l_a_komp, l_a_bearb, l_stimmen, l_erfasst, "
				+ "l_nummer, l_nummersort, l_seite,  l_seitesort , "
				+ "l_tonart, l_stimmen, l_dauermin, l_dauersec, e_titelgrafikpfad, e_verlag, e_lt, ls_a_dichter";

		// f�r alle n:m M�glichkeiten ein unterschiedliches INNER-JOIN Filter
		// und Spalten
		// alle n:m Filter leer
		if (filterlitthema.isEmpty() && filterlitwoli.isEmpty() && filterlitnoma.isEmpty())
		{
			sqlStringLit2 = ",'wl' AS lswl_wl, 'th' AS lsth_th, 'n' AS nmed_nm_bez ";

			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM tblPcndEdition INNER JOIN " + "(tblPcndLiedstueck INNER JOIN tblPcndLiteratur " + "ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) "
					+ "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition ";

		}

		// nur Woli
		if (filterlitthema.isEmpty() && !filterlitwoli.isEmpty() && filterlitnoma.isEmpty())
		{
			sqlStringLit2 = ",lswl_wl, 'th' AS lsth_th, 'n' AS nmed_nm_bez ";
			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM " + " tblPcndEdition INNER JOIN ((tblPcndLiedstueck "
					+ "INNER JOIN tblPcndLiedstueckWochenlied ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckWochenlied.lswl_ls) "
					+ "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) " + "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition " + " ";
		}
		// nur Thema
		if (!filterlitthema.isEmpty() && filterlitwoli.isEmpty() && filterlitnoma.isEmpty())
		{
			sqlStringLit2 = ",'w' AS lswl_wl, lsth_th, 'n' AS nmed_nm_bez ";

			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM (tblPcndEdition INNER JOIN (tblPcndLiedstueck " + "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) "
					+ "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition) " + "INNER JOIN tblPcndLiedstueckThema ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckThema.lsth_ls ";
		}
		// nur Notenmappe
		if (filterlitthema.isEmpty() && filterlitwoli.isEmpty() && !filterlitnoma.isEmpty())
		{
			sqlStringLit2 = ",'w' AS lswl_wl, 'th' AS lsth_th, nmed_nm_bez ";
			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM tblNotenmappeEdition INNER JOIN (tblPcndEdition " + "INNER JOIN (tblPcndLiedstueck "
					+ "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) " + "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition) "
					+ "ON tblNotenmappeEdition.nmed_e_lt = tblPcndLiteratur.l_edition ";
		}
		// Wochenlied und Thema
		if (!filterlitthema.isEmpty() && !filterlitwoli.isEmpty() && filterlitnoma.isEmpty())
		{// Wochenlied und Thema
			sqlStringLit2 = ",lswl_wl, lsth_th, 'n' AS nmed_nm_bez ";
			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM ((tblPcndEdition INNER JOIN (tblPcndLiedstueck " + "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) "
					+ "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition) INNER JOIN tblPcndLiedstueckThema "
					+ "ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckThema.lsth_ls) INNER JOIN tblPcndLiedstueckWochenlied "
					+ "ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckWochenlied.lswl_ls ";
		}
		if (!filterlitthema.isEmpty() && filterlitwoli.isEmpty() && !filterlitnoma.isEmpty())
		{// Thema und Notenmappe
			sqlStringLit2 = ",'w' AS lswl_wl, lsth_th, nmed_nm_bez  ";
			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM ((tblPcndEdition INNER JOIN (tblPcndLiedstueck " + "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) "
					+ "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition) " + "INNER JOIN tblPcndLiedstueckThema ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckThema.lsth_ls) "
					+ "INNER JOIN tblNotenmappeEdition ON tblPcndLiteratur.l_edition = tblNotenmappeEdition.nmed_e_lt ";
		}

		if (filterlitthema.isEmpty() && !filterlitwoli.isEmpty() && !filterlitnoma.isEmpty())
		{// Wochenlied und
			// Notenmappe
			sqlStringLit2 = ",lswl_wl, 'th' AS lsth_th, nmed_nm_bez  ";
			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM ((tblPcndEdition INNER JOIN (tblPcndLiedstueck " + "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) "
					+ "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition) INNER JOIN tblNotenmappeEdition " + "ON tblPcndLiteratur.l_edition = tblNotenmappeEdition.nmed_e_lt) "
					+ "INNER JOIN tblPcndLiedstueckWochenlied ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckWochenlied.lswl_ls ";

		}
		if (!filterlitthema.isEmpty() && !filterlitwoli.isEmpty() && !filterlitnoma.isEmpty())
		{// alles
			sqlStringLit2 = " ,lswl_wl, lsth_th, nmed_nm_bez ";
			sqlStringLit += sqlStringLit2;
			sqlStringLit += "FROM (((tblPcndEdition INNER JOIN (tblPcndLiedstueck " + "INNER JOIN tblPcndLiteratur ON tblPcndLiedstueck.ls_titel = tblPcndLiteratur.l_ls) "
					+ "ON tblPcndEdition.e_lt = tblPcndLiteratur.l_edition) INNER JOIN tblNotenmappeEdition " + "ON tblPcndLiteratur.l_edition = tblNotenmappeEdition.nmed_e_lt) "
					+ "INNER JOIN tblPcndLiedstueckWochenlied ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckWochenlied.lswl_ls) "
					+ "INNER JOIN tblPcndLiedstueckThema ON tblPcndLiedstueck.ls_titel = tblPcndLiedstueckThema.lsth_ls ";

		}

		else
		{

		}
		sqlStringLit += "WHERE l_ls LIKE '" + filterlittitel + "%' " + "AND l_edition LIKE '" + filterlitedit + "%' " + "AND (l_a_komp LIKE '" + filterlitkomp
				+ "%' OR l_a_bearb LIKE '" + filterlitkomp + "%') " + "AND l_stueckart LIKE '" + filterlitstueckart + "%' " + "AND lswl_wl LIKE '" + filterlitwoli + "%' "
				+ "AND lsth_th LIKE '" + filterlitthema + "%' " + "AND nmed_nm_bez LIKE '" + filterlitnoma + "%' " + "AND e_verlag LIKE '" + filterverlag + "%' "
				+ "AND ls_a_dichter LIKE '" + filterdichter + "%' ";
		if (filterlitid > 0)
		{
			sqlStringLit += "AND l_id ='" + filterlitid + "'";

		}

		sqlStringLit += " LIMIT  " + ValuesGlobals.filtermax;
		sqlStringLit += ";";
		// System.out.println(sqlStringLit);
		this.setUpStatement(sqlStringLit);
		this.runStatement();
		ResultSet rs = this.getResults();
		List<LiteraturlisteModel> methodResult = extractLiteraturListe(rs);
		return methodResult;
	}

	private List<LiteraturlisteModel> extractLiteraturListe(ResultSet rs) throws SQLException
	{
		List<LiteraturlisteModel> methodResult = new ArrayList<>();

		while (rs.next())
		{
			methodResult.add(new LiteraturlisteModel(

					rs.getInt("l_id"),
					rs.getString("l_db"),
					rs.getString("l_ls"),
					// rs.getString("l_edition"),
					rs.getString("e_lt"),
					rs.getString("l_stueckart"),
					rs.getString("l_a_komp"),
					rs.getString("l_a_bearb"),
					rs.getString("l_seite"),
					rs.getString("l_nummer"),
					rs.getString("l_stimmen"),
					rs.getString("l_tonart"),
					rs.getString("e_titelgrafikpfad"),
					toNullableInt(rs, "l_seitesort"),
					toNullableInt(rs, "l_nummersort"),
					rs.getString("lswl_wl"), // ✅ jetzt gefüllt
					rs.getString("lsth_th"), // ✅ jetzt gefüllt
					rs.getString("nmed_nm_bez"), // ✅ jetzt gefüllt
					toNullableInt(rs, "l_dauermin"),
					toNullableInt(rs, "l_dauersec"),
					rs.getString("e_verlag"),
					rs.getString("ls_a_dichter"),
					rs.getString("l_erfasst")));
		}

		return methodResult;
	}

	// ---------------- Notenasugaben nach Import auf Langtext umstellen
	// ----------------
	public void saveUpdateNotenausgaben() throws SQLException
	{

		String sql = "UPDATE tblChoraktionenPositionen SET capo_edition = "
				+ "(SELECT e_lt FROM tblPcndEdition "
				+ "WHERE tblPcndEdition.e_kt = tblChoraktionenPositionen.capo_edition) "
				+ "WHERE EXISTS (SELECT 1 FROM tblPcndEdition "
				+ "WHERE tblPcndEdition.e_kt = tblChoraktionenPositionen.capo_edition"
				+ ");";

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{

			pstmt.executeUpdate();
		}
	}
	
	public void saveUpdateTitelgrafiken() throws SQLException
	{

		String sql = "UPDATE tblChoraktionenPositionen SET capo_titelbild = "
				+ "(SELECT e_titelgrafikpfad FROM tblPcndEdition "
				+ "WHERE tblPcndEdition.e_lt = tblChoraktionenPositionen.capo_edition) "
				+ "WHERE EXISTS (SELECT 1 FROM tblPcndEdition "
				+ "WHERE tblPcndEdition.e_lt = tblChoraktionenPositionen.capo_edition"
				+ ");";

		try (Connection conn = DBManager.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{

			pstmt.executeUpdate();
		}
	}



	// ========================================================================
	// Hilfsmethoden
	// ========================================================================

	private Integer toNullableInt(ResultSet rs, String columnName) throws SQLException
	{
		Object rawValue = rs.getObject(columnName);
		if (rawValue instanceof Number number)
		{
			int intValue = number.intValue();
			return intValue == 0 ? null : intValue;
		}
		return null;
	}

}
