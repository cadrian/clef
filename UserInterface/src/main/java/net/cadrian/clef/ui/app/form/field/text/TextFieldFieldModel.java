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

import javax.swing.JTextField;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;

class TextFieldFieldModel<T extends Bean> extends AbstractSimpleFieldModel<T, String, JTextField> {

	TextFieldFieldModel(final String name, final String tab, final Method getter, final Method setter,
			final FieldComponentFactory<T, String, JTextField> componentFactory) {
		super(name, tab, getter, setter, componentFactory);
	}

	@Override
	protected FieldComponent<String, JTextField> createNewComponent(final T contextBean,
			final ApplicationContext context) throws ModelException {
		return new TextFieldComponent(componentFactory.isWritable());
	}

}
