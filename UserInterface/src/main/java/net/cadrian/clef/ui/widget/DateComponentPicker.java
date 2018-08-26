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
package net.cadrian.clef.ui.widget;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import net.cadrian.clef.ui.ApplicationContext;

class DateComponentPicker extends JDialog {

	private static final long serialVersionUID = 2728030026625833925L;

	private Date date;

	DateComponentPicker(final ApplicationContext context, final Date date) {
		super(context.getPresentation().getApplicationFrame(), context.getPresentation().getMessage("DatePickerTitle"),
				true);
		this.date = date;

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
