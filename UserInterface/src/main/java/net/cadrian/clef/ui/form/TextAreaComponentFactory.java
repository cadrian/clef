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

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.rte.RichTextEditor;

public class TextAreaComponentFactory<C extends Bean> extends AbstractFieldComponentFactory<String, RichTextEditor, C> {

	private static class TextAreaComponent implements FieldComponent<String, RichTextEditor> {

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
			component.setText(data);
		}

		@Override
		public double getWeight() {
			return 2;
		}

	}

	public TextAreaComponentFactory(final boolean writable) {
		this(writable, null);
	}

	public TextAreaComponentFactory(final boolean writable, final String tab) {
		super(writable, tab);
	}

	@Override
	public FieldComponent<String, RichTextEditor> createComponent(final ApplicationContext context,
			final C contextBean) {
		return new TextAreaComponent(context, writable);
	}

	@Override
	public Class<String> getDataType() {
		return String.class;
	}

}
