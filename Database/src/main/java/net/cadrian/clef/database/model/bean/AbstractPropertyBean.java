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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import net.cadrian.clef.database.DatabasePropertyBean;
import net.cadrian.clef.model.bean.Property;

abstract class AbstractPropertyBean extends AbstractBean implements net.cadrian.clef.model.bean.PropertyBean {

	private final DatabasePropertyBean bean;

	AbstractPropertyBean(final DatabasePropertyBean bean, final DatabaseBeansHolder db) {
		super(db);
		this.bean = bean;
	}

	@Override
	public Long getId() {
		return bean.getId();
	}

	abstract void update();

	@Override
	public Collection<? extends net.cadrian.clef.model.bean.Property> getProperties() {
		final Collection<Long> ids = bean.getProperties();
		final Collection<PropertyBean> properties = db.getProperties(ids);
		final Set<PropertyBean> result = new HashSet<>();
		for (final PropertyBean property : properties) {
			result.add(property);
		}
		return result;
	}

	@Override
	public void setProperties(final Collection<? extends Property> properties) {
		final Set<Long> newIds = new TreeSet<>();
		for (final Property property : properties) {
			final Long newId = ((PropertyBean) property).getId();
			newIds.add(newId);
		}
		bean.setProperties(newIds);
		update();
	}

}
