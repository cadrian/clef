/*
 * Copyright (C) 2018-2019 Cyril Adrian <cyril.adrian@gmail.com>
 *
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
 */
package net.cadrian.clef.ui.app.form.field.bean;

import java.util.Collection;

import javax.swing.JComponent;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.app.form.field.FieldSetter;

public class BeanComponentFactory<T extends Bean, D extends Bean>
		extends AbstractSimpleFieldComponentFactory<T, D, JComponent> {

	@FunctionalInterface
	public interface ListGetter<D extends Bean> {
		Collection<? extends D> getBeans(ApplicationContext context);
	}

	private final ListGetter<D> listGetter;

	public BeanComponentFactory(final Class<T> beanType, final String fieldName, final String tab,
			final ListGetter<D> listGetter) {
		super(beanType, fieldName, true, tab);
		this.listGetter = listGetter;
	}

	@Override
	protected FieldModel<T, D, JComponent> createModel(final String fieldName, final FieldGetter<T, D> getter,
			final FieldSetter<T, D> setter) {
		return new BeanFieldModel<>(fieldName, tab, getter, setter, this);
	}

	public ListGetter<D> getListGetter() {
		return listGetter;
	}

}
