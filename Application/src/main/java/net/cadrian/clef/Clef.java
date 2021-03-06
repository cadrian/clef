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
package net.cadrian.clef;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.DatabaseManager;
import net.cadrian.clef.database.model.ModelBeans;
import net.cadrian.clef.ui.app.Application;

public class Clef {

	private static final class ClefRunner implements Runnable {
		private final class ClefWindowListener extends WindowAdapter {
			@Override
			public void windowClosing(final WindowEvent ev) {
				try {
					ds.close();
				} catch (final SQLException e) {
					LOGGER.error("Error while closing datasource", e);
				} finally {
					LOGGER.info("Bye!");
				}
			}
		}

		private final BasicDataSource ds;
		private final DatabaseManager manager;

		private ClefRunner(final BasicDataSource ds, final DatabaseManager manager) {
			this.ds = ds;
			this.manager = manager;
		}

		@Override
		public void run() {
			final Application app = new Application(new ModelBeans(manager));
			LOGGER.info("Starting Clef.");

			app.addWindowListener(new ClefWindowListener());

			app.setSize(800, 600);
			app.setLocationRelativeTo(null);
			app.setVisible(true);
		}
	}

	private static final String PROPERTY_DB_PASSWORD = "db.password";
	private static final String PROPEERTY_DB_USERNAME = "db.username";
	private static final String PROPERTY_DB_PATH = "db.path";
	private static final Logger LOGGER = LoggerFactory.getLogger(Clef.class);

	public static void main(final String[] args) throws DatabaseException {
		System.setOut(new PrintStream(new OutLogger()));
		System.setErr(new PrintStream(new ErrLogger()));

		final BasicDataSource ds = createDataSource();
		final DatabaseManager manager = new DatabaseManager(ds);

		SwingUtilities.invokeLater(new ClefRunner(ds, manager));
	}

	private static BasicDataSource createDataSource() {
		LOGGER.debug("Loading properties");
		final Properties properties = new Properties();
		try {
			properties.load(Clef.class.getResourceAsStream("/clef.properties"));
		} catch (final IOException e) {
			LOGGER.warn("Failed to load properties", e);
		}

		final BasicDataSource result = new BasicDataSource();
		final File dbFile = new File(properties.getProperty(PROPERTY_DB_PATH, "./target/db"));
		final String dbPath = dbFile.getAbsolutePath();
		LOGGER.info("Database path: {}", dbPath);
		result.setUrl("jdbc:h2:file:" + dbPath);
		result.setUsername(properties.getProperty(PROPEERTY_DB_USERNAME, "sa"));
		result.setPassword(properties.getProperty(PROPERTY_DB_PASSWORD, ""));
		return result;
	}

}
