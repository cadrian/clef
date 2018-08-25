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
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;

public abstract class SimpleFieldModel<T extends Bean, D, J extends JComponent> implements FieldModel<T, D, J> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleFieldModel.class);

	private static final Map<Bean, Map<String, FieldComponent<?, ?>>> CACHE = new WeakHashMap<>();

	protected /* static */ FieldComponent<D, J> getCachedComponent(final T contextBean, final String name)
			throws ModelException {
		final Map<String, FieldComponent<?, ?>> cache = CACHE.get(contextBean);
		@SuppressWarnings("unchecked")
		final FieldComponent<D, J> result = cache == null ? null : (FieldComponent<D, J>) cache.get(name);
		return result;
	}

	private final String name;
	private final String tab;
	private final Method getter;
	private final Method setter;
	protected final FieldComponentFactory<T, D, J> componentFactory;

	protected SimpleFieldModel(final String name, final String tab, final Method getter, final Method setter,
			final FieldComponentFactory<T, D, J> componentFactory) {
		this.name = name;
		this.tab = tab;
		this.getter = getter;
		this.setter = setter;
		this.componentFactory = componentFactory;
	}

	@Override
	public final FieldComponent<D, J> createComponent(final T contextBean, final ApplicationContext context)
			throws ModelException {
		FieldComponent<D, J> result = getCachedComponent(contextBean);
		if (result == null) {
			result = createNewComponent(contextBean, context);
			setCachedComponent(contextBean, result);
		}
		return result;
	}

	private FieldComponent<D, J> getCachedComponent(final T contextBean) {
		return getCachedComponent(contextBean, name);
	}

	private void setCachedComponent(final T contextBean, final FieldComponent<D, J> result) {
		Map<String, FieldComponent<?, ?>> cache = CACHE.get(contextBean);
		if (cache == null) {
			cache = new HashMap<>();
			CACHE.put(contextBean, cache);
		}
		cache.put(name, result);
	}

	protected abstract FieldComponent<D, J> createNewComponent(T contextBean, ApplicationContext context)
			throws ModelException;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getTab() {
		return tab;
	}

	@Override
	public D load(final T bean) {
		try {
			@SuppressWarnings("unchecked")
			final D data = (D) getter.invoke(bean);
			return data;
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void save(final T bean, final D data) {
		if (setter == null) {
			LOGGER.info("no setter for {}", name);
		} else {
			try {
				setter.invoke(bean, data);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ModelException(e);
			}
		}
	}

}
