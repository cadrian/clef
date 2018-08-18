package net.cadrian.clef.database;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import org.junit.Test;

import net.cadrian.clef.database.DatabaseManager;

/**
 * Low-level database initialization test. Does not use the DatabaseBeans
 * framework.
 */
public class TestDatabaseInitialization extends AbstractDatabaseTestHarness {

	@Test
	public void testInitialization() throws Exception {
		final DatabaseManager mgr = getManager();
		readProperties(mgr, "1.0.0");
		incrementVersion(mgr);
		readProperties(mgr, "42");

		// check if second init keeps the database data
		readProperties(new DatabaseManager(getDataSource()), "42");
	}

	private void readProperties(final DatabaseManager mgr, final String expectedVersion) throws Exception {
		try (Connection cnx = mgr.getConnection();
				PreparedStatement ps = cnx.prepareStatement("SELECT * FROM property");
				ResultSet rs = ps.executeQuery()) {
			Assert.assertTrue(rs.next());
			final long id = rs.getLong("id");
			final String name = rs.getString("name");
			final String value = readClob("value", rs);
			Assert.assertFalse(rs.next());

			Assert.assertEquals(1L, id);
			Assert.assertEquals("VERSION", name);
			Assert.assertEquals(expectedVersion, value);
		}
	}

	private String readClob(String fieldName, ResultSet rs) throws SQLException, IOException {
		final String result;
		try (StringWriter w = new StringWriter()) {
			final Clob v = rs.getClob(fieldName);
			try (Reader r = v.getCharacterStream()) {
				final char[] cbuf = new char[4096];
				int n;
				while ((n = r.read(cbuf)) >= 0) {
					w.write(cbuf, 0, n);
				}
			}
			result = w.toString();
		}
		return result;
	}

	private void incrementVersion(final DatabaseManager mgr) throws Exception {
		try (Connection cnx = mgr.getConnection()) {
			cnx.setAutoCommit(false);
			try (PreparedStatement ps = cnx.prepareStatement("UPDATE property SET value=? WHERE name=?")) {
				final Clob value = cnx.createClob();
				value.setString(1, "42"); // new fancy version number

				ps.setClob(1, value);
				ps.setString(2, "VERSION");

				ps.execute();
			}

			cnx.commit();
		}
	}

}
