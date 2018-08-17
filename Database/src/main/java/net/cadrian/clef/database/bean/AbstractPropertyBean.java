package net.cadrian.clef.database.bean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

abstract class AbstractPropertyBean<T extends DatabaseBean> extends AbstractBean<T> {

	private static final List<Long> EMPTY_LIST = Arrays.asList();

	private List<Long> properties = EMPTY_LIST;

	AbstractPropertyBean(final Long id) {
		super(id);
	}

	public List<Long> getProperties() {
		return properties;
	}

	public void setProperties(final List<Long> properties) {
		this.properties = properties == null ? EMPTY_LIST : properties;
	}

	@Override
	public Collection<T> read(final Connection cnx) throws DatabaseException {
		final Collection<T> result = super.read(cnx);

		final StringBuilder sql = new StringBuilder("SELECT property_id FROM ");
		final String tableName = getTableName();
		sql.append(tableName).append("_property");
		sql.append(" WHERE ").append(tableName).append("_id=?");

		logger.debug(">> {}", sql);
		try (PreparedStatement ps = cnx.prepareStatement(sql.toString())) {
			for (final T bean : result) {
				@SuppressWarnings("unchecked")
				final AbstractPropertyBean<T> propertyBean = (AbstractPropertyBean<T>) bean;
				final List<Long> propertyIds = new ArrayList<>();
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

		final List<Long> propertyIds = getProperties();
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

	private void insertNewProperties(final Long id, final List<Long> propertyIds, final Connection cnx)
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
