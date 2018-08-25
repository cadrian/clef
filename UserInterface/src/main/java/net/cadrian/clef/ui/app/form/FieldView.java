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

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldModel;

class FieldView<T extends Bean, D, J extends JComponent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(FieldView.class);

	final FieldModel<T, D, J> model;
	final FieldComponent<D, J> component;

	FieldView(final FieldModel<T, D, J> model, final FieldComponent<D, J> component) {
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
		component.setData(model.load(bean));
	}

	void save(final T bean) {
		LOGGER.debug("saving {}.{}", bean, model.getName());
		model.save(bean, component.getData());
	}

	public boolean isDirty() {
		final boolean result = component.isDirty();
		if (result) {
			LOGGER.debug("dirty: {}", model.getName());
		}
		return result;
	}
}
