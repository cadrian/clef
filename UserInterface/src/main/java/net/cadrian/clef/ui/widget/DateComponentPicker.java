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
package net.cadrian.clef.ui.widget;

import java.awt.BorderLayout;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import net.cadrian.clef.ui.ApplicationContext;

class DateComponentPicker extends JDialog {

	private final class ClefToolsListenerImpl implements ClefTools.Listener {
		private final DateComponentPicker component;
		private final ZoneId zone;
		private final DateTimePicker picker;

		private ClefToolsListenerImpl(final DateComponentPicker component, final ZoneId zone,
				final DateTimePicker picker) {
			this.component = component;
			this.zone = zone;
			this.picker = picker;
		}

		@Override
		public void toolCalled(final ClefTools tools, final ClefTools.Tool tool) {
			switch (tool) {
			case Save:
				final LocalDateTime ldt = picker.getDateTimeStrict();
				component.date.set(Date.from(ldt.atZone(zone).toInstant()));
				component.setVisible(false);
				break;
			default:
			}
		}
	}

	private static final long serialVersionUID = 2728030026625833925L;

	private final AtomicReference<Date> date = new AtomicReference<>();

	DateComponentPicker(final ApplicationContext context, final Date date) {
		super(context.getPresentation().getApplicationFrame(), context.getPresentation().getMessage("DatePickerTitle"),
				true);
		this.date.set(date);

		final JPanel pickerPanel = new JPanel(new BorderLayout());
		getContentPane().add(pickerPanel);
		pickerPanel.add(new JLabel(context.getPresentation().getMessage("DatePickerMessage")), BorderLayout.NORTH);

		final ZoneId zone = ZoneId.systemDefault();
		final LocalDateTime ldt = date == null ? LocalDateTime.now() : LocalDateTime.ofInstant(date.toInstant(), zone);

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

		final ClefTools tools = new ClefTools(context, ClefTools.Tool.Save);
		tools.addListener(new ClefToolsListenerImpl(this, zone, picker));

		pickerPanel.add(tools, BorderLayout.SOUTH);

		pack();
		setLocationRelativeTo(context.getPresentation().getApplicationFrame());
	}

	Date getDate() {
		return date.get();
	}

}
