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
package net.cadrian.clef.database.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.DatabasePropertyBean;

abstract class AbstractPropertyBean<T extends DatabasePropertyBean> extends AbstractBean<T>
		implements DatabasePropertyBean {

	private static final List<Long> EMPTY_LIST = Arrays.asList();

	private Collection<Long> properties = EMPTY_LIST;

	AbstractPropertyBean(final Long id) {
		super(id);
	}

	@Override
	public Collection<Long> getProperties() {
		return properties;
	}

	@Override
	public void setProperties(final Collection<Long> properties) {
		this.properties = properties == null ? EMPTY_LIST : properties;
	}

	@Override
	public Collection<T> read(final Connection cnx, final boolean onlyId) throws DatabaseException {
		final Collection<T> result = super.read(cnx, onlyId);

		final StringBuilder sql = new StringBuilder("SELECT property_id FROM ");
		final String tableName = getTableName();
		sql.append(tableName).append("_property");
		sql.append(" WHERE ").append(tableName).append("_id=?");

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			for (final T bean : result) {
				@SuppressWarnings("unchecked")
				final AbstractPropertyBean<T> propertyBean = (AbstractPropertyBean<T>) bean;
				final Set<Long> propertyIds = new TreeSet<>(); // force ascending ids => creation order
				ps.setLong(1, propertyBean.getId());
				try (ResultSet rs = ps.executeQuery()) {
					while (rs.next()) {
						propertyIds.add(rs.getLong("property_id"));
					}
				}
				propertyBean.setProperties(propertyIds);
			}
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
		return result;
	}

	@Override
	public Long save(final Connection cnx) throws DatabaseException {
		final Long result = super.save(cnx);

		deleteOldProperties(result, cnx);

		final Collection<Long> propertyIds = getProperties();
		if (!propertyIds.isEmpty()) {
			insertNewProperties(result, propertyIds, cnx);
		}

		return result;
	}

	private void deleteOldProperties(final Long id, final Connection cnx) throws DatabaseException {
		final String tableName = getTableName();
		final StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(tableName).append("_property");
		sql.append(" WHERE ").append(tableName).append("_id=?");

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			ps.setLong(1, id);
			ps.executeUpdate();
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	private void insertNewProperties(final Long id, final Collection<Long> propertyIds, final Connection cnx)
			throws DatabaseException {
		final String tableName = getTableName();
		final StringBuilder sql = new StringBuilder("INSERT INTO ");
		sql.append(tableName).append("_property (property_id, ").append(tableName).append("_id) VALUES (?, ?)");

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			ps.setLong(2, id);
			for (final Long propertyId : propertyIds) {
				ps.setLong(1, propertyId);
				ps.executeUpdate();
			}
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@Override
	public void delete(final Connection cnx) throws DatabaseException {
		super.delete(cnx);
		deleteOldProperties(getId(), cnx);
	}

}
