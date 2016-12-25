package org.codebrother.jdbc.interfaces;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.codebrother.jdbc.entity.DBSource;

public interface ConnectionLib {
	
	Connection getConnection();
	Connection getConnection(DBSource dbSource);
}
