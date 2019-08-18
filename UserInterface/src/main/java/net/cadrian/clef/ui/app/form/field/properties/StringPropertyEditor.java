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

import javax.swing.JComponent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.widget.rte.RichTextEditor;

class StringPropertyEditor implements PropertyEditor {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringPropertyEditor.class);

	private final EditableProperty property;
	private final RichTextEditor content;

	StringPropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
		this.property = property;
		content = new RichTextEditor(context);
		content.replaceSelection(property.getValue());
		content.markSave();
		content.setEditable(writable);
	}

	@Override
	public JComponent getEditor() {
		return content;
	}

	@Override
	public boolean isDirty() {
		final boolean result = content.isDirty();
		if (result) {
			LOGGER.debug("dirty: {}", property);
		}
		return result;
	}

	@Override
	public void save() {
		property.setValue(content.getText());
		content.markSave();
	}
}
