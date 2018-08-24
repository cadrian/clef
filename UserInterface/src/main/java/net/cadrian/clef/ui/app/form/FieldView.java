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
package net.cadrian.clef.ui.app.form;

import java.lang.reflect.InvocationTargetException;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

class FieldView<T extends Bean, D, J extends JComponent, C extends Bean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldView.class);

	final FieldModel<T, D, J, C> model;
	final FieldComponent<D, J> component;

	FieldView(final FieldModel<T, D, J, C> model, final FieldComponent<D, J> component) {
		if (model == null) {
			throw new NullPointerException("null model");
		}
		this.model = model;
		if (component == null) {
			throw new NullPointerException("null component");
		}
		this.component = component;
	}

	void load(final T bean) {
		try {
			@SuppressWarnings("unchecked")
			final D data = (D) model.getter.invoke(bean);
			component.setData(data);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("BUG: could not load value", e);
		}
	}

	void save(final T bean) {
		try {
			LOGGER.debug("saving {}.{}", bean, model.name);
			final D data = component.getData();
			if (model.setter == null) {
				LOGGER.info("no setter for {}", model.name);
			} else {
				model.setter.invoke(bean, data);
			}
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("BUG: could not save value", e);
		}
	}

	public boolean isDirty() {
		final boolean result = component.isDirty();
		if (result) {
			LOGGER.debug("dirty: {}", model.name);
		}
		return result;
	}
}
