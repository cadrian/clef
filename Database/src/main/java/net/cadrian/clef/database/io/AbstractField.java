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
package net.cadrian.clef.database.io;

import net.cadrian.clef.database.DatabaseBean;
import net.cadrian.clef.database.DatabaseException;

abstract class AbstractField<T extends DatabaseBean, D> implements Field<T> {

	private final String name;
	protected final FieldGetter<T, D> getter;
	protected final FieldSetter<T, D> setter;

	AbstractField(final String name, final FieldGetter<T, D> getter, final FieldSetter<T, D> setter) {
		this.name = name;
		this.getter = getter;
		this.setter = setter;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final boolean hasCondition(final T bean) throws DatabaseException {
		final Object data = getter.get(bean);
		return data != null;
	}

}
