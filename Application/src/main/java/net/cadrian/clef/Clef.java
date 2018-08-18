package net.cadrian.clef;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.SwingUtilities;

import org.apache.commons.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.DatabaseManager;
import net.cadrian.clef.database.model.DatabaseBeans;
import net.cadrian.clef.ui.Application;

public class Clef {

	private static final Logger LOGGER = LoggerFactory.getLogger(Clef.class);

	public static void main(String[] args) throws DatabaseException {
		final BasicDataSource ds = createDataSource();
		final DatabaseManager manager = new DatabaseManager(ds);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final Application app = new Application(new DatabaseBeans(manager));
				LOGGER.info("Starting Clef.");

				app.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent ev) {
						try {
							ds.close();
						} catch (SQLException e) {
							LOGGER.error("Error while closing datasource", e);
						}
					}
				});

				app.setVisible(true);
			}
		});
	}

	private static BasicDataSource createDataSource() {
		final BasicDataSource result = new BasicDataSource();
		result.setUrl("jdbc:h2:file:./target/db"); // TODO conf
		result.setUsername("sa");
		result.setPassword("");
		return result;
	}

}
