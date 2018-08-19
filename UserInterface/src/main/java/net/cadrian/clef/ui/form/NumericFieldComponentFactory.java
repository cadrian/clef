/*
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
 *
 */
package net.cadrian.clef.ui.form;

import java.text.NumberFormat;

import javax.swing.JFormattedTextField;

public class NumericFieldComponentFactory extends AbstractFieldComponentFactory<Long, JFormattedTextField> {

	private static class NumericFieldComponent implements FieldComponent<Long, JFormattedTextField> {

		private final JFormattedTextField component;

		NumericFieldComponent(final boolean writable) {
			component = new JFormattedTextField(NumberFormat.getIntegerInstance());
			component.setEditable(writable);
		}

		@Override
		public JFormattedTextField getComponent() {
			return component;
		}

		@Override
		public Long getData() {
			try {
				return Long.parseLong(component.getText());
			} catch (final NumberFormatException e) {
				return 0L;
			}
		}

		@Override
		public void setData(final Long data) {
			component.setText(data == null ? "" : data.toString());
		}

		@Override
		public double getWeight() {
			return 0;
		}

	}

	public NumericFieldComponentFactory(final boolean writable) {
		super(writable);
	}

	@Override
	public FieldComponent<Long, JFormattedTextField> createComponent() {
		return new NumericFieldComponent(writable);
	}

	@Override
	public Class<Long> getDataType() {
		return Long.class;
	}

}
