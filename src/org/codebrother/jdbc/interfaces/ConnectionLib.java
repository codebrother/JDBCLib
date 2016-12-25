package org.codebrother.jdbc.interfaces;

import java.sql.Connection;

import org.codebrother.jdbc.entity.DBSource;

public interface ConnectionLib {
	
	Connection getConnection();
	Connection getConnection(DBSource dbSource);
}
