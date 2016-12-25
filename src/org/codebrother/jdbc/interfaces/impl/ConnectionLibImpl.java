package org.codebrother.jdbc.interfaces.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.codebrother.jdbc.entity.DBSource;
import org.codebrother.jdbc.interfaces.ConnectionLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class ConnectionLibImpl implements ConnectionLib {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Connection connection = null;
	private DBSource dbSource = null;
		
	public ConnectionLibImpl(DBSource dbSource) {
		this.dbSource = dbSource;
		getConnection(this.dbSource);
	}

	@Override
	public Connection getConnection() {
		return this.connection;
	}
	
	/**
	 * 获取数据库连接
	 * @return
	 */	
	@Override
	public Connection getConnection(DBSource dbSource) {
		try {
			Class.forName(dbSource.getDriveClass()); // 加载DB驱动
		} catch (ClassNotFoundException e) {
			logger.error("驱动加载错误");
		}
		try {
			String url = dbSource.getDbUrl();
			String user = dbSource.getDbUser();
			String password = dbSource.getDbPassword();
			logger.debug(url);
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
//			e.printStackTrace();
			logger.error(e.getMessage());
		} catch (NullPointerException e) {
			logger.error(e.getMessage());
		}
		logger.debug("已获取连接");
		// print internal state
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	    StatusPrinter.print(lc);
		return connection;
	}

}
