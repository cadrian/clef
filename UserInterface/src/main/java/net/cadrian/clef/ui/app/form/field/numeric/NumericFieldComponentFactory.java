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
package net.cadrian.clef.ui.app.form.field.numeric;

import javax.swing.JFormattedTextField;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.app.form.field.FieldSetter;

public class NumericFieldComponentFactory<T extends Bean>
		extends AbstractSimpleFieldComponentFactory<T, Long, JFormattedTextField> {

	public NumericFieldComponentFactory(final Class<T> beanType, final String fieldName, final boolean writable) {
		this(beanType, fieldName, writable, null);
	}

	public NumericFieldComponentFactory(final Class<T> beanType, final String fieldName, final boolean writable,
			final String tab) {
		super(beanType, fieldName, writable, tab);
	}

	@Override
	protected FieldModel<T, Long, JFormattedTextField> createModel(final String fieldName,
			final FieldGetter<T, Long> getter, final FieldSetter<T, Long> setter) {
		return new NumericFieldModel<>(fieldName, tab, getter, setter, this);
	}

}
