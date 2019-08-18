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
package net.cadrian.clef.ui.app.form.model;

import java.util.Arrays;
import java.util.Collection;

import javax.swing.JComponent;

import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.Session;
import net.cadrian.clef.ui.app.form.BeanFormModel;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.activity.ActivityComponentFactory;
import net.cadrian.clef.ui.app.form.field.date.DateComponentFactory;
import net.cadrian.clef.ui.app.form.field.properties.PropertiesComponentFactory;
import net.cadrian.clef.ui.app.form.field.text.TextAreaComponentFactory;

public class SessionFormModel extends BeanFormModel<Session> {

	private static final Collection<FieldComponentFactory<Session, ?, ? extends JComponent>> COMPONENT_FACTORIES;
	static {
		final DateComponentFactory<Session> startFactory = new DateComponentFactory<>(Session.class, "Start", false);
		final DateComponentFactory<Session> stopFactory = new DateComponentFactory<>(Session.class, "Stop", true);
		startFactory.setUpperBound(stopFactory);
		stopFactory.setLowerBound(startFactory);
		COMPONENT_FACTORIES = Arrays.asList(startFactory, stopFactory, new ActivityComponentFactory("Activity", true),
				new TextAreaComponentFactory<>(Session.class, "Notes", true),
				new PropertiesComponentFactory<>(Session.class, "Properties", Entity.session, true));
	}

	public SessionFormModel(final Class<Session> beanType) {
		super(COMPONENT_FACTORIES);
	}

}
