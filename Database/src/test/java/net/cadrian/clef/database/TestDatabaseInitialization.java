/*
 * This file is part of Clef.
 *
 * Clef is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3 of the License.
 *
 * Clef is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Clef.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package net.cadrian.clef.database;

import static org.junit.Assert.assertNotNull;

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

/**
 * Low-level database initialization test. Does not use the DatabaseBeans
 * framework.
 */
public class TestDatabaseInitialization extends AbstractDatabaseTestHarness {

	@Test
	public void testInitialization() throws Exception {
		final DatabaseManager mgr = getManager();

		final Long versionPD = readPropertyDescriptor(mgr, "meta", "VERSION");
		assertNotNull(versionPD);

		readProperties(mgr, versionPD, "1.0.0");
		incrementVersion(mgr, versionPD);
		readProperties(mgr, versionPD, "42");

		// check if second init keeps the database data
		readProperties(new DatabaseManager(getDataSource()), versionPD, "42");
	}

	private Long readPropertyDescriptor(final DatabaseManager mgr, final String entity, final String name)
			throws Exception {
		final Long result;
		try (Connection cnx = mgr.getConnection();
				PreparedStatement ps = cnx
						.prepareStatement("SELECT id FROM property_descriptor WHERE entity=? and name=?")) {
			ps.setString(1, entity);
			ps.setString(2, name);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					result = rs.getLong("id");
				} else {
					result = null;
				}
			}
		}
		return result;
	}

	private void readProperties(final DatabaseManager mgr, final long versionPD, final String expectedVersion)
			throws Exception {
		try (Connection cnx = mgr.getConnection();
				PreparedStatement ps = cnx.prepareStatement("SELECT * FROM property");
				ResultSet rs = ps.executeQuery()) {
			Assert.assertTrue(rs.next());
			final long id = rs.getLong("id");
			final long pdId = rs.getLong("property_descriptor_id");
			final String value = readClob("value", rs);
			Assert.assertFalse(rs.next());

			Assert.assertEquals(1L, id);
			Assert.assertEquals(versionPD, pdId);
			Assert.assertEquals(expectedVersion, value);
		}
	}

	private String readClob(final String fieldName, final ResultSet rs) throws SQLException, IOException {
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

	private void incrementVersion(final DatabaseManager mgr, final long versionPD) throws Exception {
		try (Connection cnx = mgr.getConnection()) {
			cnx.setAutoCommit(false);
			try (PreparedStatement ps = cnx
					.prepareStatement("UPDATE property SET value=? WHERE property_descriptor_id=?")) {
				final Clob value = cnx.createClob();
				value.setString(1, "42"); // new fancy version number

				ps.setClob(1, value);
				ps.setLong(2, versionPD);

				ps.execute();
			}

			cnx.commit();
		}
	}

}
