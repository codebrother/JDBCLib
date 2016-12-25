package org.codebrother.jdbc.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.codebrother.jdbc.entity.DBSource;

public interface JDBCService {

	DBSource readFromFile(String file);
	DBSource getConnectionByFile(String dbFile);
	DBSource getConnectionByPath(String dbPath);
	DBSource getConnection(List<String> dbFiles);
	
	int count(String sql);
	int insert(String sql);
	int delete(String sql);
	int update(String sql);
	int select(String sql);
	int truncate(String tableName);
	int transfer(DBSource source, DBSource dest, String sql);
	
	int executeUpdate(String sql, Object[] object);
	ResultSet executeQuery(String sql, Object[] object);
	void close(ResultSet rs);
	void close(Statement stmt);
	void close(PreparedStatement ps);
	void close(Connection connection);
	void close(Statement stmt, Connection connection);
	void close(PreparedStatement ps, Connection connection);
	void close(ResultSet rs, Statement stmt, Connection connection);
	void close(ResultSet rs, PreparedStatement ps, Connection connection);
}
