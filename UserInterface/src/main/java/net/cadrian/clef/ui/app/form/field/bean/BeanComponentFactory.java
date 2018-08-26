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

import java.lang.reflect.Method;

import javax.swing.JTextField;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldModel;

public class BeanComponentFactory<T extends Bean, D extends Bean>
		extends AbstractSimpleFieldComponentFactory<T, D, JTextField> {

	private final Class<D> beanDataType;

	public BeanComponentFactory(final Class<T> beanType, final String fieldName, final Class<D> beanDataType) {
		this(beanType, fieldName, beanDataType, null);
	}

	public BeanComponentFactory(final Class<T> beanType, final String fieldName, final Class<D> beanDataType,
			final String tab) {
		super(beanType, fieldName, false, tab);
		this.beanDataType = beanDataType;
	}

	@Override
	protected FieldModel<T, D, JTextField> createModel(final String fieldName, final Method getter,
			final Method setter) {
		return new BeanFieldModel<>(fieldName, tab, getter, setter, this);
	}

	@Override
	public Class<D> getDataType() {
		return beanDataType;
	}

}