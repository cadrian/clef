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

import java.lang.reflect.Method;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;

public abstract class AbstractSimpleFieldComponentFactory<T extends Bean, D, J extends JComponent>
		implements FieldComponentFactory<T, D, J> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimpleFieldComponentFactory.class);

	protected final boolean writable;
	protected final String tab;

	protected AbstractSimpleFieldComponentFactory(final boolean writable, final String tab) {
		this.writable = writable;
		this.tab = tab;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public FieldModel<T, D, J> createModel(final Class<T> beanType, final String fieldName) throws ModelException {
		try {
			final Class<?> dataType = getDataType();
			final Method getter = beanType.getMethod("get" + fieldName);
			if (!getter.getReturnType().equals(dataType)) {
				LOGGER.error("BUG: invalid field model for {} -- {} vs {}", fieldName, getter.getReturnType().getName(),
						dataType.getName());
			} else {
				final Method setter;
				if (isWritable()) {
					setter = beanType.getMethod("set" + fieldName, dataType);
				} else {
					setter = null;
				}
				return createModel(fieldName, getter, setter);
			}
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ModelException(e);
		}
		return null;
	}

	protected abstract FieldModel<T, D, J> createModel(final String fieldName, final Method getter,
			final Method setter);

}
