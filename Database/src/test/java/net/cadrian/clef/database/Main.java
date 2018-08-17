package net.cadrian.clef.database;

import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

	private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

	public static void main(final String[] args) throws Exception {
		final BasicDataSource dataSource = createDataSource();
		try {
			final DatabaseManager mgr = new DatabaseManager(dataSource);
			LOGGER.info("Database manager was created: {}", mgr);

			readProperties(mgr);
			incrementVersion(mgr);
			readProperties(mgr);

			// check if second init keeps the database data
			readProperties(new DatabaseManager(dataSource));
		} finally {
			dataSource.close();
		}
	}

	private static void readProperties(final DatabaseManager mgr) throws Exception {
		try (final Connection cnx = mgr.getConnection()) {
			try (final PreparedStatement ps = cnx.prepareStatement("SELECT * FROM property")) {
				try (final ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						final long id = rs.getLong("id");
						final String name = rs.getString("name");
						final String value;
						try (StringWriter w = new StringWriter()) {
							final Clob v = rs.getClob("value");
							try (Reader r = v.getCharacterStream()) {
								final char[] cbuf = new char[4096];
								int n;
								while ((n = r.read(cbuf)) >= 0) {
									w.write(cbuf, 0, n);
								}
							}
							value = w.toString();
						}
						LOGGER.info("#{} -- {}: {}", id, name, value);
					}
				}
			}
		}
	}

	private static void incrementVersion(final DatabaseManager mgr) throws Exception {
		try (final Connection cnx = mgr.getConnection()) {
			try (final PreparedStatement ps = cnx.prepareStatement("UPDATE property SET value=? WHERE name=?")) {
				final Clob value = cnx.createClob();
				value.setString(1, "42"); // new fancy version number

				ps.setClob(1, value);
				ps.setString(2, "VERSION");

				ps.execute();
			}

			cnx.commit();
		}
	}

	private static BasicDataSource createDataSource() {
		final BasicDataSource result = new BasicDataSource();
		result.setUrl("jdbc:h2:file:./target/db");
		result.setUsername("sa");
		result.setPassword("");
		return result;
	}

}
