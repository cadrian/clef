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
package net.cadrian.clef.ui.app.form.field;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;

public class ReflectFieldGetter<T extends Bean, D> implements FieldGetter<T, D> {

	private final Method getter;

	public ReflectFieldGetter(final Method getter) {
		this.getter = getter;
	}

	public ReflectFieldGetter(Class<T> beanType, String fieldName) {
		this(getMethod(beanType, fieldName));
	}

	static <T extends Bean> Method getMethod(Class<T> beanType, String fieldName) {
		try {
			return beanType.getMethod("get" + fieldName);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ModelException(e);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public D get(final T bean) {
		try {
			return (D) getter.invoke(bean);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ModelException(e);
		}
	}

}
