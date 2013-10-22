/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.rcp.dbtofile;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * <p/>
 * 
 * @author barenko
 */
public interface DBParser {

	String sql();
	
	String header();

	String toStringRow(ResultSet rs) throws SQLException;

	String footer();
}
