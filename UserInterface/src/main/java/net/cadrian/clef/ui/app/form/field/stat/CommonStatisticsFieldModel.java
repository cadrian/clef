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
package net.cadrian.clef.ui.app.form.field.stat;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldModel;

final class CommonStatisticsFieldModel<T extends Bean, D extends StatisticsData<T>>
		implements FieldModel<T, D, JPanel> {

	private final Constructor<D> componentConstructor;

	CommonStatisticsFieldModel(final Class<T> beanType, final Class<D> statisticsType) {
		try {
			componentConstructor = statisticsType.getConstructor(beanType);
		} catch (NoSuchMethodException | SecurityException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public FieldComponent<D, JPanel> createComponent(final T contextBean, final ApplicationContext context)
			throws ModelException {
		return new CommonStatisticsFieldComponent<>(context);
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public String getTab() {
		return "Statistics";
	}

	@Override
	public D load(final T bean) {
		try {
			return componentConstructor.newInstance(bean);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
			throw new ModelException(e);
		}
	}

	@Override
	public void save(final T bean, final D data) {
	}

}
