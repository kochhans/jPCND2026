// 01 
// Basis: DatabaseControllerAbstract
// Das ist ein Interface oder eine abstrakte Definition.
package application.controllers;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface DatabaseControllerAbstract
{
	
	public void dbStop() throws SQLException;
	
	void setUpStatement(String statement);
	void setUpStatement() throws Exception;
	
	ResultSet runStatement() throws Exception;
	ResultSet getResults();
		
	public Connection dbStart(String dbPath) throws SQLException;
	public Connection dbStart(String dbPath, boolean enableForeignKeys) throws SQLException;
	Connection dbStart() throws SQLException;
    Connection getConnection();   // ← NEU	
    





	
}

