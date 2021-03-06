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
package net.cadrian.clef.ui.app.form.field.date;

import java.util.Date;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldModel;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.widget.DateSelector;

public class DateComponentFactory<T extends Bean> extends AbstractSimpleFieldComponentFactory<T, Date, DateSelector> {

	private DateComponentFactory<T> upperBound;
	private DateComponentFactory<T> lowerBound;

	public DateComponentFactory(final Class<T> beanType, final String fieldName, final boolean writable) {
		this(beanType, fieldName, writable, null);
	}

	public DateComponentFactory(final Class<T> beanType, final String fieldName, final boolean writable,
			final String tab) {
		super(beanType, fieldName, writable, tab);
	}

	@Override
	protected FieldModel<T, Date, DateSelector> createModel(final String fieldName, final FieldGetter<T, Date> getter,
			final FieldSetter<T, Date> setter) {
		return new DateFieldModel<>(fieldName, tab, getter, setter, this);
	}

	@Override
	public void created(final FieldModel<T, Date, DateSelector> model) {
		if (upperBound != null) {
			((DateFieldModel<T>) model).setUpperBound((DateFieldModel<T>) AbstractSimpleFieldComponentFactory
					.<T, Date, DateSelector>getCachedModel(upperBound.getBeanType(), upperBound.getFieldName()));
		}
		if (lowerBound != null) {
			((DateFieldModel<T>) model).setLowerBound((DateFieldModel<T>) AbstractSimpleFieldComponentFactory
					.<T, Date, DateSelector>getCachedModel(lowerBound.getBeanType(), lowerBound.getFieldName()));
		}
	}

	public void setUpperBound(final DateComponentFactory<T> upperBound) {
		this.upperBound = upperBound;
	}

	public void setLowerBound(final DateComponentFactory<T> lowerBound) {
		this.lowerBound = lowerBound;
	}

}
