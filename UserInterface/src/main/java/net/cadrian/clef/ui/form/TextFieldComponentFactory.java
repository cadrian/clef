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

public class TextFieldComponentFactory implements FieldComponentFactory<String, JTextField> {

	private static class TextFieldComponent implements FieldComponent<String, JTextField> {

		private final JTextField component;

		TextFieldComponent() {
			component = new JTextField();
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

	@Override
	public FieldComponent<String, JTextField> createComponent() {
		return new TextFieldComponent();
	}

	@Override
	public Class<String> getDataType() {
		return String.class;
	}

}
