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
