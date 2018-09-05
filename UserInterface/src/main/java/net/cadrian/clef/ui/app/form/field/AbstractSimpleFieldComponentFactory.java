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
import java.util.HashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.app.tab.DataPane;

public abstract class AbstractSimpleFieldComponentFactory<T extends Bean, D, J extends JComponent>
		implements FieldComponentFactory<T, D, J> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractSimpleFieldComponentFactory.class);

	private static final Map<Class<?>, Map<String, FieldModel<?, ?, ?>>> CACHE = new HashMap<>();

	protected static <T extends Bean, D, J extends JComponent> FieldModel<T, D, J> getCachedModel(
			final Class<T> beanType, final String fieldName) throws ModelException {
		final Map<String, FieldModel<?, ?, ?>> cache = CACHE.get(beanType);
		@SuppressWarnings("unchecked")
		final FieldModel<T, D, J> result = cache == null ? null : (FieldModel<T, D, J>) cache.get(fieldName);
		return result;
	}

	protected final Class<T> beanType;
	protected final String fieldName;
	protected final boolean writable;
	protected final String tab;

	protected AbstractSimpleFieldComponentFactory(final Class<T> beanType, final String fieldName,
			final boolean writable, final String tab) {
		this.beanType = beanType;
		this.fieldName = fieldName;
		this.writable = writable;
		this.tab = tab == null ? DataPane.DEFAULT_TAB : tab;
	}

	@Override
	public boolean isWritable() {
		return writable;
	}

	@Override
	public String getFieldName() {
		return fieldName;
	}

	@Override
	public Class<T> getBeanType() {
		return beanType;
	}

	@Override
	public FieldModel<T, D, J> createModel() throws ModelException {
		FieldModel<T, D, J> result = getCachedModel();
		if (result == null) {
			try {
				final Class<?> dataType = getDataType();
				final Method getter = beanType.getMethod("get" + fieldName);
				if (!getter.getReturnType().equals(dataType)) {
					LOGGER.error("BUG: invalid field model for {} -- {} vs {}", fieldName,
							getter.getReturnType().getName(), dataType.getName(), new Exception("BUG"));
				} else {
					Method setter;
					try {
						setter = beanType.getMethod("set" + fieldName, dataType);
					} catch (NoSuchMethodException e) {
						if (isWritable()) {
							throw e;
						}
						LOGGER.debug("No setter, but {} is not writable anyway", fieldName);
						setter = null;
					}
					result = createModel(fieldName, getter, setter);
					setCachedModel(result);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				throw new ModelException(e);
			}
		}
		return result;
	}

	protected abstract FieldModel<T, D, J> createModel(final String fieldName, final Method getter,
			final Method setter);

	private FieldModel<T, D, J> getCachedModel() {
		return getCachedModel(beanType, fieldName);
	}

	private void setCachedModel(final FieldModel<T, D, J> result) {
		Map<String, FieldModel<?, ?, ?>> cache = CACHE.get(beanType);
		if (cache == null) {
			cache = new HashMap<>();
			CACHE.put(beanType, cache);
		}
		cache.put(fieldName, result);
	}

}
