package org.codebrother.jdbc.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.codebrother.jdbc.entity.DBSource;
import org.codebrother.jdbc.interfaces.impl.ConnectionLibImpl;
import org.codebrother.jdbc.service.JDBCService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JDBCServiceImpl implements JDBCService {
//	日志记录
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	private DBSource dbSource;
	private Connection connection;
	private ConnectionLibImpl connectionLibImpl;
//	数据源列表
	private List<String> dbArray = null;
	
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
	
	private void init() {
		this.dbArray = new ArrayList<String>();
	}
	
	public JDBCServiceImpl() {
		init();
		this.dbSource = getInstance("/dbKey.properties");
		this.connection = new ConnectionLibImpl(this.dbSource).getConnection();
	}
	
	public JDBCServiceImpl(String file) {
		init();
		this.dbSource = getInstance(file);
		if(null == dbSource) {
			logger.error("The database source is null.");
			return;
		}
		this.connection = new ConnectionLibImpl(this.dbSource).getConnection();
	}
	
	private DBSource getInstance(String dbFile) {
		if (null == dbSource) {
//			this.dbSource = readFromFile(dbFile);
			this.dbSource = getDBfile(dbFile);
		}
		return this.dbSource;
	}
	
	public void getDBfile() {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader("db.properties"));
			String line = "";
			while( null != (line = br.readLine()) ) {
				readFromFile(line);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void getDbfiles(String file) {
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
	
	public DBSource getDBfile(String file) {
		Properties prop = new Properties();
		try {
//			prop.load(getClass().getResourceAsStream(file));
			prop.load(new FileInputStream(new File(file)));
		} catch (IOException e) {
//			e.printStackTrace();
		}
		String dbValue = prop.getProperty("def_db");
		return readFromFile(dbValue);
	}
	
	@Override
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
				logger.error(e.getMessage());
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
				logger.error(e.getMessage());
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
				logger.error(e.getMessage());
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
				logger.error(e.getMessage());
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
	
	public void execute() {
//		如果数据源为null，则继续下一个数据源连接测试
		if(null == dbSource) {
			logger.error("the database source is null, return\n");
			return;
		}
//		如果数据源连接为null，则进行下一个数据源连接测试
		if(null == this.connection) {
			logger.error("session can not open, retry please.\n");
			return;
		}
		logger.info("session opened");
//		获取执行sql
		String sql = dbSource.getSql();
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = this.connection.prepareStatement(sql);
			rs = ps.executeQuery();
			logger.info("execute:" + sql + "===" + rs.next());
			logger.info("finished!");
		} catch (SQLException e) {
			logger.error(e.getMessage());
		} finally {
//			关闭数据源连接
			close(rs, ps, this.connection);
			logger.info("session closed\n");
		}
	}
}
