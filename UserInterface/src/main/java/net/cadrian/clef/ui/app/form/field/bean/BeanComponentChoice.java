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
package net.cadrian.clef.ui.app.form.field.bean;

import java.util.Collection;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

class BeanComponentChoice<T extends Bean> implements FieldComponent<T, JComponent> {

	private final JComboBox<T> componentChoice;

	BeanComponentChoice(final boolean writable, final Collection<? extends T> values) {
		componentChoice = new JComboBox<>(new Vector<>(values));
		componentChoice.setEditable(false);
		componentChoice.setEnabled(writable);
	}

	@Override
	public JComponent getComponent() {
		return componentChoice;
	}

	@Override
	public T getData() {
		@SuppressWarnings("unchecked")
		final T result = (T) componentChoice.getSelectedItem();
		return result;
	}

	@Override
	public void setData(final T data) {
		componentChoice.setSelectedItem(data);
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public boolean isDirty() {
		return false;
	}

}
