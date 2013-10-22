package br.rcp.dbtofile;

import br.rcp.dbtofile.DBParser;
import br.rcp.dbtofile.BusinessDB;

import java.io.File;
import java.math.BigDecimal;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 Unit test for simple App.
 */
public class AppTest {

    private static String dbUrl = "jdbc:h2:~/test";

    private static Connection conn = null;

    @BeforeClass
    public static void config() {
        try{
            conn = DriverManager.getConnection(dbUrl);
            conn.prepareStatement("create table tbl(id int, name text, address char(50), value real)").execute();

            PreparedStatement stm = conn.prepareStatement("insert into tbl(id,name,address,value)values(?,?,?,?)");
            for(int i = 0; i < 10000; i++){
                stm.setInt(1, i);
                stm.setString(2, "Nome do cara " + i);
                stm.setString(3, "Endereço do cara " + i);
                stm.setBigDecimal(4, new BigDecimal(Math.random() * i * 100));
                stm.addBatch();
            }
            stm.executeBatch();
        }catch(SQLException ex){
            Logger.getLogger(AppTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    @AfterClass
    public static void cleanup() throws SQLException {
        conn.prepareStatement("drop table tbl").execute();
    }

    @Test
    public void testApp() throws Exception {
        try{
            BusinessDBConfiguration config = new BusinessDBConfiguration()
            	.setDbUrl(dbUrl)
            	.setEncoding("UTF-8");
            
			BusinessDB b = new BusinessDB(config);

            long initAt = System.currentTimeMillis();
            File tempFile = File.createTempFile("test.output", ".txt");
            System.out.println("Escrevendo no arquivo "+tempFile.getAbsolutePath());
            b.loadData(tempFile.getAbsolutePath(), new DBParser() {
                @Override
                public String sql() {
                    return "select id,name,address,value from tbl";
                }

                @Override
                public String header() {
                	return "id\tname\taddress\tvalue\n";
                }
                
                @Override
                public String toStringRow(ResultSet rs) throws SQLException {
                    StringBuilder sb = new StringBuilder();
                    sb.append(rs.getInt(1)).append("\t");
                    sb.append(rs.getString(2)).append("\t");
                    sb.append(rs.getString(3)).append("\t");
                    sb.append(rs.getBigDecimal(4)).append("\n");
                    return sb.toString();
                }

				@Override
				public String footer() {
					return "";
				}
            });
            System.out.println("Tempo total de execução: " + (System.currentTimeMillis() - initAt) + " ms");
            Runtime.getRuntime().exec("open "+ tempFile.getAbsolutePath());
        }catch(SQLException ex){
            Logger.getLogger(AppTest.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
