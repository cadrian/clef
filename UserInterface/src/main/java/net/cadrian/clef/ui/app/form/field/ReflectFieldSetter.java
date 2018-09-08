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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;

public class ReflectFieldSetter<T extends Bean, D> implements FieldSetter<T, D> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectFieldSetter.class);

	private final Method setter;

	public ReflectFieldSetter(final Method setter) {
		this.setter = setter;
	}

	public ReflectFieldSetter(final Class<T> beanType, final String fieldName, final boolean writable) {
		this(setMethod(beanType, fieldName, writable, ReflectFieldGetter.<T>getMethod(beanType, fieldName)));
	}

	static <T extends Bean> Method setMethod(final Class<T> beanType, final String fieldName, final boolean writable,
			final Method getter) {
		try {
			return beanType.getMethod("set" + fieldName, getter.getReturnType());
		} catch (final NoSuchMethodException e) {
			if (writable) {
				throw new ModelException(e);
			}
			LOGGER.debug("No setter, but {} is not writable anyway", fieldName);
			return null;
		} catch (final SecurityException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void set(final T bean, final D data) {
		if (setter == null) {
			LOGGER.debug("No setter, discarding data");
		} else {
			try {
				setter.invoke(bean, data);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ModelException(e);
			}
		}
	}

}
