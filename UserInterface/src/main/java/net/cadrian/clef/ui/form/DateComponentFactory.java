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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

public class DateComponentFactory implements FieldComponentFactory<Date, JButton> {

	private static class DateComponent implements FieldComponent<Date, JButton> {

		private final JButton component;
		private Date date;
		private final DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

		DateComponent() {
			component = new JButton("***/***");
			component.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					date = new Date();
					component.setText(df.format(date));
				}
			});
		}

		@Override
		public JButton getComponent() {
			return component;
		}

		@Override
		public Date getData() {
			return date;
		}

		@Override
		public void setData(final Date data) {
			date = data;
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					component.setText(df.format(date));
				}
			});
		}

		@Override
		public double getWeight() {
			return 0;
		}

	}

	@Override
	public FieldComponent<Date, JButton> createComponent() {
		return new DateComponent();
	}

	@Override
	public Class<Date> getDataType() {
		return Date.class;
	}

}
