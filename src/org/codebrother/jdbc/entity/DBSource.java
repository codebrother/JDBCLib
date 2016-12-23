package org.codebrother.jdbc.entity;

public class DBSource {

	private String id;
	private String driveClass;
	private String dbIP;
	private String dbPort;
	private String dbSid;
	private String dbUrl;
	private String dbUser;
	private String dbPassword;
	private String sql;
	private String remark;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDriveClass() {
		return driveClass;
	}
	public void setDriveClass(String driveClass) {
		this.driveClass = driveClass;
	}
	public String getDbIP() {
		return dbIP;
	}
	public void setDbIP(String dbIP) {
		this.dbIP = dbIP;
	}
	public String getDbPort() {
		return dbPort;
	}
	public void setDbPort(String dbPort) {
		this.dbPort = dbPort;
	}
	public String getDbSid() {
		return dbSid;
	}
	public void setDbSid(String dbSid) {
		this.dbSid = dbSid;
	}
	public String getDbUrl() {
		return dbUrl;
	}
	public void setDbUrl(String dbUrl) {
		this.dbUrl = dbUrl;
	}
	public String getDbUser() {
		return dbUser;
	}
	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getSql() {
		return sql;
	}
	public void setSql(String sql) {
		this.sql = sql;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String toString() {
		return "id:" + id +"\ndriveClass:" + driveClass +"\ndbUrl:" + dbUrl +"\ndbUser:" + dbUser +"\ndbPassword:" + dbPassword +"\n"; 
	}
	
}
