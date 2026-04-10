package application.controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import application.ValuesGlobals;
import application.models.*;
import application.utils.AktuellesDatum;

public class DatabaseControllerPcndStartfenster extends DatabaseControllerTemplate
{

	// --------------------------------------------------------------------------------
	// Konstruktor
	// --------------------------------------------------------------------------------
	public DatabaseControllerPcndStartfenster() throws SQLException
	{
		super();
	}

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
		preparedStatement = connection.prepareStatement(sql);
	}

//    private void setUpStatement(PreparedStatement stmt) throws SQLException {
//        checkConnection();
//        if (preparedStatement != null && !preparedStatement.isClosed()) {
//            preparedStatement.close();
//        }
//        preparedStatement = stmt;
//    }

	// --------------------------------------------------------------------------------
	// Statement ausführen
	// --------------------------------------------------------------------------------
	@Override
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
	@Override
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
		if (connection == null || connection.isClosed())
		{
			throw new SQLException("Keine gültige Datenbankverbindung!");
		}
	}

// #########################################################################################
// ================== Registertab 1: LITERATUR ==================
	// Filter
//	public List<LiteraturlisteModel> getLiteraturListeFilter(
//	        int filterlitid,
//	        String filterlittitel,
//	        String filterlitedit,
//	        String filterlitkomp,
//	        String filterlitstueckart,
//	        String filterlitthema,
//	        String filterlitwoli,
//	        String filterlitnoma,
//	        String filterverlag,
//	        String filterdichter) throws SQLException
//	{
//
//	    StringBuilder sql = new StringBuilder();
//	    sql.append("""
//	        SELECT
//	            l.l_id,
//	            l.l_db,
//	            l.l_ls,
//	            l.l_edition,
//	            l.l_stueckart,
//	            l.l_a_komp,
//	            l.l_a_bearb,
//	            l.l_seite,
//	            l.l_nummer,
//	            l.l_stimmen,
//	            l.l_tonart,
//	            e.e_titelgrafikpfad,
//	            l.l_seitesort,
//	            l.l_nummersort,
//
//	            -- Anzeige: genau 1 Wert pro Zeile
//	            (
//	                SELECT wl.lswl_wl
//	                FROM tblPcndLiedstueckWochenlied wl
//	                WHERE wl.lswl_ls = ls.ls_titel
//	                ORDER BY wl.lswl_wl
//	                LIMIT 1
//	            ) AS lswl_wl,
//
//	            (
//	                SELECT th.lsth_th
//	                FROM tblPcndLiedstueckThema th
//	                WHERE th.lsth_ls = ls.ls_titel
//	                ORDER BY th.lsth_th
//	                LIMIT 1
//	            ) AS lsth_th,
//
//	            (
//	                SELECT nm.nmed_nm_bez
//	                FROM tblNotenmappeEdition nm
//	                WHERE nm.nmed_e_lt = l.l_edition
//	                ORDER BY nm.nmed_nm_bez
//	                LIMIT 1
//	            ) AS nmed_nm_bez,
//
//	            l.l_dauermin,
//	            l.l_dauersec,
//	            e.e_verlag,
//	            ls.ls_a_dichter,
//	            l.l_erfasst
//
//	        FROM tblPcndLiteratur l
//	        INNER JOIN tblPcndEdition e ON e.e_lt = l.l_edition
//	        INNER JOIN tblPcndLiedstueck ls ON ls.ls_titel = l.l_ls
//	        WHERE 1 = 1
//	    """);
//
//	    List<Object> params = new ArrayList<>();
//
//	    // ---------- direkte Filter ----------
//	    if (filterlitid > 0)
//	    {
//	        sql.append(" AND l.l_id = ? ");
//	        params.add(filterlitid);
//	    }
//
//	    if (filterlittitel != null && !filterlittitel.isEmpty())
//	    {
//	        sql.append(" AND l.l_ls LIKE ? ");
//	        params.add(filterlittitel + "%");
//	    }
//
//	    if (filterlitedit != null && !filterlitedit.isEmpty())
//	    {
//	        sql.append(" AND l.l_edition LIKE ? ");
//	        params.add(filterlitedit + "%");
//	    }
//
//	    if (filterlitkomp != null && !filterlitkomp.isEmpty())
//	    {
//	        sql.append(" AND (l.l_a_komp LIKE ? OR l.l_a_bearb LIKE ?) ");
//	        params.add(filterlitkomp + "%");
//	        params.add(filterlitkomp + "%");
//	    }
//
//	    if (filterlitstueckart != null && !filterlitstueckart.isEmpty())
//	    {
//	        sql.append(" AND l.l_stueckart LIKE ? ");
//	        params.add(filterlitstueckart + "%");
//	    }
//
//	    if (filterverlag != null && !filterverlag.isEmpty())
//	    {
//	        sql.append(" AND e.e_verlag LIKE ? ");
//	        params.add(filterverlag + "%");
//	    }
//
//	    if (filterdichter != null && !filterdichter.isEmpty())
//	    {
//	        sql.append(" AND ls.ls_a_dichter LIKE ? ");
//	        params.add(filterdichter + "%");
//	    }
//
//	    // ---------- EXISTS Filter (n:m) ----------
//	    if (filterlitwoli != null && !filterlitwoli.isEmpty())
//	    {
//	        sql.append("""
//	            AND EXISTS (
//	                SELECT 1
//	                FROM tblPcndLiedstueckWochenlied wl
//	                WHERE wl.lswl_ls = ls.ls_titel
//	                  AND wl.lswl_wl LIKE ?
//	            )
//	        """);
//	        params.add(filterlitwoli + "%");
//	    }
//
//	    if (filterlitthema != null && !filterlitthema.isEmpty())
//	    {
//	        sql.append("""
//	            AND EXISTS (
//	                SELECT 1
//	                FROM tblPcndLiedstueckThema th
//	                WHERE th.lsth_ls = ls.ls_titel
//	                  AND th.lsth_th LIKE ?
//	            )
//	        """);
//	        params.add(filterlitthema + "%");
//	    }
//
//	    if (filterlitnoma != null && !filterlitnoma.isEmpty())
//	    {
//	        sql.append("""
//	            AND EXISTS (
//	                SELECT 1
//	                FROM tblNotenmappeEdition nm
//	                WHERE nm.nmed_e_lt = l.l_edition
//	                  AND nm.nmed_nm_bez LIKE ?
//	            )
//	        """);
//	        params.add(filterlitnoma + "%");
//	    }
//
//	    sql.append(" LIMIT ").append(ValuesGlobals.filtermax);
//
//	    // ---------- Execute ----------
//	    try (PreparedStatement stmt = connection.prepareStatement(sql.toString()))
//	    {
//	        for (int i = 0; i < params.size(); i++)
//	        {
//	            stmt.setObject(i + 1, params.get(i));
//	        }
//
//	        List<LiteraturlisteModel> result = new ArrayList<>();
//
//	        try (ResultSet rs = stmt.executeQuery())
//	        {
//	            while (rs.next())
//	            {
//	                result.add(new LiteraturlisteModel(
//	                        rs.getInt("l_id"),
//	                        rs.getString("l_db"),
//	                        rs.getString("l_ls"),
//	                        rs.getString("l_edition"),
//	                        rs.getString("l_stueckart"),
//	                        rs.getString("l_a_komp"),
//	                        rs.getString("l_a_bearb"),
//	                        rs.getString("l_seite"),
//	                        rs.getString("l_nummer"),
//	                        rs.getString("l_stimmen"),
//	                        rs.getString("l_tonart"),
//	                        rs.getString("e_titelgrafikpfad"),
//	                        toNullableInt(rs, "l_seitesort"),
//	                        toNullableInt(rs, "l_nummersort"),
//	                        rs.getString("lswl_wl"),     // ✅ jetzt gefüllt
//	                        rs.getString("lsth_th"),     // ✅ jetzt gefüllt
//	                        rs.getString("nmed_nm_bez"), // ✅ jetzt gefüllt
//	                        toNullableInt(rs, "l_dauermin"),
//	                        toNullableInt(rs, "l_dauersec"),
//	                        rs.getString("e_verlag"),
//	                        rs.getString("ls_a_dichter"),
//	                        rs.getString("l_erfasst")
//	                ));
//	            }
//	        }
//
//	        return result;
//	    }
//	}
//
	// Literatur neu und editieren


//	
	public List<LiteraturlisteModel> getLiteraturListeFilter(int filterlitid, String filterlittitel, String filterlitedit, String filterlitkomp, String filterlitstueckart,
			String filterlitthema,
			String filterlitwoli, String filterlitnoma, String filterverlag, String filterdichter) throws Exception
	{
		// Grund-SELECT
		String sqlStringLit = "";
		String sqlStringLit2 = "";
		// 02.05.2025 geändert mit NULLIF
		sqlStringLit =
//				"SELECT l_id, l_ls, l_db AS dbk, l_edition, l_stueckart, l_a_komp AS komponist, " +
//			    "l_a_bearb, l_stimmen, l_nummer, NULLIF(l_nummersort, 0) AS l_nummersort, " +    
//				"l_seite, NULLIF(l_seitesort, 0) AS l_seitesort, l_tonart, l_stimmen, NULLIF(l_dauermin, 0) AS l_dauermin, " +
//			    "NULLIF(l_dauersec, 0) AS l_dauersec, e_titelgrafikpfad, e_verlag, ls_a_dichter";

				"SELECT l_id,l_db, l_ls, l_db AS dbk,  l_edition, l_stueckart,  "
						+ "l_a_komp, l_a_bearb, l_stimmen, l_erfasst, "
						+ "l_nummer, l_nummersort, l_seite,  l_seitesort , "
						+ "l_tonart, l_stimmen, l_dauermin, l_dauersec, e_titelgrafikpfad, e_verlag, ls_a_dichter";

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
		//System.out.println(sqlStringLit);
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
//					rs.getInt("l_id"),
//					rs.getString("dbk"),
//					rs.getString("l_ls"),
//					rs.getString("l_edition"),
//					rs.getString("l_stueckart"),
//					rs.getString("komponist"),
//					rs.getString("bearbeiter"),
//					rs.getString("l_seite"),
//					rs.getString("l_nummer"),
//					rs.getString("l_stimmen"),
//					rs.getString("l_tonart"),
//					rs.getString("e_titelgrafikpfad"),
//					toNullableInt(rs, "l_seitesort"),
//					toNullableInt(rs, "l_nummersort"),
//					rs.getString("lswl_wl"),
//					rs.getString("lsth_th"),
//					rs.getString("nmed_nm_bez"),
//					toNullableInt(rs, "l_dauermin"),
//					toNullableInt(rs, "l_dauersec"),
//					rs.getString("e_verlag"),
//					rs.getString("ls_a_dichter")));


                    rs.getInt("l_id"),
                    rs.getString("l_db"),
                    rs.getString("l_ls"),
                    rs.getString("l_edition"),
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
                    rs.getString("lswl_wl"),     // ✅ jetzt gefüllt
                    rs.getString("lsth_th"),     // ✅ jetzt gefüllt
                    rs.getString("nmed_nm_bez"), // ✅ jetzt gefüllt
                    toNullableInt(rs, "l_dauermin"),
                    toNullableInt(rs, "l_dauersec"),
                    rs.getString("e_verlag"),
                    rs.getString("ls_a_dichter"),
                    rs.getString("l_erfasst")));
		}

		return methodResult;
	}
	
	public void setLiteraturNeu(
			String dbkennung,
			String littitel,
			String litnotenausgedit,
			String litkompedit,
			String litbearbedit,
			String litstueckartedit,
			Integer litdauermin,
			Integer litdauersec,
			String litgrafikedit,
			Integer litseiteedit,
			String litseitezusedit,
			Integer litnummeredit,
			String litnummerzusedit,
			String littonartedit,
			String litbesetzungedit) throws SQLException
	{

		String sql = "INSERT INTO tblPcndLiteratur " +
				"(l_db, l_ls, l_edition, l_stueckart, l_a_komp, l_a_bearb, " +
				"l_dauermin, l_dauersec, l_seitesort, l_seite, l_nummersort, l_nummer, " +
				"l_tonart, l_stimmen, l_erfasst, user_modified) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, 1)";

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, dbkennung);
			pstmt.setString(2, littitel);
			pstmt.setString(3, litnotenausgedit);
			pstmt.setString(4, litstueckartedit);
			pstmt.setString(5, litkompedit);
			pstmt.setString(6, litbearbedit);
			pstmt.setInt(7, litdauermin != null ? litdauermin : 0);
			pstmt.setInt(8, litdauersec != null ? litdauersec : 0);
			pstmt.setInt(9, litseiteedit != null ? litseiteedit : 0);
			pstmt.setString(10, litseitezusedit);
			pstmt.setInt(11, litnummeredit != null ? litnummeredit : 0);
			pstmt.setString(12, litnummerzusedit != null ? litnummerzusedit : "");
			pstmt.setString(13, littonartedit != null ? littonartedit : "");
			pstmt.setString(14, litbesetzungedit != null ? litbesetzungedit : "");

			int rowsAffected = pstmt.executeUpdate();
			System.out.println("Literatur neu hinzugefügt, Zeilen betroffen: " + rowsAffected);
		}
	}

	public Integer getLitIdNeuerDs() throws Exception
	{
		String sqlStatement = "SELECT MAX(l_id) AS max_id FROM tblPcndLiteratur";
		System.out.println(sqlStatement);

		this.setUpStatement(sqlStatement);
		this.runStatement();
		ResultSet rs = this.getResults();

		Integer maxId = null;
		if (rs.next())
		{
			maxId = rs.getInt("max_id"); // Holt den MAX-Wert
			if (rs.wasNull())
			{
				maxId = 0; // Wenn die Tabelle leer ist
			}
		}

		return maxId;
	}

	// --------------------------------------------------------------------------------------------
	public void setLiteraturEdit(
			String dbkennung,
			String littiteledit,
			String litnotenausgedit,
			String litkompedit,
			String litbearbedit,
			String litstueckartedit,
			Integer litdauermin,
			Integer litdauersec,
			String litgrafikedit,
			Integer litseiteedit,
			String litseitezusedit,
			Integer litnummeredit,
			String litnummerzusedit,
			String littonartedit,
			String litbesetzungedit,
			String litidedit) throws SQLException
	{

		String dsEditiert = new AktuellesDatum().getDateAsString();

		String sql = "UPDATE tblPcndLiteratur SET "
				+ "l_db = ?, "
				+ "l_ls = ?, "
				+ "l_edition = ?, "
				+ "l_stueckart = ?, "
				+ "l_a_komp = ?, "
				+ "l_a_bearb = ?, "
				+ "l_dauermin = ?, "
				+ "l_dauersec = ?, "
				+ "l_seitesort = ?, "
				+ "l_seite = ?, "
				+ "l_nummersort = ?, "
				+ "l_nummer = ?, "
				+ "l_tonart = ?, "
				+ "l_stimmen = ?, "
				+ "l_erfasst = ?, "
				+ "user_modified =? "
				+ "WHERE l_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, dbkennung);
			pstmt.setString(2, littiteledit);
			pstmt.setString(3, litnotenausgedit);
			pstmt.setString(4, litstueckartedit);
			pstmt.setString(5, litkompedit);
			pstmt.setString(6, litbearbedit);
			pstmt.setInt(7, litdauermin != null ? litdauermin : 0);
			pstmt.setInt(8, litdauersec != null ? litdauersec : 0);
			pstmt.setInt(9, litseiteedit != null ? litseiteedit : 0);
			pstmt.setString(10, litseitezusedit);
			pstmt.setInt(11, litnummeredit != null ? litnummeredit : 0);
			pstmt.setString(12, litnummerzusedit);
			pstmt.setString(13, littonartedit);
			pstmt.setString(14, litbesetzungedit);
			pstmt.setString(15, dsEditiert);
			pstmt.setInt(16, 1);
			pstmt.setString(17, litidedit);
				

			int rowsAffected = pstmt.executeUpdate();
			System.out.println("Literatur aktualisiert, Zeilen: " + rowsAffected);
		}
	}

	public String fctLiteratureintragLoeschen(int litid)
	{
		String result = "OK"; // Standard: kein Fehler
		String sqlDel = "DELETE FROM tblPcndLiteratur WHERE l_id = ?";

		try (PreparedStatement pstmt = connection.prepareStatement(sqlDel))
		{
			pstmt.setInt(1, litid);
			int rowsAffected = pstmt.executeUpdate();

			if (rowsAffected == 0)
			{
				result = "Kein Eintrag gefunden";
			}
			else
			{
				System.out.println("Literatureintrag gelöscht, Zeilen: " + rowsAffected);
			}
		}
		catch (SQLException e)
		{
			// SQLite spezifische Fehlercodes
			switch (e.getErrorCode())
			{
			case 19: // Constraint violation, z.B. FK verletzt
				System.out.println("Fehler: Fremdschlüsselverletzung beim Löschen: " + e.getMessage());
				result = "Fremdschlüsselverletzung";
				break;
			default:
				System.out.println("SQL-Fehler: " + e.getMessage() + " / Code: " + e.getErrorCode());
				result = "Fehler: " + e.getMessage();
			}
		}

		return result;
	}

// #########################################################################################
// ================== Registertab 2: NOTENAUSGABEN (EDITIONEN)
// Filter
	public List<EditionenlisteKompaktModel> getEditionenListeFilterNoma(String filterEditLang, String filterVerlag) throws SQLException
	{
		String sql = "SELECT e_db AS dbkedit, e_lt, e_verlag, e_titelgrafikpfad " +
				"FROM tblPcndEdition " +
				"WHERE e_lt LIKE ? AND e_verlag LIKE ? " +
				"ORDER BY e_lt COLLATE NOCASE;";

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, filterEditLang + "%");
		stmt.setString(2, filterVerlag + "%");

		ResultSet rs = stmt.executeQuery();
		return extractEditionenKompaktListe(rs);
	}

	private List<EditionenlisteKompaktModel> extractEditionenKompaktListe(ResultSet rs) throws SQLException
	{
		List<EditionenlisteKompaktModel> result = new ArrayList<>();
		while (rs.next())
		{
			result.add(new EditionenlisteKompaktModel(
					rs.getString("dbkedit"),
					rs.getString("e_lt"),
					rs.getString("e_titelgrafikpfad"),
					rs.getString("e_verlag")));
		}
		return result;
	}

	// -------------------
	public List<EditionenlisteModel> getEditionenListeFilter(String filterEditLang, String filterVerlag, String filterNoma) throws SQLException
	{
		String sql;
		boolean useNomaJoin = !filterNoma.isEmpty();

		if (useNomaJoin)
		{
			// Mit Noma-Filter: Join auf tblNotenmappeEdition
			sql = "SELECT e_db AS dbkedit, e_lt, e_kt, e_verlag, e_editjahr, e_hrsg, e_bestnr, " +
					"e_beschreibung, e_edart, e_titelgrafikpfad, e_eingabezeitpunkt, e_schwierig, nmed_nm_bez, e_erfasst, e_id " +
					"FROM tblNotenmappeEdition " +
					"INNER JOIN tblPcndEdition ON tblNotenmappeEdition.nmed_e_lt = tblPcndEdition.e_lt " +
					"WHERE e_lt LIKE ? AND e_verlag LIKE ? AND nmed_nm_bez LIKE ? " +
					"ORDER BY e_lt COLLATE NOCASE;";
		}
		else
		{
			// Ohne Noma-Filter: kein Join nötig, Dummy-Spalte für nmed_nm_bez
			sql = "SELECT e_db AS dbkedit, e_lt, e_kt, e_verlag, e_editjahr, e_hrsg, e_bestnr, " +
					"e_beschreibung, e_edart, e_titelgrafikpfad, e_eingabezeitpunkt, e_schwierig,e_erfasst, e_id, " +
					"'nm' AS nmed_nm_bez " +
					"FROM tblPcndEdition " +
					"WHERE e_lt LIKE ? AND e_verlag LIKE ? " +
					"ORDER BY e_lt COLLATE NOCASE;";
		}

		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setString(1, filterEditLang + "%");
		stmt.setString(2, filterVerlag + "%");
		if (useNomaJoin)
		{
			stmt.setString(3, filterNoma + "%");
		}

		ResultSet rs = stmt.executeQuery();
		return extractEditionenListe(rs);
	}

	private List<EditionenlisteModel> extractEditionenListe(ResultSet rs) throws SQLException
	{
		List<EditionenlisteModel> result = new ArrayList<>();
		while (rs.next())
		{
			result.add(new EditionenlisteModel(
					rs.getInt("e_id"),
					rs.getString("dbkedit"),
					rs.getString("e_editjahr"),
					rs.getString("e_eingabezeitpunkt"),
					rs.getString("e_lt"),
					rs.getString("e_kt"),
					rs.getString("e_hrsg"),
					rs.getString("e_bestnr"),
					rs.getString("e_edart"),
					rs.getString("e_beschreibung"),
					rs.getString("e_titelgrafikpfad"),
					rs.getString("e_verlag"),
					rs.getString("nmed_nm_bez"),
					rs.getString("e_schwierig"),
					rs.getString("e_erfasst")));
		}
		return result;
	}

	// Combobox Editionsart
	public List<EditionenlisteComboModel> getEditionenArtGroup() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndEditionart;";

		List<EditionenlisteComboModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new EditionenlisteComboModel(rs.getString("ea_bez")));
			}
		}

		return result;
	}

	// Notenausgaben neu und editieren
	// ##########################################
	public void setNotenausgabeNeu(String lt, String kt, String verlag, String hrsg,
			String bestnr, String edart, String editjahr,
			String schwierig, String grafik, String beschr, String erfasst, String db) throws SQLException
	// ACHTUNG: Eingabzeitpunkt=Grunderfassung (wird nicht verändert
	// erfasst=Änderungen
	// editjahr=Herausgabejahr
	{

		String sql = """
                INSERT INTO tblPcndEdition (e_id, e_lt, e_kt, e_verlag, e_hrsg, 
                e_bestnr, e_edart, e_editjahr, e_schwierig,e_titelgrafikpfad, e_beschreibung, e_erfasst,e_eingabezeitpunkt, e_db, user_modified )
                VALUES ((SELECT IFNULL(MAX(e_id),0)+1 FROM tblPcndEdition), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1)
                """;
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lt);
			pstmt.setString(2, kt);
			pstmt.setString(3, verlag);
			pstmt.setString(4, hrsg);
			pstmt.setString(5, bestnr);
			pstmt.setString(6, edart);
			pstmt.setString(7, editjahr);
			pstmt.setString(8, schwierig);
			pstmt.setString(9, grafik);
			pstmt.setString(10, beschr);
			pstmt.setString(11, erfasst);
			pstmt.setString(12, erfasst);
			pstmt.setString(13, db);
			pstmt.executeUpdate();
		}
	}

	public void setNotenausgabeEditSpeichern(String lt, String kt, String verlag, String hrsg,
			String bestnr, String edart, String editjahr,
			String schwierig, String grafik, String beschr, String erfasst, String db, int id, String ltalt) throws SQLException

	// ACHTUNG: Eingabzeitpunkt=Grunderfassung (wird nicht verändert
	// erfasst=Änderungen
	// editjahr=Herausgabejahr
	{

		String sql = """
                UPDATE tblPcndEdition SET e_lt = ?, e_kt =?, e_verlag =?, e_hrsg =?, 
                e_bestnr =?, e_edart =?, e_editjahr=?, e_schwierig=?,e_titelgrafikpfad=?, e_beschreibung=?, e_erfasst=?, e_db=? , user_modified=1
                WHERE e_lt = ?
                """;
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lt);
			pstmt.setString(2, kt);
			pstmt.setString(3, verlag);
			pstmt.setString(4, hrsg);
			pstmt.setString(5, bestnr);
			pstmt.setString(6, edart);
			pstmt.setString(7, editjahr);
			pstmt.setString(8, schwierig);
			pstmt.setString(9, grafik);
			pstmt.setString(10, beschr);
			pstmt.setString(11, erfasst);
			pstmt.setString(12, db);
			pstmt.setString(13, ltalt);
			pstmt.executeUpdate();
		}
	}

	public void deleteNotenausgabe(int id) throws SQLException
	{
			String sql = """
		    DELETE FROM tblPcndEdition WHERE e_id = ?
		    """;
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}

	}

// #########################################################################################
// ================== Registertab 3: LIEDER STÜCKE ==================
	// Filtern
//	public List<LiederStueckeModel> getStueckeLiederListe(
//			int id,
//			String filterTitel,
//			String filterDichter,
//			String filterThema,
//			String filterWochenlied,
//			String filterBibel,
//			String filterGesangbuch) throws SQLException
//	{
//
//		StringBuilder sql = new StringBuilder("""
//            SELECT
//                ls.ls_id,
//                ls.ls_titel,
//                ls.ls_db AS ls_dbs,
//                ls.ls_a_dichter,
//                ls.ls_erfasst,
//
//                COALESCE((
//                    SELECT MIN(th.lsth_th)
//                    FROM tblPcndLiedstueckThema th
//                    WHERE th.lsth_ls = ls.ls_titel
//                ), '') AS lsth_th,
//
//                COALESCE((
//                    SELECT MIN(wl.lswl_wl)
//                    FROM tblPcndLiedstueckWochenlied wl
//                    WHERE wl.lswl_ls = ls.ls_titel
//                ), '') AS lswl_wl,
//
//                COALESCE((
//                    SELECT MIN(bi.lsbi_bi)
//                    FROM tblPcndLiedstueckBibel bi
//                    WHERE bi.lsbi_ls = ls.ls_titel
//                ), '') AS lsbi_bi,
//
//                COALESCE((
//                    SELECT MIN(gb.lsgb_g)
//                    FROM tblPcndLiedstueckGesangbuch gb
//                    WHERE gb.lsgb_ls = ls.ls_titel
//                ), '') AS lsgb_g
//
//            FROM tblPcndLiedstueck ls
//            WHERE 1 = 1
//            """);
//
//		List<Object> params = new ArrayList<>();
//
//		// -------- Basisfilter --------
//		if (filterTitel != null && !filterTitel.isBlank())
//		{
//			sql.append(" AND ls.ls_titel LIKE ? COLLATE NOCASE");
//			params.add(filterTitel + "%");
//		}
//
//		if (filterDichter != null && !filterDichter.isBlank())
//		{
//			sql.append(" AND ls.ls_a_dichter LIKE ? COLLATE NOCASE");
//			params.add(filterDichter + "%");
//		}
//
//		if (id > 0)
//		{
//			sql.append(" AND ls.ls_id = ?");
//			params.add(id);
//		}
//
//		// -------- Filter ohne Duplikate --------
//		if (filterThema != null && !filterThema.isBlank())
//		{
//			sql.append("""
//                AND EXISTS (
//                    SELECT 1 FROM tblPcndLiedstueckThema th
//                    WHERE th.lsth_ls = ls.ls_titel
//                      AND th.lsth_th LIKE ? COLLATE NOCASE
//                )
//                """);
//			params.add(filterThema + "%");
//		}
//
//		if (filterWochenlied != null && !filterWochenlied.isBlank())
//		{
//			sql.append("""
//                AND EXISTS (
//                    SELECT 1 FROM tblPcndLiedstueckWochenlied wl
//                    WHERE wl.lswl_ls = ls.ls_titel
//                      AND wl.lswl_wl LIKE ? COLLATE NOCASE
//                )
//                """);
//			params.add(filterWochenlied + "%");
//		}
//
//		if (filterBibel != null && !filterBibel.isBlank())
//		{
//			sql.append("""
//                AND EXISTS (
//                    SELECT 1 FROM tblPcndLiedstueckBibel bi
//                    WHERE bi.lsbi_ls = ls.ls_titel
//                      AND bi.lsbi_bi LIKE ? COLLATE NOCASE
//                )
//                """);
//			params.add(filterBibel + "%");
//		}
//
//		if (filterGesangbuch != null && !filterGesangbuch.isBlank())
//		{
//			sql.append("""
//                AND EXISTS (
//                    SELECT 1 FROM tblPcndLiedstueckGesangbuch gb
//                    WHERE gb.lsgb_ls = ls.ls_titel
//                      AND gb.lsgb_g LIKE ? COLLATE NOCASE
//                )
//                """);
//			params.add(filterGesangbuch + "%");
//		}
//
//		sql.append(" ORDER BY ls.ls_titel COLLATE NOCASE");
//
//		try (PreparedStatement ps = connection.prepareStatement(sql.toString()))
//		{
//			for (int i = 0; i < params.size(); i++)
//			{
//				ps.setObject(i + 1, params.get(i));
//			}
//
//			try (ResultSet rs = ps.executeQuery())
//			{
//				return extractStueckeLiederListe(rs);
//			}
//		}
//	}


	public List<LiederStueckeModel> getStueckeLiederListe(String filterstcktitel, String filterstckdicht, String filterstckthema, String filterstckwoli, String filterstckbib,
			String filtergesbuch) throws Exception
	{

		// String ActiveDbank = ValuesGlobals.ActiveDb;
		String sqlStatement = "";
		sqlStatement = "SELECT ls_id, ls_titel, ls_db AS ls_dbs,"
				+ " ls_a_dichter, ls_erfasst, ";
		System.out.print("\nFilter:\n" + filterstckthema + "\n" + filterstckwoli + "\n" + filterstckbib + "\n" + filtergesbuch + "\n");
		Integer auswahl = 0;
		if (!(filterstckthema.isEmpty()))
		{
			auswahl += 1;
		}
		if (!(filterstckwoli.isEmpty()))
		{
			auswahl += 2;
		}
		if (!(filterstckbib.isEmpty()))
		{
			auswahl += 4;
		}
		if (!(filtergesbuch.isEmpty()))
		{
			auswahl += 8;
		}
		System.out.print("\n\nAuswahl: " + auswahl);
		switch (auswahl) // Auswahl je nach Filterfüllung Wochenlied, Thema, Bibel, Gesangbuch
		{
		case 0: // alles leer
			sqlStatement += " 'th' AS lsth_th, 'wl' AS lswl_wl, 'bi' AS lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueck ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			break;
		case 1: // nur Thema
			sqlStatement += " lsth_th, 'wl' AS lswl_wl, 'bi' AS lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckThema INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lsth_th LIKE '" + filterstckthema + "%' ";
			sqlStatement += ";";
			break;
		case 2: // nur Wochenlied
			sqlStatement += " 'th' AS lsth_th, lswl_wl, 'bi' AS lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckWochenlied INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lswl_wl LIKE '" + filterstckwoli + "%' ";
			sqlStatement += ";";
			break;
		case 3: // Thema und Wochenlied
			sqlStatement += " lsth_th, lswl_wl, 'bi' AS lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckWochenlied INNER JOIN "
					+ " (tblPcndLiedstueckThema INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lswl_wl LIKE '" + filterstckwoli + "%' "
					+ " AND lsth_th LIKE '" + filterstckthema + "%' ";
			sqlStatement += ";";
			break;
		case 4: // nur Bibelstelle
			sqlStatement += " 'th' AS lsth_th, 'wl' AS lswl_wl, lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckBibel INNER JOIN "
					+ " tblPcndLiedstueck ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lsbi_bi LIKE '" + filterstckbib + "%' ";
			sqlStatement += ";";
			break;
		case 5: // Bibelstelle und Thema
			sqlStatement += " lsth_th, 'wl' AS lswl_wl, lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckBibel INNER JOIN (tblPcndLiedstueckThema "
					+ " INNER JOIN tblPcndLiedstueck ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lsth_th LIKE '" + filterstckthema + "%' "
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%' ";
			sqlStatement += ";";
			break;
		case 6: // Bibelstelle und Wochenlied
			sqlStatement += " 'th' AS lsth_th, lswl_wl, lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckBibel INNER JOIN (tblPcndLiedstueckWochenlied "
					+ " INNER JOIN tblPcndLiedstueck ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lswl_wl LIKE '" + filterstckwoli + "%' "
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%' ";
			sqlStatement += ";";
			break;
		case 7: // Bibelstelle und Wochenlied und Thema
			sqlStatement += " lsth_th, lswl_wl, lsbi_bi, 'gb' AS lsgb_g  "
					+ " FROM tblPcndLiedstueckBibel INNER JOIN (tblPcndLiedstueckWochenlied "
					+ " INNER JOIN (tblPcndLiedstueckThema INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lswl_wl LIKE '" + filterstckwoli + "%' "
					+ " AND lsth_th LIKE '" + filterstckthema + "%' "
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%'";
			sqlStatement += ";";
			break;
		case 8: // nur Gesangbuch
			sqlStatement += " 'th' AS lsth_th, 'wl' AS lswl_wl, 'bi' AS lsbi_bi, lsgb_g  "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%' ";
			sqlStatement += " AND lsgb_g LIKE '" + filtergesbuch + "%' ";
			sqlStatement += ";";
			break;
		case 9: // nur Gesangbuch und Thema
			sqlStatement += " lsth_th, 'wl' AS lswl_wl, 'bi' AS lsbi_bi, lsgb_g  "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN (tblPcndLiedstueckThema "
					+ " INNER JOIN tblPcndLiedstueck ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%' ";
			sqlStatement += " AND lsgb_g LIKE '" + filtergesbuch + "%'"
					+ " AND lsth_th LIKE '" + filterstckthema + "%'";
			sqlStatement += ";";
			break;
		case 10: // nur Gesangbuch und Wochenlied
			sqlStatement += " 'th' AS lsth_th, lswl_wl, 'bi' AS lsbi_bi, lsgb_g  "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN (tblPcndLiedstueckWochenlied "
					+ " INNER JOIN tblPcndLiedstueck ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%' ";
			sqlStatement += " AND lsgb_g LIKE '" + filtergesbuch + "%'"
					+ " AND lswl_wl LIKE '" + filterstckwoli + "%'";
			sqlStatement += ";";
			break;

		case 11: // nur Gesangbuch und Wochenlied und Thema
			sqlStatement += " lsth_th, lswl_wl, 'bi' AS lsbi_bi, lsgb_g  "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN (tblPcndLiedstueckWochenlied "
					+ " INNER JOIN (tblPcndLiedstueckThema INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%' ";
			sqlStatement += " AND lsgb_g LIKE '" + filtergesbuch + "%'"
					+ " AND lswl_wl LIKE '" + filterstckwoli + "%'"
					+ " AND lsth_th LIKE '" + filterstckthema + "%' ";
			sqlStatement += ";";
			break;
		case 12: // nur Gesangbuch und Bibel
			sqlStatement += " 'th' AS lsth_th, 'wl' AS lswl_wl, lsbi_bi, lsgb_g  "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN (tblPcndLiedstueckBibel "
					+ " INNER JOIN tblPcndLiedstueck ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%' ";
			sqlStatement += " AND lsgb_g LIKE '" + filtergesbuch + "%'"
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%'";
			sqlStatement += ";";
			break;

		case 13:// nur Gesangbuch und Bibel und Thema
			sqlStatement += " lsth_th, 'wl' AS lswl_wl, lsbi_bi, lsgb_g "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN (tblPcndLiedstueckBibel "
					+ " INNER JOIN (tblPcndLiedstueckThema INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";

			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lsth_th LIKE '" + filterstckthema + "%' "
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%' "
					+ " AND lsgb_g LIKE '" + filtergesbuch + "%' ";
			break;

		case 14:// nur Gesangbuch und Bibel und Woli
			sqlStatement += "'th' AS lsth_th, lswl_wl, lsbi_bi, lsgb_g "
					+ " FROM tblPcndLiedstueckGesangbuch INNER JOIN (tblPcndLiedstueckBibel "
					+ " INNER JOIN (tblPcndLiedstueckWochenlied "
					+ " INNER JOIN tblPcndLiedstueck ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lswl_wl LIKE '" + filterstckwoli + "%' "
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%' "
					+ " AND lsgb_g LIKE '" + filtergesbuch + "%' ";
			break;
		case 15:
			sqlStatement += " lsth_th, lswl_wl, lsbi_bi, lsgb_g "
					+ " FROM tblPcndLiedstueckGesangbuch"
					+ " INNER JOIN (tblPcndLiedstueckBibel INNER JOIN (tblPcndLiedstueckWochenlied"
					+ " INNER JOIN (tblPcndLiedstueckThema INNER JOIN tblPcndLiedstueck "
					+ " ON tblPcndLiedstueckThema.lsth_ls = tblPcndLiedstueck.ls_titel)"
					+ " ON tblPcndLiedstueckWochenlied.lswl_ls = tblPcndLiedstueck.ls_titel)"
					+ " ON tblPcndLiedstueckBibel.lsbi_ls = tblPcndLiedstueck.ls_titel) "
					+ " ON tblPcndLiedstueckGesangbuch.lsgb_ls = tblPcndLiedstueck.ls_titel ";
			sqlStatement += " WHERE ls_titel LIKE '" + filterstcktitel + "%' "
					+ " AND ls_a_dichter LIKE '" + filterstckdicht + "%'";
			sqlStatement += " AND lsth_th LIKE '" + filterstckthema + "%' "
					+ " AND lswl_wl LIKE '" + filterstckwoli + "%' "
					+ " AND lsbi_bi LIKE '" + filterstckbib + "%' "
					+ " AND lsgb_g LIKE '" + filtergesbuch + "%' ";
			break;
		}
		System.out.println(sqlStatement);
		this.setUpStatement(sqlStatement);
		this.runStatement();
		ResultSet rs = this.getResults();
		List<LiederStueckeModel> methodResult = extractStueckeLiederListe(rs);
		return methodResult;
	}


	private List<LiederStueckeModel> extractStueckeLiederListe(ResultSet rs) throws SQLException
	{
		List<LiederStueckeModel> result = new ArrayList<>();
		while (rs.next())
		{
			result.add(new LiederStueckeModel(
					rs.getInt("ls_id"),
					rs.getString("ls_dbs"),
					rs.getString("ls_titel"),
					rs.getString("ls_a_dichter"),
					rs.getString("lsth_th"),
					rs.getString("lswl_wl"),
					rs.getString("lsbi_bi"),
					rs.getString("lsgb_g"),
					rs.getString("ls_erfasst")));
		}
		return result;
	}

	// Neu und editieren
	public void setStckNeu(String stckdb, String stcktiteledit, String stckdichter) throws SQLException
	{
		String dsEditiert = new AktuellesDatum().getDateAsString();
		String sql = """
                INSERT INTO tblPcndLiedstueck (ls_id, ls_db, ls_titel, ls_a_dichter, ls_erfasst, user_modified)
                VALUES ((SELECT IFNULL(MAX(ls_id),0)+1 FROM tblPcndLiedstueck), ?, ?, ?, ?, 1)
                """;
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, stckdb);
			pstmt.setString(2, stcktiteledit);
			pstmt.setString(3, stckdichter);
			pstmt.setString(4, dsEditiert);
			pstmt.executeUpdate();
		}
	}

	public Integer getStckNeuerDs() throws Exception
	{// wenn die einfachen Filterkriterien eingetragen sind
		int id;
		String sqlStatement = "";
		sqlStatement = "SELECT MAX(ls_id) FROM tblPcndLiedstueck";
		System.out.println(sqlStatement);
		this.setUpStatement(sqlStatement);
		this.runStatement();
		ResultSet rs = this.getResults();
		id = rs.getInt(1);
		return id;
	}

	public void setStckEdit(String stckdb, String stcktitel, String stcktiteledit, String stckdichter) throws SQLException
	{
		String dsEditiert = new AktuellesDatum().getDateAsString();
		String sql = "UPDATE tblPcndLiedstueck SET ls_db = ?, ls_titel = ?, ls_a_dichter = ?, ls_erfasst = ?, user_modified =1 WHERE ls_titel = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, stckdb);
			pstmt.setString(2, stcktiteledit);
			pstmt.setString(3, stckdichter);
			pstmt.setString(4, dsEditiert);
			pstmt.setString(5, stcktitel);
			pstmt.executeUpdate();
		}
	}

	public String setStckLoeschen(String lstitel)
	{
		String sqlReturn = "";
		String sql = "DELETE FROM tblPcndLiedstueck WHERE ls_titel = ?";
		try (PreparedStatement pstmt = connection.prepareStatement("PRAGMA foreign_keys = ON"))
		{
			pstmt.execute();
		}
		catch (SQLException ignored)
		{
		}

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lstitel);
			pstmt.executeUpdate();
		}
		catch (SQLException e)
		{
			if (e.getErrorCode() == 19)
			{ // Foreign key constraint
				sqlReturn = "Fehler beim Löschen des Liedes/Stücks " + lstitel + "!\n" +
						"Es befinden sich noch Einträge zu diesem Lied/Stück in der Literaturtabelle.\n" +
						"Diese Datensätze müssen zuerst entfernt werden!";
			}
			else
			{
				e.printStackTrace();
			}
		}
		return sqlReturn;
	}

//#################################################################
//Zwischentabellen
	// Stücke-Themen (m:n)
	public List<LiederStueckeThemenModel> getStueckinfosTh(String lstitel) throws SQLException
	{
		String sql = "SELECT lsth_ls, lsth_th FROM tblPcndLiedstueckThema " +
				"WHERE lsth_ls = ? ORDER BY lsth_th COLLATE NOCASE";

		List<LiederStueckeThemenModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lstitel);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new LiederStueckeThemenModel(
							rs.getString("lsth_th"),
							rs.getString("lsth_ls")));
				}
			}
		}

		return result;
	}

	public void setLiedStckThemaNeu(String stthema, String ststueck) throws SQLException
	{
		String sql = "INSERT INTO tblPcndLiedstueckThema (lsth_ls, lsth_th) VALUES (?, ?)";
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, ststueck);
			pstmt.setString(2, stthema);
			pstmt.executeUpdate();
		}
	}

	public void setLiedStckThemaWegnehmen(String stthth, String stthls) throws SQLException
	{
		String sql = "DELETE FROM tblPcndLiedstueckThema WHERE lsth_th = ? AND lsth_ls = ?";
		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, stthth);
			pstmt.setString(2, stthls);
			pstmt.executeUpdate();
		}
	}

	// Stücke - Wochenlieder (m:n)
	public List<LiederStueckeWoliModel> getStueckinfosWoli(String lstitel) throws SQLException
	{
		String sql = "SELECT lswl_ls, lswl_wl, lswl_id FROM tblPcndLiedstueckWochenlied " +
				"WHERE lswl_ls = ? ORDER BY lswl_wl COLLATE NOCASE";

		List<LiederStueckeWoliModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lstitel);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new LiederStueckeWoliModel(
							rs.getString("lswl_wl"),
							rs.getString("lswl_ls"),
							rs.getInt("lswl_id")));
				}
			}
		}

		return result;
	}

	// Stücke- Gesangbücher (m:n)
	public List<LiederStueckeGesbModel> getStueckinfosGesb(String lstitel) throws SQLException
	{
		String sql = "SELECT lsgb_id, lsgb_ls, lsgb_g, lsgb_nr, lsgb_nrsort " +
				"FROM tblPcndLiedstueckGesangbuch " +
				"WHERE lsgb_ls = ? " +
				"ORDER BY lsgb_g, lsgb_nrsort COLLATE NOCASE";

		List<LiederStueckeGesbModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lstitel);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new LiederStueckeGesbModel(
							rs.getString("lsgb_g"),
							rs.getString("lsgb_nr"),
							rs.getInt("lsgb_nrsort"),
							rs.getString("lsgb_ls"),
							rs.getInt("lsgb_id")));
				}
			}
		}

		return result;
	}

	// Stücke - Bibel (m:n)
	public List<LiederStueckeBibModel> getStueckinfosBib(String lstitel) throws SQLException
	{
		String sql = """
	        SELECT lsbi_id, lsbi_ls, lsbi_bi, b_birang,
	               IFNULL(lsbi_bi, '') || ' ' || IFNULL(lsbi_versangabe, '') AS lsbi_versang,
	               lsbi_versangabe
	        FROM tblPcndLiedstueckBibel
	        INNER JOIN tblPcndBibel ON tblPcndBibel.b_buch = tblPcndLiedstueckBibel.lsbi_bi
	        WHERE lsbi_ls = ?
	        ORDER BY b_birang, lsbi_versang COLLATE NOCASE
	    """;

		List<LiederStueckeBibModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, lstitel);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new LiederStueckeBibModel(
							rs.getString("lsbi_ls"),
							rs.getString("lsbi_bi"),
							rs.getString("lsbi_versangabe")));
				}
			}
		}

		return result;
	}

//###############################################
	// Autorenliste - wird in Literatur und Lieder/Stücke benötigt

	public List<AutorenlisteModel> getAutorenlisteAlle() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndAutor " +
				"ORDER BY a_autor COLLATE NOCASE, a_nname COLLATE NOCASE, a_vname COLLATE NOCASE;";

		List<AutorenlisteModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new AutorenlisteModel(
						rs.getString("a_autor"),
						rs.getString("a_nname"),
						rs.getString("a_vname"),
						rs.getString("a_gjahr"),
						rs.getString("a_tjahr"),
						rs.getString("a_sonst"),
						rs.getString("a_db"),
						rs.getString("a_id")));
			}
		}

		return result;
	}

	// ###############################################################
	// -------------- einzelne Comboboxen
	// ========================================================================
	// 1️⃣ Combos: Wochenlieder / Themen / Themen-Stück
	// ========================================================================

	public List<WochenliedlisteModel> getWochenliedlisteListeAll() throws SQLException
	{
		String sql = "SELECT wl_bez, wl_rang, wl_db, wl_id FROM tblPcndWochenlied ORDER BY wl_rang COLLATE NOCASE";

		List<WochenliedlisteModel> result = new ArrayList<>();

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

	public List<ThemenlisteModel> getThemenListeAll() throws SQLException
	{
		String sql = "SELECT th_bez, th_db, th_id FROM tblPcndThema ORDER BY th_bez COLLATE NOCASE";

		List<ThemenlisteModel> result = new ArrayList<>();

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

	public List<ThemenlisteStueckModel> getThemenListeAll2(String liedstueck) throws SQLException
	{
		String sql = "SELECT lsth_th FROM tblPcndLiedstueckThema WHERE lsth_ls = ? ORDER BY lsth_th COLLATE NOCASE";

		List<ThemenlisteStueckModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql))
		{
			pstmt.setString(1, liedstueck);
			try (ResultSet rs = pstmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new ThemenlisteStueckModel(rs.getString("lsth_th")));
				}
			}
		}

		return result;
	}

	public List<EditionenlisteComboNaModel> getEditionenListeNaCombo() throws SQLException
	{
		String sql = "SELECT e_lt FROM tblPcndEdition " +
				"ORDER BY e_lt COLLATE NOCASE;";

		List<EditionenlisteComboNaModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new EditionenlisteComboNaModel(rs.getString("e_lt")));
			}
		}

		return result;
	}

	public List<LiederStueckeComboModel> getStueckLiedAll() throws SQLException
	{
		String sql = "SELECT ls_titel FROM tblPcndLiedstueck;";

		List<LiederStueckeComboModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new LiederStueckeComboModel(rs.getString("ls_titel")));
			}
		}

		return result;
	}

	// ========================================================================
	// 2️⃣ Bibel
	// ========================================================================

	public List<BibellisteModel> getBibelListeAll() throws SQLException
	{
		String sql = "SELECT b_buch, b_kuerzel, b_birang, b_db FROM tblPcndBibel WHERE b_birang > 0 ORDER BY b_birang";

		List<BibellisteModel> result = new ArrayList<>();

		try (PreparedStatement pstmt = connection.prepareStatement(sql);
				ResultSet rs = pstmt.executeQuery())
		{

			while (rs.next())
			{
				result.add(new BibellisteModel(
						rs.getString("b_buch"),
						rs.getString("b_kuerzel"),
						rs.getString("b_birang"),
						rs.getString("b_db")));
			}
		}

		return result;
	}

	public List<LiederStueckeBibelModel> getBibellisteZugew(String liedstueck) throws SQLException
	{
		String sql = """
            SELECT lsbi_id, lsbi_ls, lsbi_bi, b_birang,
                   IFNULL(lsbi_bi, '') || ' ' || IFNULL(lsbi_versangabe, '') AS lsbi_versang,
                   lsbi_versangabe
            FROM tblPcndLiedstueckBibel
            INNER JOIN tblPcndBibel ON tblPcndBibel.b_buch = tblPcndLiedstueckBibel.lsbi_bi
            WHERE lsbi_ls = ?
            ORDER BY b_birang, lsbi_versang COLLATE NOCASE
            """;

		List<LiederStueckeBibelModel> result = new ArrayList<>();

		try (PreparedStatement stmt = connection.prepareStatement(sql))
		{
			stmt.setString(1, liedstueck);
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new LiederStueckeBibelModel(
							rs.getString("lsbi_versang"),
							rs.getString("lsbi_id"),
							rs.getString("lsbi_ls")));
				}
			}
		}

		return result;
	}

	// ========================================================================
	// 3️⃣ Gesangbuch
	// ========================================================================
	// Alle Gesangbücher
	public List<GesangbuchModel> getGesAll() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndGesangbuch ORDER BY g_bez COLLATE NOCASE";
		List<GesangbuchModel> result = new ArrayList<>();

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new GesangbuchModel(
						rs.getString("g_bez"),
						rs.getString("g_kurz"),
						rs.getString("g_bem"),
						rs.getString("g_db")));
			}
		}

		return result;
	}

	// Gesangbuchzuweisungen für ein Liedstück
	public List<LiederStueckeGesbModel> getGesbuchlisteZugew(String liedstueck) throws SQLException
	{
		String sql = """
            SELECT lsgb_id, lsgb_nr, lsgb_ls, lsgb_nrsort, lsgb_g,
                   IFNULL(lsgb_nr,'') || ' ' || IFNULL(lsgb_bem,'') AS lsgb_ang,
                   lsgb_bem
            FROM tblPcndLiedstueckGesangbuch
            WHERE lsgb_ls = ?
            ORDER BY lsgb_g, lsgb_nr COLLATE NOCASE
            """;

		List<LiederStueckeGesbModel> result = new ArrayList<>();
		try (PreparedStatement stmt = connection.prepareStatement(sql))
		{
			stmt.setString(1, liedstueck);
			try (ResultSet rs = stmt.executeQuery())
			{
				while (rs.next())
				{
					result.add(new LiederStueckeGesbModel(
							rs.getString("lsgb_g"),
							rs.getString("lsgb_nr"),
							rs.getInt("lsgb_nrsort"),
							rs.getString("lsgb_ls"),
							rs.getInt("lsgb_id")));
				}
			}
		}

		return result;
	}

	// ========================================================================
	// 4️⃣ Stückarten / Verlage / Notenmappen / Programmversionen
	// ========================================================================

	// Alle Stückarten
	public List<StueckartlisteModel> getStueckartListeAll() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndStueckart ORDER BY sar_bez COLLATE NOCASE";
		List<StueckartlisteModel> result = new ArrayList<>();

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

	// Alle Verlage
	public List<VerlaglisteModel> getVerlaglisteListeAlle() throws SQLException
	{
		String sql = "SELECT * FROM tblPcndVerlag ORDER BY v_verlag COLLATE NOCASE";
		List<VerlaglisteModel> result = new ArrayList<>();

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

	// ========================================================================
	// Notenmappen
	// ========================================================================

	// Alle Notenmappen
	public List<NotenmappeModel> getNomaAll() throws SQLException
	{
		String sql = "SELECT * FROM tblNotenmappe";
		List<NotenmappeModel> result = new ArrayList<>();

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

	// Alle Inhalte der Notenmappen
	public List<NotenmappeInhaltModel> getNomainhaltAlle() throws SQLException
	{
		String sql = "SELECT * FROM tblNotenmappeEdition";
		List<NotenmappeInhaltModel> result = new ArrayList<>();

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new NotenmappeInhaltModel(
						rs.getInt("nmed_id"),
						rs.getString("nmed_e_lt"),
						rs.getString("nmed_nm_bez"),
						rs.getString("nmed_bem"),
						rs.getString("nmed_lager1"),
						rs.getString("nmed_lager2"),
						rs.getString("nmed_lager3"),
						rs.getString("nmed_titelgrafik"),
						rs.getInt("nmed_anz")));
			}
		}

		return result;
	}
//#################### Sonstiges ###################################################
	// ========================================================================
	// Programmversionen
	// ========================================================================

	// Alle Versionen
	public List<ProgrammversionenModel> getVersionAlles() throws SQLException
	{
		String sql = "SELECT * FROM tblAdbVersion";
		List<ProgrammversionenModel> result = new ArrayList<>();

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			while (rs.next())
			{
				result.add(new ProgrammversionenModel(
						rs.getString("ver_nr"),
						rs.getString("ver_dat")));
			}
		}

		return result;
	}

	// Höchste Version
	public ProgrammversionenModel getHoechsteVersion() throws SQLException
	{
		String sql = "SELECT * FROM tblAdbVersion ORDER BY ver_nr DESC LIMIT 1";

		try (Statement stmt = connection.createStatement();
				ResultSet rs = stmt.executeQuery(sql))
		{
			if (rs.next())
			{
				return new ProgrammversionenModel(
						rs.getString("ver_nr"),
						rs.getString("ver_dat"));
			}
		}

		return null;
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
