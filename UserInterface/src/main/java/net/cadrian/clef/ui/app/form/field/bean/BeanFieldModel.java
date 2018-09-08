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
package net.cadrian.clef.ui.app.form.field.bean;

import javax.swing.JTextField;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldSetter;

class BeanFieldModel<T extends Bean, D extends Bean> extends AbstractSimpleFieldModel<T, D, JTextField> {

	BeanFieldModel(final String name, final String tab, final FieldGetter<T, D> getter, final FieldSetter<T, D> setter,
			final FieldComponentFactory<T, D, JTextField> componentFactory) {
		super(name, tab, getter, setter, componentFactory);
	}

	@Override
	protected FieldComponent<D, JTextField> createNewComponent(final T contextBean, final ApplicationContext context)
			throws ModelException {
		return new BeanComponent<>(componentFactory.isWritable());
	}

}
