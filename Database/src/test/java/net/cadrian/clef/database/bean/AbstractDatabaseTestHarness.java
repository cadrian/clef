package net.cadrian.clef.database.bean;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.DatabaseManager;

public abstract class AbstractDatabaseTestHarness {

	private BasicDataSource dataSource;
	private DatabaseManager manager;

	@Before
	public void setup() throws DatabaseException {
		dataSource = createDataSource();
		manager = new DatabaseManager(dataSource);
	}

	@After
	public void tearup() throws DatabaseException {
		if (dataSource != null) {
			try {
				dataSource.close();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			} finally {
				dataSource = null;
			}
		}
	}

	private static BasicDataSource createDataSource() {
		final BasicDataSource result = new BasicDataSource();
		result.setUrl("jdbc:h2:mem:test");
		result.setUsername("sa");
		result.setPassword("");
		return result;
	}

	protected DataSource getDataSource() {
		return dataSource;
	}

	protected DatabaseManager getManager() {
		return manager;
	}

}
