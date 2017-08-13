package org.codebrother.jdbc.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codebrother.jdbc.entity.DBSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestDBLink {
//	日志记录
	private Logger logger = LoggerFactory.getLogger(this.getClass());
//	数据源列表
	private List<String> dbArray = null;
//	构造方法，读取数据文件，进而初始化数据源列表
	public TestDBLink(String file) {
		init();
		getDbfile(file);
	}
	
	private void init() {
		this.dbArray = new ArrayList<String>();
	}

	public void getDbfile(String file) {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String line = null;
//			循环读取文件，并加入到集合
			while ( null != (line = br.readLine()) ) {
				this.dbArray.add(line);
			}
		} catch (FileNotFoundException e) {
			logger.error("The file " + file + " is not found.");
			logger.error(e.getMessage());
		} catch (IOException e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if(null != br) {
					br.close();
					br = null;
				}
			} catch (IOException e) {
				logger.error(e.getMessage());
			}
		}
	}
/**
 * 
 * @param file
 * @return
 */
	public DBSource readFromFile(String file) {
		DBSource dbSource = new DBSource();
		Properties prop = new Properties();
		try {
//			prop.load(getClass().getResourceAsStream(file));
			if( !new File(file).exists() ) {
				logger.error("error");
				return null;
			}
			prop.load(new FileInputStream(new File(file)));
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		dbSource.setId(prop.getProperty("id", "null"));
		dbSource.setDriveClass(prop.getProperty("DriverClass"));
		if (dbSource.getDriveClass().toLowerCase().contains("mysql")) {
			dbSource.setDbUrl(prop.getProperty("url") 
					+ prop.getProperty("ip")
					+ ":" 
					+ prop.getProperty("port", "3306")
					+ "/"
					+ prop.getProperty("sid", "orcl"));
		} else if (dbSource.getDriveClass().toLowerCase().contains("oracle")) {
			dbSource.setDbUrl(prop.getProperty("url") 
					+ prop.getProperty("ip")
					+ ":" 
					+ prop.getProperty("port", "1521") 
					+ ":"
					+ prop.getProperty("sid", "orcl"));
		} else {
			logger.error("The database source file: " + file + " is not be supported");
			return null;
		}
		dbSource.setDbUser(prop.getProperty("user"));
		dbSource.setDbPassword(prop.getProperty("password"));
		dbSource.setSql(prop.getProperty("sql", "select 1 from dual"));
		dbSource.setRemark(prop.getProperty("remark", "no remark"));
		return dbSource;
	}

	public Connection getConnection(DBSource dbSource) {
		Connection conn = null;
		try {
			Class.forName(dbSource.getDriveClass()); // 加载DB驱动
		} catch (ClassNotFoundException e) {
			logger.debug("驱动加载错误");
		}
		try {
			String url = dbSource.getDbUrl();
			String user = dbSource.getDbUser();
			String password = dbSource.getDbPassword();
			logger.debug(url);
			conn = DriverManager.getConnection(url, user, password);
			logger.debug("已获取连接");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		} finally {
			if(null == conn) {
				logger.error("Cann't link the database source");
			}
		}
		// print internal state
//		LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//		StatusPrinter.print(lc);
		return conn;
	}

	public void execute(DBSource dbSource) {
//		如果数据源为null，则继续下一个数据源连接测试
		if(null == dbSource) {
			logger.error("the database source is null, return\n");
			return;
		}
		Connection conn = getConnection(dbSource);
//		如果数据源连接为null，则进行下一个数据源连接测试
		if(null == conn) {
			logger.error("session can not open, retry please.\n");
			return;
		}
		logger.info("session opened");
//		获取执行sql
		String sql = dbSource.getSql();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(sql);
			rs = ps.executeQuery();
			logger.info("execute:" + sql + "===" + rs.next());
			logger.info("finished!");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
//			逐一关闭数据源连接
			try {
				if(null != rs) {
					rs.close();
					rs = null;
				}
				if(null != ps) {
					ps.close();
					ps = null;
				}
				if(null != conn) {
					conn.close();
					conn = null;
				}
				logger.info("session closed\n");
			} catch (SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}
	
	public void test() {
//		如果数据源文件为空，则提示信息，最终退出。
		if(this.dbArray.isEmpty()) {
			logger.error("No database source found,exit.");
			return;
		}
		for (int i = 0; i < dbArray.size(); i++) {
			DBSource dbSource = readFromFile(dbArray.get(i));
			execute(dbSource);
		}
	}

	public static void main(String[] args) {
		new TestDBLink("db.properties").test();
	}
}
