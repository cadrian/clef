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

import javax.swing.JComponent;
import javax.swing.JTextField;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

class BeanComponentText<T extends Bean> implements FieldComponent<T, JComponent> {

	private final JTextField componentText;
	private T data;

	BeanComponentText(final boolean writable) {
		componentText = new JTextField();
		componentText.setEditable(writable);
	}

	@Override
	public JComponent getComponent() {
		return componentText;
	}

	@Override
	public T getData() {
		return data;
	}

	@Override
	public void setData(final T data) {
		this.data = data;
		componentText.setText(data.toString());
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
