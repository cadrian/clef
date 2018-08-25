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

import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldModel;

public abstract class BeanFormModel<T extends Bean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanFormModel.class);

	private final Class<T> beanType;
	private final Map<String, FieldModel<T, ?, ?>> fields = new LinkedHashMap<>();

	protected BeanFormModel(final Class<T> beanType,
			final Map<String, FieldComponentFactory<T, ?, ? extends JComponent>> componentFactories) {
		this.beanType = beanType;
		initFields(componentFactories);
	}

	private void initFields(final Map<String, FieldComponentFactory<T, ?, ? extends JComponent>> componentFactories) {
		for (final Map.Entry<String, FieldComponentFactory<T, ?, ? extends JComponent>> entry : componentFactories
				.entrySet()) {
			final String fieldName = entry.getKey();
			final FieldComponentFactory<T, ?, ? extends JComponent> componentFactory = entry.getValue();
			LOGGER.info("Adding field model for {}", fieldName);
			final FieldModel<T, ?, ?> model = componentFactory.createModel(beanType, fieldName);
			fields.put(fieldName, model);
		}
	}

	Map<String, FieldModel<T, ?, ?>> getFields() {
		return fields;
	}

}
