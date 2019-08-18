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

import net.cadrian.clef.model.bean.Activity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.app.form.field.FieldSetter;

public class ActivityComponentFactory
		extends AbstractSimpleFieldComponentFactory<Session, Activity, JComboBox<Activity>> {

	public ActivityComponentFactory(final String fieldName, final boolean writable) {
		this(fieldName, writable, null);
	}

	public ActivityComponentFactory(final String fieldName, final boolean writable, final String tab) {
		super(Session.class, fieldName, writable, tab);
	}

	@Override
	protected FieldModel<Session, Activity, JComboBox<Activity>> createModel(final String fieldName,
			final FieldGetter<Session, Activity> getter, final FieldSetter<Session, Activity> setter) {
		return new ActivityFieldModel(fieldName, tab, getter, setter, this);
	}

}
