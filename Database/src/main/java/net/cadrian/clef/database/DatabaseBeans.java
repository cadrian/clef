package net.cadrian.clef.database;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database accessor
 *
 * @param <T> type of database beans managed by this accessor
 */
public class DatabaseBeans<T extends DatabaseBean> {

	private final Class<T> beanType;
	private final Constructor<T> ctor;
	private final DataSource dataSource;
	private final Logger logger;

	public DatabaseBeans(final DataSource dataSource, final Class<T> beanType) throws DatabaseException {
		this.dataSource = dataSource;
		this.beanType = beanType;
		try {
			this.ctor = beanType.getConstructor(Long.class);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new DatabaseException(e);
		}
		this.logger = LoggerFactory.getLogger(DatabaseBeans.class.getName() + "<" + beanType.getSimpleName() + ">");
	}

	/**
	 * Count the beans that match the template.
	 *
	 * @param template
	 * @return
	 * @throws DatabaseException
	 */
	public int count(final T template) throws DatabaseException {
		try (Connection cnx = dataSource.getConnection()) {
			return template.count(cnx);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Read one bean matching the template. It must be unique. (Typical use case:
	 * template contains only id)
	 *
	 * @param template
	 * @return <code>null if no match</code>, the bean if exists
	 * @throws DatabaseException if more than one bean matches the template
	 */
	public T readOne(final T template) throws DatabaseException {
		try (Connection cnx = dataSource.getConnection()) {
			return readOne(template, cnx);
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	private T readOne(final T template, final Connection cnx) throws DatabaseException {
		@SuppressWarnings("unchecked")
		final Collection<T> results = (Collection<T>) template.read(cnx);
		logger.debug("{} returned {}", template, results);
		if (results.isEmpty()) {
			return null;
		}
		if (results.size() > 1) {
			throw new DatabaseException("Multiple objects returned by template: " + template);
		}
		return beanType.cast(results.iterator().next());
	}

	/**
	 * Read all beans matching the template.
	 *
	 * @param template
	 * @return the list of matching beans. Never <code>null</code>.
	 * @throws DatabaseException
	 */
	public Map<Long, T> readMany(final T template) throws DatabaseException {
		try (Connection cnx = dataSource.getConnection()) {
			cnx.setAutoCommit(false);
			@SuppressWarnings("unchecked")
			final Collection<T> results = (Collection<T>) template.read(cnx);
			logger.debug("{} returned {}", template, results);
			final Map<Long, T> result = new HashMap<>(results.size());
			for (final DatabaseBean b : results) {
				result.put(b.getId(), beanType.cast(b));
			}
			cnx.commit();
			return result;
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Update the bean and returns it. The bean must have a valid id.
	 *
	 * @param bean
	 * @return
	 * @throws DatabaseException
	 */
	public T update(final T bean) throws DatabaseException {
		assert bean.getId() != null;
		try (Connection cnx = dataSource.getConnection()) {
			cnx.setAutoCommit(false);
			final Long id = bean.save(cnx);
			assert id == bean.getId();
			cnx.commit();
			return bean;
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Insert the bean and returns a version with the correct id. The template must
	 * not have an id.
	 *
	 * @param template
	 * @return
	 * @throws DatabaseException
	 */
	public T insert(final T template) throws DatabaseException {
		assert template.getId() == null;
		try (Connection cnx = dataSource.getConnection()) {
			cnx.setAutoCommit(false);
			final Long id = template.save(cnx);
			final T bean;
			try {
				bean = ctor.newInstance(id);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new DatabaseException(e);
			}
			final T result = readOne(bean, cnx);
			cnx.commit();
			return result;
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Delete the bean.
	 *
	 * @param bean
	 * @throws DatabaseException
	 */
	public void delete(final T bean) throws DatabaseException {
		assert bean.getId() != null;
		try (Connection cnx = dataSource.getConnection()) {
			cnx.setAutoCommit(false);
			bean.delete(cnx);
			cnx.commit();
		} catch (final SQLException e) {
			throw new DatabaseException(e);
		}
	}

}
