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

import java.text.ParseException;

import javax.swing.JFormattedTextField;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.cadrian.clef.tools.Converters;

public class DurationTextField extends JFormattedTextField {

	private static final class Formatter extends AbstractFormatter {
		private static final long serialVersionUID = -4277802724363308031L;

		@Override
		public Object stringToValue(final String text) throws ParseException {
			if (text != null && !text.isEmpty()) {
				final Long result = Converters.parseTime(text);
				LOGGER.debug("{} => {}", text, result);
				return result;
			}
			return Long.valueOf(0);
		}

		@Override
		public String valueToString(final Object value) throws ParseException {
			if (value != null) {
				if (value instanceof Long) {
					final String result = Converters.formatTime((Long) value).toString();
					LOGGER.debug("{} => {}", value, result);
					return result;
				}
				LOGGER.warn("Invalid duration value: {}", value);
			}
			return "";
		}
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(DurationTextField.class);

	private static final long serialVersionUID = 9181768552406022378L;

	private static final AbstractFormatter FORMAT = new Formatter();

	public DurationTextField() {
		super(FORMAT);
	}

}
