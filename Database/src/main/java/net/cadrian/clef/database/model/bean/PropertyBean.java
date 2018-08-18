package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Property;
import net.cadrian.clef.model.ModelException;

public class PropertyBean extends AbstractBean implements net.cadrian.clef.model.bean.Property {

	private final Property bean;
	private final DatabaseBeansHolder db;

	public PropertyBean(final Property bean, final DatabaseBeansHolder db) {
		this.bean = bean;
		this.db = db;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

	@Override
	public String getName() {
		return bean.getName();
	}

	@Override
	public String getValue() {
		return bean.getValue();
	}

	@Override
	public void setValue(final String value) {
		bean.setValue(value);
		update();
	}

	private void update() {
		try {
			db.getProperties().update(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

}
