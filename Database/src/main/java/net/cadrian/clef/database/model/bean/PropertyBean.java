package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Property;
import net.cadrian.clef.model.ModelException;

public class PropertyBean extends AbstractBean implements net.cadrian.clef.model.bean.Property {

	private final Property bean;

	public PropertyBean(final Property bean, final DatabaseBeansHolder db) {
		super(db);
		this.bean = bean;
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

	@Override
	public void delete() {
		try {
			db.getProperties().delete(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public String toString() {
		final String name = getName();
		if (name == null || name.isEmpty()) {
			return "(no name)";
		}
		return name;
	}

}
