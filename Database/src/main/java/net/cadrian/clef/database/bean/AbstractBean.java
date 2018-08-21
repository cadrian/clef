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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.io.Field;

abstract class AbstractBean<T extends DatabaseBean> implements DatabaseBean {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final Long id;

	AbstractBean(final Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	abstract List<Field<T>> getFields();

	abstract String getTableName();

	abstract T createBean(Long id);

	@Override
	public int count(final Connection cnx) throws DatabaseException {
		final int result;

		final StringBuilder sql = new StringBuilder("SELECT count(*) cnt FROM ");
		sql.append(getTableName());
		final String readWhere = getReadWhere();
		if (!readWhere.isEmpty()) {
			sql.append(" WHERE ").append(readWhere);
		}

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			setReadConditionValues(ps);
			try (ResultSet rs = ps.executeQuery()) {
				if (!rs.next()) {
					throw new DatabaseException("no count!");
				}
				result = rs.getInt("cnt");
			}
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}

		return result;
	}

	@Override
	public Collection<T> read(final Connection cnx, final boolean onlyId) throws DatabaseException {
		final List<T> result = new ArrayList<>();

		final StringBuilder sql = new StringBuilder();
		if (onlyId) {
			sql.append("SELECT id FROM ");
		} else {
			sql.append("SELECT * FROM ");
		}
		sql.append(getTableName());
		final String readWhere = getReadWhere();
		if (!readWhere.isEmpty()) {
			sql.append(" WHERE ").append(readWhere);
		}

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			setReadConditionValues(ps);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					final Long id = rs.getLong("id");
					final T newBean = createBean(id);
					if (!onlyId) {
						for (final Field<T> field : getFields()) {
							field.setValue(rs, newBean);
						}
					}
					result.add(newBean);
				}
			}
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}

		return result;
	}

	private String getReadWhere() throws DatabaseException {
		final StringBuilder result = new StringBuilder();
		if (getId() != null) {
			result.append("id = ?");
		}
		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;
		for (final Field<T> field : getFields()) {
			if (field.hasCondition(thisBean)) {
				if (result.length() > 0) {
					result.append(" AND ");
				}
				result.append(field.getCondition(thisBean));
			}
		}
		return result.toString();
	}

	private void setReadConditionValues(final PreparedStatement ps) throws SQLException, DatabaseException {
		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;
		int index;
		if (getId() == null) {
			index = 1;
		} else {
			index = 2;
			ps.setLong(1, thisBean.getId());
		}
		for (final Field<T> field : getFields()) {
			if (field.hasCondition(thisBean)) {
				field.setConditionValue(index, thisBean, ps);
				index++;
			}
		}
	}

	@Override
	public Long save(final Connection cnx) throws DatabaseException {
		final Long result;
		final StringBuilder sql;

		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;

		final Long id = thisBean.getId();
		if (id == null) {
			sql = new StringBuilder("INSERT INTO ");
			sql.append(getTableName());
			sql.append(" (");
			sql.append(getNames());
			sql.append(") VALUES (");
			sql.append(getPlaceholders());
			sql.append(")");
		} else {
			sql = new StringBuilder("UPDATE ");
			sql.append(getTableName());
			sql.append(" SET ");
			sql.append(getSaveSet());
			sql.append(" WHERE id=?");
		}

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString(), new String[] { "id" })) {
			final int index = setSaveValues(ps);
			if (id != null) {
				ps.setLong(index, id);
			}
			ps.executeUpdate();
			if (id != null) {
				result = id;
			} else {
				try (ResultSet rs = ps.getGeneratedKeys()) {
					if (rs.next()) {
						result = rs.getLong(1);
					} else {
						throw new DatabaseException("Could not retrieve inserted id");
					}
				}
			}
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}

		return result;
	}

	private String getNames() throws DatabaseException {
		final StringBuilder result = new StringBuilder();
		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;
		for (final Field<T> field : getFields()) {
			if (field.hasCondition(thisBean)) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append(field.getName());
			}
		}
		return result.toString();
	}

	private String getPlaceholders() throws DatabaseException {
		final StringBuilder result = new StringBuilder();
		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;
		for (final Field<T> field : getFields()) {
			if (field.hasCondition(thisBean)) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append("?");
			}
		}
		return result.toString();
	}

	private String getSaveSet() throws DatabaseException {
		final StringBuilder result = new StringBuilder();
		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;
		for (final Field<T> field : getFields()) {
			if (field.hasCondition(thisBean)) {
				if (result.length() > 0) {
					result.append(",");
				}
				result.append(field.getName()).append("=?");
			}
		}
		return result.toString();
	}

	private int setSaveValues(final PreparedStatement ps) throws SQLException, DatabaseException {
		@SuppressWarnings("unchecked")
		final T thisBean = (T) this;
		int index = 1;
		for (final Field<T> field : getFields()) {
			if (field.hasCondition(thisBean)) {
				field.setConditionValue(index, thisBean, ps);
				index++;
			}
		}
		return index;
	}

	@Override
	public void delete(final Connection cnx) throws DatabaseException {
		final StringBuilder sql = new StringBuilder("DELETE FROM ");
		sql.append(getTableName());
		sql.append(" WHERE id=?");

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			ps.setLong(1, getId());
			ps.executeUpdate();
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		return id != null && id.equals(((AbstractBean<T>) obj).id);
	}

	@Override
	public int hashCode() {
		return id == null ? 0 : id.hashCode();
	}

}
