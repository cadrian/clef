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

import net.cadrian.clef.model.Bean;

abstract class AbstractBean implements Bean {

	protected final DatabaseBeansHolder db;

	public abstract Long getId();

	AbstractBean(final DatabaseBeansHolder db) {
		this.db = db;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj.getClass() != getClass()) {
			return false;
		}
		return getId().equals(((AbstractBean) obj).getId());
	}

	@Override
	public boolean isVersionOf(final Bean bean) {
		return equals(bean);
	}

	@Override
	public int hashCode() {
		final Long id = getId();
		return id == null ? 0 : id.hashCode();
	}

}
