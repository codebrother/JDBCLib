package org.codebrother.jdbc.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.codebrother.jdbc.entity.DBSource;

public interface ConnectionLib {

	Connection getConnection();
	Connection getConnection(DBSource dbSource);
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
