/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
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

import net.cadrian.clef.database.bean.Property;
import net.cadrian.clef.database.bean.PropertyDescriptor;

public class DatabaseManager {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseManager.class);

	private final DataSource dataSource;

	public DatabaseManager(final DataSource dataSource) throws DatabaseException {
		this.dataSource = dataSource;
		init();
	}

	private void init() throws DatabaseException {
		try (Connection cnx = getConnection()) {
			LOGGER.info("Creating database...");
			try (InputStream createSql = ClassLoader.getSystemResourceAsStream("database/create.sql")) {
				try (Reader createSqlReader = new InputStreamReader(createSql)) {
					RunScript.execute(cnx, createSqlReader);
				}
			}

			boolean done = false;
			do {
				final String version = getVersion();
				LOGGER.info("Current database version is {}, trying to update...", version);
				try (InputStream updateSql = ClassLoader
						.getSystemResourceAsStream("database/from-version-" + version + ".sql")) {
					if (updateSql == null) {
						LOGGER.info("Version update script not found for version {} -- stopping update", version);
						done = true;
					} else {
						try (Reader updateSqlReader = new InputStreamReader(updateSql)) {
							RunScript.execute(cnx, updateSqlReader);
						}
						final String newVersion = getVersion();
						if (newVersion.equals(version)) {
							LOGGER.warn("Version did not change!! ({}) -- stopping update", newVersion);
							done = true;
						}
					}
				} catch (final IOException e) {
					LOGGER.info("Version update script not found for version {} -- stopping update", version, e);
					done = true;
				}
			} while (!done);
		} catch (SQLException | IOException e) {
			throw new DatabaseException(e);
		}

		LOGGER.info("Database initialized (version {})", getVersion());
	}

	private String getVersion() throws DatabaseException {
		final DatabaseBeans<PropertyDescriptor> propertyDescriptors = new DatabaseBeans<>(dataSource,
				PropertyDescriptor.class);
		final DatabaseBeans<Property> properties = new DatabaseBeans<>(dataSource, Property.class);

		final PropertyDescriptor templateVersionDescriptor = new PropertyDescriptor();
		templateVersionDescriptor.setEntity("meta");
		templateVersionDescriptor.setName("VERSION");

		final PropertyDescriptor versionPropertyDescriptor = propertyDescriptors.readOne(templateVersionDescriptor);

		final Property templateVersion = new Property();
		templateVersion.setPropertyDescriptorId(versionPropertyDescriptor.getId());
		final Property versionProperty = properties.readOne(templateVersion);

		final String version = versionProperty.getValue();
		return version;
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
