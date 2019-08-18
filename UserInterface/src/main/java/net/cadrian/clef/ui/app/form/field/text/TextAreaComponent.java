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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.widget.rte.RichTextEditor;

class TextAreaComponent implements FieldComponent<String, RichTextEditor> {

	private static final Logger LOGGER = LoggerFactory.getLogger(TextAreaComponent.class);

	private final RichTextEditor component;

	TextAreaComponent(final ApplicationContext context, final boolean writable) {
		component = new RichTextEditor(context);
		component.setEditable(writable);
	}

	@Override
	public RichTextEditor getComponent() {
		return component;
	}

	@Override
	public String getData() {
		return component.getText();
	}

	@Override
	public void setData(final String data) {
		component.setText(data == null ? "" : data);
		component.markSave();
	}

	@Override
	public double getWeight() {
		return 2;
	}

	@Override
	public boolean isDirty() {
		final boolean result = component.isDirty();
		if (result) {
			LOGGER.debug("dirty");
		}
		return result;
	}

}
