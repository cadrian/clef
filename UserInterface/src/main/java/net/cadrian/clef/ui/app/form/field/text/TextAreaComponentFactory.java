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
package net.cadrian.clef.ui.app.form.field.text;

import java.lang.reflect.Method;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.widget.rte.RichTextEditor;

public class TextAreaComponentFactory<T extends Bean>
		extends AbstractSimpleFieldComponentFactory<T, String, RichTextEditor> {

	public TextAreaComponentFactory(final boolean writable) {
		this(writable, null);
	}

	public TextAreaComponentFactory(final boolean writable, final String tab) {
		super(writable, tab);
	}

	@Override
	protected FieldModel<T, String, RichTextEditor> createModel(final String fieldName, final Method getter,
			final Method setter) {
		return new TextAreaFieldModel<>(fieldName, tab, getter, setter, this);
	}

	@Override
	public Class<String> getDataType() {
		return String.class;
	}

}
