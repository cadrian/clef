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
import net.cadrian.clef.model.ModelException;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.app.form.field.AbstractSimpleFieldModel;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.app.form.field.FieldComponentFactory;
import net.cadrian.clef.ui.app.form.field.FieldGetter;
import net.cadrian.clef.ui.app.form.field.FieldSetter;
import net.cadrian.clef.ui.widget.DateSelector;

class DateFieldModel<T extends Bean> extends AbstractSimpleFieldModel<T, Date, DateSelector> {

	private DateFieldModel<T> upperBound;
	private DateFieldModel<T> lowerBound;

	DateFieldModel(final String name, final String tab, final FieldGetter<T, Date> getter,
			final FieldSetter<T, Date> setter, final FieldComponentFactory<T, Date, DateSelector> componentFactory) {
		super(name, tab, getter, setter, componentFactory);
	}

	@Override
	public void created(final T contextBean, final ApplicationContext context,
			final FieldComponent<Date, DateSelector> component) {
		if (upperBound != null) {
			((DateComponent) component).setUpperBound((DateComponent) upperBound.createComponent(contextBean, context));
		}
		if (lowerBound != null) {
			((DateComponent) component).setLowerBound((DateComponent) lowerBound.createComponent(contextBean, context));
		}
	}

	@Override
	protected FieldComponent<Date, DateSelector> createNewComponent(final T contextBean,
			final ApplicationContext context) throws ModelException {
		return new DateComponent(context, componentFactory.isWritable());
	}

	void setUpperBound(final DateFieldModel<T> upperBound) {
		this.upperBound = upperBound;
	}

	void setLowerBound(final DateFieldModel<T> lowerBound) {
		this.lowerBound = lowerBound;
	}

}
