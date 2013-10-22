/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.rcp.dbtofile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.CharacterCodingException;
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

    private BusinessDBConfiguration config;

    public BusinessDB(BusinessDBConfiguration config) {
        this.config = config;
        encoder = Charset.forName(config.getEncoding()).newEncoder();
    }

    protected void connect() throws SQLException {
        conn = DriverManager.getConnection(config.getDbUrl(), config.getDbProps());
    }

    public void loadData(String filename, DBParser parser) throws SQLException, IOException, ExecutionException, InterruptedException {
        connect();

        PreparedStatement stm = null;
        ResultSet rs = null;
        FileOutputStream fileOutputStream = null;
		FileChannel fileChannel = null;

        try{
        	fileOutputStream = new FileOutputStream(Paths.get(filename).toFile());
        	fileChannel = fileOutputStream.getChannel();
        	
            stm = conn.prepareStatement(parser.sql(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery();

            write(fileChannel, parser.header());
            while(rs.next()){
                write(fileChannel, parser.toStringRow(rs));
            }
            write(fileChannel, parser.footer());
            fileChannel.close();
        }finally{
            if(rs != null)
                rs.close();
            if(stm != null)
                stm.close();
            if(fileChannel != null && fileChannel.isOpen())
                fileChannel.close();
            if(fileOutputStream != null)
            	fileOutputStream.close();
            disconnect();
        }
    }

	private void write(FileChannel fileChannel, String content) throws IOException, CharacterCodingException {
		fileChannel.write(encoder.encode(CharBuffer.wrap(content)));
	}

    protected void disconnect() throws SQLException {
        if(conn != null && !conn.isClosed())
            conn.close();
    }
}
