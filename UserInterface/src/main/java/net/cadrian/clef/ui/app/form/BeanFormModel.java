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

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;

public abstract class BeanFormModel<T extends Bean, C extends Bean> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BeanFormModel.class);

	private final Class<T> beanType;
	private final Map<String, FieldModel<T, ?, ?, C>> fields = new LinkedHashMap<>();

	protected BeanFormModel(final Class<T> beanType,
			final Map<String, FieldComponentFactory<?, ? extends JComponent, C>> componentFactories) {
		this.beanType = beanType;
		initFields(componentFactories);
	}

	private void initFields(final Map<String, FieldComponentFactory<?, ? extends JComponent, C>> componentFactories) {
		for (final Map.Entry<String, FieldComponentFactory<?, ? extends JComponent, C>> entry : componentFactories
				.entrySet()) {
			final String fieldName = entry.getKey();
			try {
				final FieldComponentFactory<?, ? extends JComponent, C> componentFactory = entry.getValue();
				final Class<?> dataType = componentFactory.getDataType();
				final Method getter = beanType.getMethod("get" + fieldName);
				if (!getter.getReturnType().equals(dataType)) {
					LOGGER.error("BUG: invalid field model for {} -- {} vs {}", fieldName,
							getter.getReturnType().getName(), dataType.getName());
				} else {
					final Method setter;
					if (componentFactory.isWritable()) {
						setter = beanType.getMethod("set" + fieldName, dataType);
					} else {
						setter = null;
					}
					LOGGER.info("Adding field model for {}", fieldName);
					final FieldModel<T, ?, ?, C> model = new FieldModel<>(fieldName, getter, setter, componentFactory);
					fields.put(fieldName, model);
				}
			} catch (NoSuchMethodException | SecurityException e) {
				LOGGER.error("BUG: invalid field model for {}", fieldName, e);
			}
		}
	}

	Map<String, FieldModel<T, ?, ?, C>> getFields() {
		return fields;
	}

}
