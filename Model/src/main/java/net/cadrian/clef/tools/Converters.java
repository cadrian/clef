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
package net.cadrian.clef.tools;

import java.nio.charset.Charset;

public class Converters {

	private static final class TimeFormatter {
		private final long time;

		private TimeFormatter(final long time) {
			this.time = time;
		}

		@Override
		public String toString() {
			final StringBuilder result = new StringBuilder();
			long s = time;
			long m = s / 60L;
			s -= m * 60L;
			final long h = m / 60L;
			m -= h * 60L;
			if (h < 10L) {
				result.append("0");
			}
			result.append(h).append(":");
			if (m < 10L) {
				result.append("0");
			}
			result.append(m).append(":");
			if (s < 10L) {
				result.append("0");
			}
			result.append(s);
			return result.toString();
		}
	}

	public static final Charset CHARSET = Charset.forName("UTF-8");

	private Converters() {
		// no instance
	}

	public static final Object formatTime(final long time) {
		return new TimeFormatter(time);
	}

	public static final long parseTime(final String time) {
		final long result;
		final String[] split = time.split(":");
		switch (split.length) {
		case 0: { // seconds
			result = Long.parseLong(split[0]);
			break;
		}
		case 1: { // minutes and seconds
			final long s = Long.parseLong(split[1]);
			final long m = Long.parseLong(split[0]);
			result = m * 60L + s;
			break;
		}
		default: { // hours, minutes, and seconds -- ignore extra
			final long s = Long.parseLong(split[2]);
			final long m = Long.parseLong(split[1]);
			final long h = Long.parseLong(split[0]);
			result = h * 3600L + m * 60L + s;
		}
		}
		return result;
	}

}
