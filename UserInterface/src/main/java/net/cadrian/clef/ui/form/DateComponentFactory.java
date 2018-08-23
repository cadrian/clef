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

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import net.cadrian.clef.model.Bean;
import net.cadrian.clef.ui.ApplicationContext;
import net.cadrian.clef.ui.ApplicationContext.AdvancedConfigurationEntry;
import net.cadrian.clef.ui.ApplicationContext.ApplicationContextListener;

public class DateComponentFactory<C extends Bean> extends AbstractFieldComponentFactory<Date, JPanel, C> {

	private static class DateComponentPicker extends JDialog {

		private static final long serialVersionUID = 2728030026625833925L;

		private Date date;

		DateComponentPicker(final ApplicationContext context, final Date date) {
			super(context.getPresentation().getApplicationFrame(),
					context.getPresentation().getMessage("DatePickerTitle"), true);
			this.date = date;

			final JPanel pickerPanel = new JPanel(new BorderLayout());
			getContentPane().add(pickerPanel);
			pickerPanel.add(new JLabel(context.getPresentation().getMessage("DatePickerMessage")), BorderLayout.NORTH);

			final ZoneId zone = ZoneId.systemDefault();
			final LocalDateTime ldt = LocalDateTime.ofInstant(date.toInstant(), zone);

			final DatePickerSettings dateSettings = new DatePickerSettings();
			dateSettings.setFormatForDatesCommonEra("yyyy/MM/dd");
			dateSettings.setAllowEmptyDates(false);
			dateSettings.setWeekNumbersDisplayed(true, true);
			dateSettings.setFirstDayOfWeek(DayOfWeek.MONDAY);

			final TimePickerSettings timeSettings = new TimePickerSettings();
			timeSettings.setFormatForDisplayTime("HH:mm:ss");
			timeSettings.setAllowEmptyTimes(false);

			final DateTimePicker picker = new DateTimePicker(dateSettings, timeSettings);
			picker.setDateTimeStrict(ldt);

			final JButton datePickerButton = picker.getDatePicker().getComponentToggleCalendarButton();
			datePickerButton.setText("DatePicker");
			context.getPresentation().awesome(datePickerButton);
			final JButton timePickerButton = picker.getTimePicker().getComponentToggleTimeMenuButton();
			timePickerButton.setText("TimePicker");
			context.getPresentation().awesome(timePickerButton);

			pickerPanel.add(picker, BorderLayout.CENTER);

			final Action saveAction = new AbstractAction("Save") {
				private static final long serialVersionUID = -8659808353683696964L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					final LocalDateTime ldt = picker.getDateTimeStrict();
					DateComponentPicker.this.date = Date.from(ldt.atZone(zone).toInstant());
					DateComponentPicker.this.setVisible(false);
				}
			};

			final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
			buttons.setFloatable(false);
			buttons.add(saveAction);
			pickerPanel.add(context.getPresentation().awesome(buttons), BorderLayout.SOUTH);

			pack();
			setLocationRelativeTo(context.getPresentation().getApplicationFrame());
		}

		Date getDate() {
			return date;
		}

	}

	private static class DateComponent implements FieldComponent<Date, JPanel> {

		private final JPanel component;
		private final JTextField display;
		private final Action refresh;
		private final Action setDate;
		private Date date;
		private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		private boolean dirty;

		DateComponent(final ApplicationContext context, final boolean writable) {
			component = new JPanel();
			component.setLayout(new BoxLayout(component, BoxLayout.X_AXIS));

			display = new JTextField();
			display.setEditable(false);

			refresh = new AbstractAction("Refresh") {
				private static final long serialVersionUID = -3091414439330672834L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					date = new Date();
					display.setText(df.format(date));
					dirty = true;
				}

			};

			setDate = new AbstractAction("Calendar") {
				private static final long serialVersionUID = 7862670744497256762L;

				@Override
				public void actionPerformed(final ActionEvent e) {
					final DateComponentPicker picker = new DateComponentPicker(context, date);
					picker.setVisible(true);
					date = picker.getDate();
					display.setText(df.format(date));
					dirty = true;
				}

			};

			final boolean w = context.getValue(AdvancedConfigurationEntry.allowStartWrite);
			refresh.setEnabled(writable || w);
			setDate.setEnabled(writable || w);

			final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
			buttons.setFloatable(false);
			buttons.add(refresh);
			buttons.add(setDate);

			component.add(display);
			component.add(context.getPresentation().awesome(buttons));

			context.addApplicationContextListener(AdvancedConfigurationEntry.allowStartWrite,
					new ApplicationContextListener<Boolean>() {

						@Override
						public void onAdvancedConfigurationChange(final AdvancedConfigurationEntry entry,
								final Boolean value) {
							refresh.setEnabled(writable || value);
							setDate.setEnabled(writable || value);
						}
					});
		}

		@Override
		public JPanel getComponent() {
			return component;
		}

		@Override
		public Date getData() {
			dirty = false;
			return date;
		}

		@Override
		public void setData(final Date data) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					date = data;
					display.setText(df.format(date));
					dirty = false;
				}
			});
		}

		@Override
		public double getWeight() {
			return 0;
		}

		@Override
		public boolean isDirty() {
			return dirty;
		}

	}

	public DateComponentFactory(final boolean writable) {
		this(writable, null);
	}

	public DateComponentFactory(final boolean writable, final String tab) {
		super(writable, tab);
	}

	@Override
	public FieldComponent<Date, JPanel> createComponent(final ApplicationContext context, final C contextBean) {
		return new DateComponent(context, writable);
	}

	@Override
	public Class<Date> getDataType() {
		return Date.class;
	}

}
