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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.widget.DateSelector;

class DatePropertyEditor implements PropertyEditor {

	private static final Logger LOGGER = LoggerFactory.getLogger(DatePropertyEditor.class);

	private final EditableProperty property;
	private final JPanel container;
	private final DateSelector content;

	DatePropertyEditor(final ApplicationContext context, final boolean writable, final EditableProperty property) {
		this.property = property;

		content = new DateSelector(context, writable);
		content.setDateString(property.getValue());

		container = new JPanel(new GridBagLayout());
		final GridBagConstraints constraints = new GridBagConstraints();
		constraints.anchor = GridBagConstraints.CENTER;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.weightx = 1;
		container.add(content, constraints);
	}

	@Override
	public JComponent getEditor() {
		return container;
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
		property.setValue(content.getDateString());
		content.markSave();
	}
}
