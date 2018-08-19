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

import javax.swing.JTextField;

import net.cadrian.clef.ui.Resources;

public class TextFieldComponentFactory<C> extends AbstractFieldComponentFactory<String, JTextField, C> {

	private static class TextFieldComponent implements FieldComponent<String, JTextField> {

		private final JTextField component;

		TextFieldComponent(final boolean writable) {
			component = new JTextField();
			component.setEditable(writable);
		}

		@Override
		public JTextField getComponent() {
			return component;
		}

		@Override
		public String getData() {
			return component.getText();
		}

		@Override
		public void setData(final String data) {
			component.setText(data);
		}

		@Override
		public double getWeight() {
			return 0;
		}

	}

	public TextFieldComponentFactory(final boolean writable) {
		this(writable, null);
	}

	public TextFieldComponentFactory(final boolean writable, final String tab) {
		super(writable, tab);
	}

	@Override
	public FieldComponent<String, JTextField> createComponent(final Resources rc, final C context) {
		return new TextFieldComponent(writable);
	}

	@Override
	public Class<String> getDataType() {
		return String.class;
	}

}
