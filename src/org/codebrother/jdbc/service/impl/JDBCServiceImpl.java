package org.codebrother.jdbc.service.impl;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import org.codebrother.jdbc.entity.DBSource;
import org.codebrother.jdbc.interfaces.impl.ConnectionLibImpl;
import org.codebrother.jdbc.service.JDBCService;

public class JDBCServiceImpl implements JDBCService {
	
	private DBSource dbSource;
	private Connection connection;
	private ConnectionLibImpl connectionLibImpl;
	
	public ConnectionLibImpl getConnectionLibImpl() {
		return connectionLibImpl;
	}
	public void setConnectionLibImpl(ConnectionLibImpl connectionLibImpl) {
		this.connectionLibImpl = connectionLibImpl;
	}
	
	public DBSource getDbSource() {
		return dbSource;
	}
	public void setDbSource(DBSource dbSource) {
		this.dbSource = dbSource;
	}
	
	public JDBCServiceImpl() {
		
	}
	
	public JDBCServiceImpl(String file) {
		this.dbSource = getInstance(file);
		this.connection = new ConnectionLibImpl(this.dbSource).getConnection();
	}
	
	private DBSource getInstance(String dbFile) {
		if (null == dbSource) {
			this.dbSource = readFromFile(dbFile);
		}
		return this.dbSource;
	}
	
	@Override
	public DBSource readFromFile(String file) {
		DBSource dbSource = new DBSource();
		Properties prop = new Properties();
		System.out.println(file);
		try {
			prop.load(getClass().getResourceAsStream(file));
		} catch (IOException e) {
//			e.printStackTrace();
		}
		dbSource.setId(prop.getProperty("id", "null"));
		dbSource.setDriveClass(prop.getProperty("DriverClass"));
		if (dbSource.getDriveClass().toLowerCase().contains("mysql")) {
			dbSource.setDbUrl(prop.getProperty("url") + prop.getProperty("ip")+ ":" + prop.getProperty("port", "3306") + "/" + prop.getProperty("sid", "orcl"));			
		} else if (dbSource.getDriveClass().toLowerCase().contains("oracle")) {
			dbSource.setDbUrl(prop.getProperty("url") + prop.getProperty("ip")+ ":" + prop.getProperty("port", "1521") + ":" + prop.getProperty("sid", "orcl"));			
		}
		dbSource.setDbUser(prop.getProperty("user"));
		dbSource.setDbPassword(prop.getProperty("password"));
		dbSource.setRemark(prop.getProperty("remark", "no remark"));
		this.dbSource = dbSource;
		return this.dbSource;
	}
	
	@Override
	public DBSource getConnectionByFile(String dbFile) {
		return null;
	}

	@Override
	public DBSource getConnectionByPath(String dbPath) {
		return null;
	}

	@Override
	public DBSource getConnection(List<String> dbFiles) {
		return null;
	}
	
	@Override
	public int count(String sql) {
		int count = 0;
		ResultSet rs = executeQuery(sql, null);
		try {
			while(rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	@Override
	public int insert(String sql) {
		return executeUpdate(sql, null);
	}

	@Override
	public int delete(String sql) {
		return executeUpdate(sql, null);
	}

	@Override
	public int update(String sql) {
		return executeUpdate(sql, null);
	}

	@Override
	public int select(String sql) {
		int count = 0;
		ResultSet rs = executeQuery(sql, null);
		try {
			while(rs.next()) {
				count++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return count;
	}

	@Override
	public int truncate(String tableName) {
		String sql = "truncate table " + tableName;
		int n = executeUpdate(sql, null);
		return n;
	}

	@Override
	public int transfer(String sour, String sour_sql, Object[] obj_sour, String dest, String dest_sql) {
		ResultSet rs = executeQuery(readFromFile(sour), sour_sql, obj_sour);
		try {
			byte count = (byte)(dest_sql.split("\\?").length - 1);
			Object[] obj = new Object[count];
			while(rs.next()) {
				for(byte i=0; i<count; i++) {
					obj[i] = rs.getObject(i+1);
				}
				executeUpdate(readFromFile(dest), dest_sql, obj);
			}
			obj = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * 返回SQL查询结果
	 * @param sql
	 * @param object
	 * @return
	 */
	@Override
	public ResultSet executeQuery(String sql, Object[] object) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			ps = this.connection.prepareStatement(sql);
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
	 * 返回SQL更新结果
	 * @param sql
	 * @param object
	 * @return
	 */
	@Override
	public int executeUpdate(String sql, Object[] object) {
		int result = 0;
		PreparedStatement ps = null;
		try {
			ps = this.connection.prepareStatement(sql);
			if(null != object) {
				for(int i=0; i<object.length; i++) {
					if(null == object[i]) {
						object[i] = "";
					}
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
	 * 释放数据库连接
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
	 * 释放数据库连接
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
	 * 释放数据库连接
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
	@Override
	public int executeUpdate(DBSource dbSource, String sql, Object[] object) {
		int result = 0;
		PreparedStatement ps = null;
		try {
			connection = new ConnectionLibImpl(dbSource).getConnection();
			ps = connection.prepareStatement(sql);
			if(null != object) {
				for(int i=0; i<object.length; i++) {
					if(null == object[i]) {
						object[i] = "";
					}
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
	@Override
	public ResultSet executeQuery(DBSource dbSource, String sql, Object[] object) {
		ResultSet rs = null;
		PreparedStatement ps = null;
		try {
			connection = new ConnectionLibImpl(dbSource).getConnection();
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
}
