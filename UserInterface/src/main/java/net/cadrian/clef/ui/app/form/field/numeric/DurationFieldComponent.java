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
package net.cadrian.clef.ui.app.form.field.numeric;

import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.widget.DurationTextField;

class DurationFieldComponent implements FieldComponent<Long, DurationTextField> {

	private final DurationTextField component;
	private String savedData = "";

	DurationFieldComponent(final boolean writable) {
		component = new DurationTextField();
		component.setEditable(writable);
	}

	@Override
	public DurationTextField getComponent() {
		return component;
	}

	@Override
	public Long getData() {
		savedData = component.getText();
		return (Long) component.getValue();
	}

	@Override
	public void setData(final Long data) {
		component.setValue(data);
		savedData = component.getText();
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public boolean isDirty() {
		return !savedData.equals(component.getText());
	}

}
