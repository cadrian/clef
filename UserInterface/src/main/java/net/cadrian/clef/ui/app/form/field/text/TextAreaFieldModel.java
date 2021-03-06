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
package net.cadrian.clef.ui.app.form.field.text;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.widget.rte.RichTextEditor;

class TextAreaFieldModel<T extends Bean> extends AbstractSimpleFieldModel<T, String, RichTextEditor> {

	TextAreaFieldModel(final String name, final String tab, final FieldGetter<T, String> getter,
			final FieldSetter<T, String> setter,
			final FieldComponentFactory<T, String, RichTextEditor> componentFactory) {
		super(name, tab, getter, setter, componentFactory);
	}

	@Override
	protected FieldComponent<String, RichTextEditor> createNewComponent(final T contextBean,
			final ApplicationContext context) throws ModelException {
		return new TextAreaComponent(context, componentFactory.isWritable());
	}

}
