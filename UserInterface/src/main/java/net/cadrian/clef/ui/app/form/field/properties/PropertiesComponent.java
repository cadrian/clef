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
package net.cadrian.clef.ui.app.form.field.properties;

import net.cadrian.clef.model.bean.PropertyDescriptor.Entity;
import net.cadrian.clef.model.bean.PropertyDescriptor.Type;
import net.cadrian.clef.ui.ApplicationContext;

class PropertiesComponent extends AbstractPropertiesComponent {

	PropertiesComponent(final ApplicationContext context, final Entity entity, final boolean writable) {
		super(context, entity, writable);
	}

	@Override
	protected PropertyEditor getEditor(final EditableProperty selected) {
		final Type type = selected.getPropertyDescriptor().getType();
		switch (type) {
		case string:
			return new StringPropertyEditor(context, writable, selected);
		case date:
			return new DatePropertyEditor(context, writable, selected);
		case file:
			return new FilePropertyEditor(context, writable, selected);
		case path:
			return new PathPropertyEditor(context, writable, selected);
		default:
			return null;
		}
	}
}
