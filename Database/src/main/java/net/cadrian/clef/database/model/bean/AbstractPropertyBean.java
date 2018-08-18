package net.cadrian.clef.database.model.bean;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import net.cadrian.clef.database.DatabasePropertyBean;
import net.cadrian.clef.model.bean.Property;

abstract class AbstractPropertyBean extends AbstractBean implements net.cadrian.clef.model.bean.PropertyBean {

	private final DatabasePropertyBean bean;
	protected final DatabaseBeansHolder db;

	AbstractPropertyBean(final DatabasePropertyBean bean, final DatabaseBeansHolder db) {
		this.bean = bean;
		this.db = db;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

	abstract void update();

	@Override
	public Map<String, ? extends net.cadrian.clef.model.bean.Property> getProperties() {
		final Collection<Long> ids = bean.getProperties();
		final Collection<PropertyBean> properties = db.getProperties(ids);
		final Map<String, PropertyBean> result = new HashMap<>();
		for (final PropertyBean property : properties) {
			result.put(property.getName(), property);
		}
		return result;
	}

	@Override
	public Property getProperty(final String propertyName) {
		return getProperties().get(propertyName);
	}

	@Override
	public void setProperty(final Property property) {
		final Collection<Long> ids = bean.getProperties();
		final Long newId = ((PropertyBean) property).getId();
		if (!ids.contains(newId)) {
			final Set<Long> newIds = new TreeSet<>(ids);
			newIds.add(newId);
			bean.setProperties(newIds);
			update();
		}
	}

	@Override
	public Property delProperty(final String propertyName) {
		final PropertyBean result = (PropertyBean) getProperty(propertyName);
		final Collection<Long> ids = bean.getProperties();
		final Long newId = result.getId();
		if (ids.contains(newId)) {
			final Set<Long> newIds = new TreeSet<>(ids);
			newIds.remove(newId);
			bean.setProperties(newIds);
			update();
		}
		return result;
	}

}
