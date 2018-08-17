package net.cadrian.clef.database;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.h2.tools.RunScript;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

	private final DataSource dataSource;

	public DatabaseManager(final DataSource dataSource) throws Exception {
		this.dataSource = dataSource;
		init();
	}

	private void init() throws Exception {
		try (final Connection cnx = getConnection()) {
			try (final InputStream createSql = ClassLoader.getSystemResourceAsStream("database/create.sql")) {
				try (final Reader createSqlReader = new InputStreamReader(createSql)) {
					RunScript.execute(cnx, createSqlReader);
				}
			}
			LOGGER.info("Database initialized: {}", cnx);
		}
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public String toString() {
		return "Database Manager: " + dataSource;
	}

}
