package net.cadrian.clef;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.SwingUtilities;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.DatabaseManager;
import net.cadrian.clef.database.model.ModelBeans;
import net.cadrian.clef.ui.Application;

public class Clef {

	private static final String PROPERTY_DB_PASSWORD = "db.password";
	private static final String PROPEERTY_DB_USERNAME = "db.username";
	private static final String PROPERTY_DB_PATH = "db.path";
	private static final Logger LOGGER = LoggerFactory.getLogger(Clef.class);

	public static void main(String[] args) throws DatabaseException {
		final BasicDataSource ds = createDataSource();
		final DatabaseManager manager = new DatabaseManager(ds);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Application app = new Application(new ModelBeans(manager));
				LOGGER.info("Starting Clef.");

				app.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent ev) {
						try {
							ds.close();
						} catch (SQLException e) {
							LOGGER.error("Error while closing datasource", e);
						} finally {
							LOGGER.info("Bye!");
						}
					}
				});

				app.setVisible(true);
			}
		});
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
		final String dbPath = properties.getProperty(PROPERTY_DB_PATH, "./target/db");
		LOGGER.info("Database path: {}", dbPath);
		result.setUrl("jdbc:h2:file:" + dbPath);
		result.setUsername(properties.getProperty(PROPEERTY_DB_USERNAME, "sa"));
		result.setPassword(properties.getProperty(PROPERTY_DB_PASSWORD, ""));
		return result;
	}

}
