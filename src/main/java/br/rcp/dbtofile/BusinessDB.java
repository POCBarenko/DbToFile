/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.rcp.dbtofile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

/**
 <p/>
 @author barenko
 */
public class BusinessDB {

    private CharsetEncoder encoder;

    private Connection conn;

    private Properties props;

    public BusinessDB(Properties props) {
        this.props = props;
        encoder = Charset.forName(props.getProperty("encoding")).newEncoder();
    }

    protected void connect() throws SQLException {
        conn = DriverManager.getConnection(props.getProperty("dbUrl"), props);
    }

    public void loadData(String filename, DBParser parser) throws SQLException, IOException, ExecutionException, InterruptedException {
        connect();

        PreparedStatement stm = null;
        ResultSet rs = null;
        FileChannel fileChannel = new FileOutputStream(Paths.get(filename).toFile()).getChannel();

        try{
            stm = conn.prepareStatement(parser.sql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery();

            while(rs.next()){
                String line = parser.toStringRow(rs);
                fileChannel.write(encoder.encode(CharBuffer.wrap(line)));
            }
            fileChannel.close();
        }finally{
            if(rs != null)
                rs.close();
            if(stm != null)
                stm.close();
            if(fileChannel != null && fileChannel.isOpen())
                fileChannel.close();
            disconnect();
        }
    }

    protected void disconnect() throws SQLException {
        if(conn != null && !conn.isClosed())
            conn.close();
    }
}
