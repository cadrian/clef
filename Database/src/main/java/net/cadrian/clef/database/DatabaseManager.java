package net.cadrian.clef.database;

import java.io.IOException;
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

	public DatabaseManager(final DataSource dataSource) throws DatabaseException {
		this.dataSource = dataSource;
		init();
	}

	private void init() throws DatabaseException {
		try (Connection cnx = getConnection()) {
			try (InputStream createSql = ClassLoader.getSystemResourceAsStream("database/create.sql")) {
				try (Reader createSqlReader = new InputStreamReader(createSql)) {
					RunScript.execute(cnx, createSqlReader);
				}
			}
			LOGGER.info("Database initialized: {}", cnx);
		} catch (SQLException | IOException e) {
			throw new DatabaseException(e);
		}
	}

	public <T extends DatabaseBean> DatabaseBeans<T> getDatabaseBeans(final Class<T> beanType)
			throws DatabaseException {
		return new DatabaseBeans<>(dataSource, beanType);
	}

	public Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	@Override
	public String toString() {
		return "Database Manager: " + dataSource;
	}

}
