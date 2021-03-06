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
package net.cadrian.clef.ui.app.form.field.activity;

import javax.swing.JComboBox;

import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldSetter;

class ActivityFieldModel extends AbstractSimpleFieldModel<Session, Activity, JComboBox<Activity>> {

	ActivityFieldModel(final String name, final String tab, final FieldGetter<Session, Activity> getter,
			final FieldSetter<Session, Activity> setter,
			final FieldComponentFactory<Session, Activity, JComboBox<Activity>> componentFactory) {
		super(name, tab, getter, setter, componentFactory);
	}

	@Override
	protected FieldComponent<Activity, JComboBox<Activity>> createNewComponent(final Session contextBean,
			final ApplicationContext context) throws ModelException {
		return new ActivityComponent(componentFactory.isWritable(), context.getBeans());
	}

}
