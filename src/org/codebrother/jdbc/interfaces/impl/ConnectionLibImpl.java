package org.codebrother.jdbc.interfaces.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.codebrother.jdbc.entity.DBSource;
import org.codebrother.jdbc.interfaces.ConnectionLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class ConnectionLibImpl implements ConnectionLib {
	
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private Connection connection = null;

	/**
	 * ��ȡ���ݿ�����
	 * @return
	 */
	@Override
	public Connection getConnection() {
		try {
			Class.forName("com.mysql.jdbc.Driver"); // ����mysql����
		} catch (ClassNotFoundException e) {
			logger.error("�������ش���");
//			e.printStackTrace();
		}
		try {
			String url = "jdbc:mysql://localhost:3306/pos?useSSL=true";
			String user = "root";
			String password = "root";
			connection = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		logger.debug("�ѻ�ȡ����");
		return connection;
	}
	
	@Override
	public Connection getConnection(DBSource dbSource) {
		try {
			Class.forName(dbSource.getDriveClass()); // ����DB����
		} catch (ClassNotFoundException e) {
			logger.debug("�������ش���");
//			e.printStackTrace();
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
		logger.debug("�ѻ�ȡ����");
		// print internal state
	    LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
	    StatusPrinter.print(lc);
		return connection;
	}
	
	/**
	 * ����SQL��ѯ���
	 * @param sql
	 * @param object
	 * @return
	 */
	@Override
	public ResultSet executeQuery(String sql, Object[] object) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(sql);
			if(null != object) {
				for(int i=0; i<object.length; i++) {
					ps.setObject(i+1, object[i]);
				}				
			}
			rs = ps.executeQuery();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
//			close(rs, ps, connection);
		}
		return rs;
	}
	
	/**
	 * ����SQL���½��
	 * @param sql
	 * @param object
	 * @return
	 */
	@Override
	public int executeUpdate(String sql, Object[] object) {
		int result = 0;
		PreparedStatement ps = null;
		try {
			connection = getConnection();
			ps = connection.prepareStatement(sql);
			if(null != object) {
				for(int i=0; i<object.length; i++) {
					ps.setObject(i+1, object[i]);
				}
			}
			result = ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(ps, connection);
		}
		return result;
	}
	
	/**
	 * �ͷ����ݿ�����
	 * @param ps
	 */
	@Override
	public void close(PreparedStatement ps) {
		if (ps != null) {
			try {
				ps.close();
				ps = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * �ͷ����ݿ�����
	 * @param stmt
	 */
	@Override
	public void close(Statement stmt) {
		if (stmt != null) {
			try {
				stmt.close();
				stmt = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}	
	/**
	 * �ͷ����ݿ�����
	 * @param rs
	 */
	@Override
	public void close(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
				rs = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}


	@Override
	public void close(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
				connection = null;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void close(PreparedStatement ps, Connection connection) {
		close(ps);
		close(connection);
	}

	@Override
	public void close(Statement stmt, Connection connection) {
		close(stmt);
		close(connection);		
	}

	@Override
	public void close(ResultSet rs, PreparedStatement ps, Connection connection) {
		close(rs);
		close(ps);
		close(connection);		
	}

	@Override
	public void close(ResultSet rs, Statement stmt, Connection connection) {
		close(rs);
		close(stmt);
		close(connection);
	}
	
	public static void main(String[] args) {
//		new ConnectionLibImpl().getConnection(new ETLServiceImpl().readFromFile("/source.properties"));
//		new ConnectionLibImpl().getConnection(new ETLServiceImpl().readFromFile("/source_mysql.properties"));
//		new ConnectionLibImpl().getConnection();
	}

}
