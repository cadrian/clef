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
package net.cadrian.clef.ui.app.form.field;

import java.util.Date;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.ApplicationContext.ApplicationContextListener;
import net.cadrian.clef.ui.widget.DateSelector;

public class DateComponentFactory<C extends Bean> extends AbstractFieldComponentFactory<Date, DateSelector, C> {

	private static class DateComponent implements FieldComponent<Date, DateSelector> {

		private final DateSelector component;

		DateComponent(final ApplicationContext context, final boolean writable) {
			final boolean w = context.getValue(AdvancedConfigurationEntry.allowStartWrite);
			component = new DateSelector(context, writable || w);

			context.addApplicationContextListener(AdvancedConfigurationEntry.allowStartWrite,
					new ApplicationContextListener<Boolean>() {

						@Override
						public void onAdvancedConfigurationChange(final AdvancedConfigurationEntry entry,
								final Boolean value) {
							component.getRefreshAction().setEnabled(writable || value);
							component.getSetDateAction().setEnabled(writable || value);
						}
					});
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

	}

	public DateComponentFactory(final boolean writable) {
		this(writable, null);
	}

	public DateComponentFactory(final boolean writable, final String tab) {
		super(writable, tab);
	}

	@Override
	public FieldComponent<Date, DateSelector> createComponent(final ApplicationContext context, final C contextBean) {
		return new DateComponent(context, writable);
	}

	@Override
	public Class<Date> getDataType() {
		return Date.class;
	}

}
