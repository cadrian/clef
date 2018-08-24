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
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldComponent;

public class BeanComponentFactory<T extends Bean, C extends Bean>
		extends AbstractFieldComponentFactory<T, JTextField, C> {

	private final Class<T> beanType;

	public BeanComponentFactory(final Class<T> beanType) {
		this(beanType, null);
	}

	public BeanComponentFactory(final Class<T> beanType, final String tab) {
		super(false, tab);
		this.beanType = beanType;
	}

	@Override
	public FieldComponent<T, JTextField> createComponent(final ApplicationContext context, final C contextBean) {
		return new BeanComponent<>(writable);
	}

	@Override
	public Class<T> getDataType() {
		return beanType;
	}

}
