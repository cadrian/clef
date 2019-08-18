/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
package net.cadrian.clef.ui.app.form;

import java.util.Collection;
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

	private final Map<String, FieldModel<T, ?, ?>> fields = new LinkedHashMap<>();

	protected BeanFormModel(final Collection<FieldComponentFactory<T, ?, ? extends JComponent>> componentFactories) {
		initFields(componentFactories);
	}

	private void initFields(final Collection<FieldComponentFactory<T, ?, ? extends JComponent>> componentFactories) {
		for (final FieldComponentFactory<T, ?, ? extends JComponent> componentFactory : componentFactories) {
			final String fieldName = componentFactory.getFieldName();
			LOGGER.info("Adding field model for {}", fieldName);
			final FieldModel<T, ?, ?> fieldModel = componentFactory.createModel();
			fields.put(fieldName, fieldModel);
			LOGGER.info("Added field model for {}: {}", fieldName, fieldModel);
		}
		for (final FieldComponentFactory<T, ?, ? extends JComponent> componentFactory : componentFactories) {
			final String fieldName = componentFactory.getFieldName();
			created(componentFactory, fieldName);
		}
	}

	@SuppressWarnings("unchecked")
	private <D, J extends JComponent> void created(final FieldComponentFactory<T, D, J> componentFactory,
			final String fieldName) {
		componentFactory.created((FieldModel<T, D, J>) fields.get(fieldName));
	}

	Map<String, FieldModel<T, ?, ?>> getFields() {
		return fields;
	}

}
