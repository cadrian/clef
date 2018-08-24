package net.cadrian.clef.ui.widget;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.DateTimePicker;
import com.github.lgooddatepicker.components.TimePickerSettings;

import net.cadrian.clef.ui.ApplicationContext;

public class DateSelector extends JPanel {

	private static final long serialVersionUID = 5669423531441161006L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DateSelector.class);

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

	private final JTextField display;
	private final Action refresh;
	private final Action setDate;
	private Date date;
	private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	private boolean dirty;

	public DateSelector(final ApplicationContext context, final boolean writable) {
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

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

		refresh.setEnabled(writable);
		setDate.setEnabled(writable);

		final JToolBar buttons = new JToolBar(SwingConstants.HORIZONTAL);
		buttons.setFloatable(false);
		buttons.add(refresh);
		buttons.add(setDate);

		add(display);
		add(context.getPresentation().awesome(buttons));
	}

	public Action getRefreshAction() {
		return refresh;
	}

	public Action getSetDateAction() {
		return setDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		dirty = false;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				display.setText(df.format(date));
			}
		});
	}

	public String getDateString() {
		return df.format(getDate());
	}

	public void setDateString(String date) {
		try {
			setDate(df.parse(date));
		} catch (ParseException e) {
			LOGGER.error("Error while setting date", e);
			setDate(new Date());
		}
	}

	public boolean isDirty() {
		if (dirty) {
			LOGGER.debug("dirty");
		}
		return dirty;
	}

	public void markSave() {
		dirty = false;
	}

}
