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

import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.ui.ApplicationContext;

public class DateSelector extends JPanel {

	private static final long serialVersionUID = 5669423531441161006L;

	private static final Logger LOGGER = LoggerFactory.getLogger(DateSelector.class);

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

	public void setDate(final Date date) {
		this.date = date;
		dirty = false;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				display.setText(date == null ? "" : df.format(date));
			}
		});
	}

	public String getDateString() {
		return df.format(getDate());
	}

	public void setDateString(final String date) {
		try {
			setDate(df.parse(date));
		} catch (final ParseException e) {
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
