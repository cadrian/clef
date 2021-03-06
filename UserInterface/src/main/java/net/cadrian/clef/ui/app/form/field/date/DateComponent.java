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

import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.ApplicationContext.ApplicationContextListener;
import net.cadrian.clef.ui.app.form.field.FieldComponent;
import net.cadrian.clef.ui.widget.DateSelector;

class DateComponent implements FieldComponent<Date, DateSelector> {

	private final class ApplicationContextListenerImpl implements ApplicationContextListener<Boolean> {
		private final boolean writable;

		private ApplicationContextListenerImpl(final boolean writable) {
			this.writable = writable;
		}

		@Override
		public void onAdvancedConfigurationChange(final AdvancedConfigurationEntry entry, final Boolean value) {
			component.getRefreshAction().setEnabled(writable || value);
			component.getSetDateAction().setEnabled(writable || value);
		}
	}

	private final ApplicationContext context;
	private final DateSelector component;
	private final ApplicationContextListener<Boolean> applicationContextListener;

	DateComponent(final ApplicationContext context, final boolean writable) {
		final boolean w = context.getValue(AdvancedConfigurationEntry.offlineMode);

		this.context = context;
		component = new DateSelector(context, writable || w);

		applicationContextListener = new ApplicationContextListenerImpl(writable);
		context.addApplicationContextListener(AdvancedConfigurationEntry.offlineMode, applicationContextListener);
	}

	@Override
	public void removed() {
		context.removeApplicationContextListener(AdvancedConfigurationEntry.offlineMode, applicationContextListener);
	}

	@Override
	public DateSelector getComponent() {
		return component;
	}

	@Override
	public Date getData() {
		final Date result = component.getDate();
		component.markSave();
		return result;
	}

	@Override
	public void setData(final Date data) {
		component.setDate(data);
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public boolean isDirty() {
		return component.isDirty();
	}

	public void setUpperBound(final DateComponent upperBound) {
		component.setUpperBound(upperBound.component);
	}

	public void setLowerBound(final DateComponent lowerBound) {
		component.setLowerBound(lowerBound.component);
	}

}
