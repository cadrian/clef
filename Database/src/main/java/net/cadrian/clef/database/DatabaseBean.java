package net.cadrian.clef.database;

import java.sql.Connection;
import java.util.Collection;

public interface DatabaseBean {

	Long getId();

	/**
	 * Use the current object as template to count the data. More efficient than
	 * <code>read(dataSource).length</code> :-)
	 *
	 * @param cnx
	 * @return count
	 * @throws DatabaseException
	 */
	int count(Connection cnx) throws DatabaseException;

	/**
	 * Use the current object as template to find the data
	 *
	 * @param cnx
	 * @param onlyId if true, the resulting objects will only have their id set.
	 * @return a list of objects matching the template; never null
	 * @throws DatabaseException
	 */
	Collection<? extends DatabaseBean> read(Connection cnx, boolean onlyId) throws DatabaseException;

	/**
	 * Insert or update
	 *
	 * @param cnx
	 * @return the object id (interesting for insert)
	 * @throws DatabaseException
	 */
	Long save(Connection cnx) throws DatabaseException;

	/**
	 * Delete the object. The id must be set. In this case the object is stale and
	 * should not be referenced anymore.
	 *
	 * @param cnx
	 * @throws DatabaseException
	 */
	void delete(Connection cnx) throws DatabaseException;

}
