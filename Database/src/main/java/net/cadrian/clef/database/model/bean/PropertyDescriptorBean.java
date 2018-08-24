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
package net.cadrian.clef.database.model.bean;

import net.cadrian.clef.database.DatabaseException;
import net.cadrian.clef.database.bean.Property;
import net.cadrian.clef.database.bean.PropertyDescriptor;
import net.cadrian.clef.model.ModelException;

public class PropertyDescriptorBean extends AbstractBean implements net.cadrian.clef.model.bean.PropertyDescriptor {

	private final PropertyDescriptor bean;

	public PropertyDescriptorBean(final PropertyDescriptor bean, final DatabaseBeansHolder db) {
		super(db);
		this.bean = bean;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

	@Override
	public Entity getEntity() {
		return Entity.valueOf(bean.getEntity());
	}

	@Override
	public Type getType() {
		final String type = bean.getType();
		return type == null ? null : Type.valueOf(type);
	}

	@Override
	public void setType(final Type type) {
		bean.setType(type.name());
	}

	@Override
	public String getName() {
		return bean.getName();
	}

	@Override
	public void setName(final String name) {
		bean.setName(name);
	}

	@Override
	public String getDescription() {
		return bean.getDescription();
	}

	@Override
	public void setDescription(final String description) {
		bean.setDescription(description);
		update();
	}

	@Override
	public int countUsages() {
		try {
			final Property template = new Property();
			template.setPropertyDescriptorId(getId());
			return db.getProperties().count(template);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	private void update() {
		try {
			db.getPropertyDescriptors().update(bean);
		} catch (final DatabaseException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void delete() {
		try {
			db.getPropertyDescriptors().delete(bean);
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
