package br.rcp.dbtofile;

import java.util.Properties;

public class BusinessDBConfiguration {
	private String encoding = "UTF-8";
	private String dbUrl = "";
	private Properties dbProps = new Properties();

	public String getEncoding() {
		return encoding;
	}

	public BusinessDBConfiguration setEncoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public String getDbUrl() {
		return dbUrl;
	}

	public BusinessDBConfiguration setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
		return this;
	}

	public Properties getDbProps() {
		return dbProps;
	}

	public BusinessDBConfiguration setDbProps(Properties dbProps) {
		this.dbProps = dbProps;
		return this;
	}
}
