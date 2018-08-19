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

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.Resources;

public class BeanComponentFactory<T extends Bean, C> extends AbstractFieldComponentFactory<T, JTextField, C> {

	private static class BeanComponent<T extends Bean> implements FieldComponent<T, JTextField> {

		private final JTextField component;

		BeanComponent(final boolean writable) {
			component = new JTextField();
			component.setEditable(writable);
		}

		@Override
		public JTextField getComponent() {
			return component;
		}

		@Override
		public T getData() {
			return null; // TODO
		}

		@Override
		public void setData(final T data) {
			component.setText(data.toString());
		}

		@Override
		public double getWeight() {
			return 0;
		}

	}

	private final Class<T> beanType;

	public BeanComponentFactory(final Class<T> beanType) {
		this(beanType, null);
	}

	public BeanComponentFactory(final Class<T> beanType, final String tab) {
		super(false, tab);
		this.beanType = beanType;
	}

	@Override
	public FieldComponent<T, JTextField> createComponent(final Resources rc, final C context) {
		return new BeanComponent<>(writable);
	}

	@Override
	public Class<T> getDataType() {
		return beanType;
	}

}